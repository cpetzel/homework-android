package com.eazeup.eazehomework.ui;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.eazeup.eazehomework.EazeApplication;
import com.eazeup.eazehomework.R;
import com.eazeup.eazehomework.api.NetworkManager;
import com.eazeup.eazehomework.api.model.Gif;
import com.eazeup.eazehomework.ui.GifAdapter.ViewHolder;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Abstract class for showing a grid of Gifs.
 */
public abstract class AbstractGifGridActivity extends Activity {

    private static final String TAG = "AbstractGifGridActivity";

    //keeping track of the loading status from the activity to the adapter
    AtomicBoolean mIsLoading = new AtomicBoolean(false);

    @ColorInt
    @BindColor(R.color.colorAccent)
    int accent;

    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.gifRecyclerView)
    RecyclerView mGridView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    //adapter of gifs
    GifAdapter mGifAdapter;

    //the Unbinder to use when the activity is destroyed
    Unbinder mUnbinder;

    //the Network helper
    @Inject
    NetworkManager mNetworkManager;

    //holds onto all of this Activity's subscriptions. will dispose when the activity is destroyed
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    /**
     * The child Activities will decide which layout to use. (toolbars are different).
     *
     * todo let this activity define most of the shared stuff
     *
     * @return
     */
    @LayoutRes
    abstract int getLayoutRes();

    /**
     * Child Activities will define the toolbar title
     * @return
     */
    abstract String getToolbarTitle();

    /**
     * The adapter is asking for more items. You should refresh the data
     *
     * @param offset - how many items are already in the adapter
     */
    abstract void onLoadMoreItems(int offset);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutRes());

        EazeApplication.getInstance().getCoreComponent().inject(this);
        mUnbinder = ButterKnife.bind(this);

        mToolbar.setTitle(getToolbarTitle());
        setActionBar(mToolbar);

        setupGrid();
        setupTransitions();
        setupClicks();
    }

    /**
     * Glide will stop the Gif from playing during the activity transitions. this will restart it
     */
    void setupTransitions() {
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View>
                sharedElementSnapshots) {
                Log.d(TAG, "EXIT onSharedElementStart");
                //forcing a restart of the GifDrawable (the transition stopped it)
                for (View v : sharedElements) {
                    if (v instanceof ImageView) {
                        Drawable d = ((ImageView) v).getDrawable();
                        if (d instanceof GifDrawable) {
                            d.setVisible(true, false);
                        }
                    }
                }
            }
        });
    }

    /**
     * Subscribes to the gif click listener in the {@link GifAdapter} and will start the {@link GifViewActivity} once it is
     * clicked.
     *
     * note: this subscription will be disposed in {@link #onDestroy()}
     */
    void setupClicks() {
        mCompositeDisposable.add(mGifAdapter.getGifClickObservable().observeOn(AndroidSchedulers.mainThread()).subscribeWith
            (new DisposableObserver<Pair<ViewHolder, Gif>>() {
            @Override
            public void onNext(@NonNull Pair<ViewHolder, Gif> viewHolderGifPair) {
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(AbstractGifGridActivity.this, viewHolderGifPair
                    .first.image, viewHolderGifPair.second.getId()).toBundle();
                startActivity(GifViewActivity.getStartIntentForGif(AbstractGifGridActivity.this, viewHolderGifPair.second),
                    options);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError with gif click relay", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete with gif click relay stream");
            }
        }));
    }

    void setLoadingState(LoadingState state) {
        Log.d(TAG, "loadingState == " + state.name());
        switch (state) {
            case LOADING:
                mIsLoading.set(true);
                swipeRefreshLayout.setRefreshing(true);
                break;
            case IDLE:
            case ERROR:
                swipeRefreshLayout.setRefreshing(false);
                mIsLoading.set(false);
                break;
        }
    }

    /**
     * Initial call to set up the grid of gifs
     */
    private void setupGrid() {

        mGifAdapter = new GifAdapter(this, Collections.EMPTY_LIST);
        GridLayoutManager layoutManager = new GridLayoutManager(mGridView.getContext(), 2);
        mGridView.setLayoutManager(layoutManager);

        mGridView.scheduleLayoutAnimation();
        mGridView.addOnScrollListener(new InfiniteScrollListener(layoutManager, mIsLoading) {
            @Override
            public void onLoadMore() {
                onLoadMoreItems(mGifAdapter.getItemCount());
            }
        });

        swipeRefreshLayout.setColorSchemeColors(accent);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);

            if (mGifAdapter.getItemCount() < 1) {
                onLoadMoreItems(0);
                return;
            }
            //the adapter's scroll listener will try and load more once the items are cleared
            mGifAdapter.clear();
        });

        mGridView.setAdapter(mGifAdapter);
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

    /**
     * Exposing a method for the owners of this class to subscribe it to hook up the loading from network, to the adapter
     *
     * @return an {@link Observable} that will capture the list of Gifs and then put them into the adapter.
     */

    DisposableSubscriber<List<Gif>> gifReceivedObserver() {
        return new DisposableSubscriber<List<Gif>>() {
            @Override
            public void onNext(List<Gif> gifs) {
                mGifAdapter.addItems(gifs);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Error receiving the stream of Gifs", t);
                setLoadingState(LoadingState.ERROR);
            }

            @Override
            public void onComplete() {
                setLoadingState(LoadingState.IDLE);
            }
        };
    }
}
