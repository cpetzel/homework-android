package com.eazeup.eazehomework.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.eazeup.eazehomework.R;
import com.eazeup.eazehomework.api.NetworkManager;

import org.reactivestreams.Subscription;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Entry {@link Activity} that shows a list of trending gifs
 */
public class TrendingActivity extends AbstractGifGridActivity {

    private static final String TAG = "TrendingActivity";

    @Override
    int getLayoutRes() {
        return R.layout.activity_trending;
    }

    @Override
    String getToolbarTitle() {
        return "Trending";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshData(0);
    }

    /**
     * The adapter is telling us to load more items
     *
     * @param offset - how many items are already in the adapter
     */
    @Override
    void onLoadMoreItems(int offset) {
        refreshData(offset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                View searchMenuView = mToolbar.findViewById(R.id.menu_search);
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(this, searchMenuView, getString(R.string
                    .transition_search_back)).toBundle();
                startActivity(new Intent(this, GifSearchActivity.class), options);
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mUnbinder.unbind();
        super.onDestroy();
    }

    /**
     * Will use the {@link NetworkManager} to hit our endpoint and forward the stream of Gifs into the adapter
     *
     * When the Activity is finished, the {@link Subscription} is disposed (which will automagically tear down all of the stream
     *
     * @param currentGifCount
     */
    void refreshData(int currentGifCount) {
        //@formatter:off
        mCompositeDisposable.add(mNetworkManager.getTrendingFromApi(currentGifCount)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(subscription-> {
                Log.d(TAG, "refreshData() onSubscribe currentGifCount = " + currentGifCount);
                setLoadingState(LoadingState.LOADING);
            }).subscribeWith(gifReceivedObserver()));
        //@formatter:on
    }

}
