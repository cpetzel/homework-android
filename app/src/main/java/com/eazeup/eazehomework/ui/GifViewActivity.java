package com.eazeup.eazehomework.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.eazeup.eazehomework.R;
import com.eazeup.eazehomework.api.model.Gif;
import com.eazeup.eazehomework.ui.widget.ElasticDragDismissFrameLayout;
import com.eazeup.eazehomework.ui.widget.ElasticDragDismissFrameLayout.ElasticDragDismissCallback;
import com.eazeup.eazehomework.util.AnimUtils.TransitionEndListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.droidsonroids.gif.GifImageView;

/**
 * {@link Activity} to view a gif from Giphy
 */
public class GifViewActivity extends Activity {

    static final String KEY_GIF = "gif";
    private static final String TAG = "GifViewActivity";

    //delaying the reveal of the gif until a bit after the transition has ended for visual integrity
    public static final int DISPLAY_THRESHOLD = 300;

    Unbinder mUnbinder;

    @BindView(R.id.detailProgressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.gifDetailStatic)
    ImageView mStaticImageView;

    @BindView(R.id.gifDetailAnimated)
    GifImageView mGifImageView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.elastricDragContainer)
    ElasticDragDismissFrameLayout mElasticDragDismissFrameLayout;

    //the Gif to use
    Gif mGif;

    //keeping track of when the transition time ended
    long transitionEndTime;

    public static Intent getStartIntentForGif(Context c, Gif gif) {
        Intent i = new Intent(c, GifViewActivity.class);
        i.putExtra(KEY_GIF, gif);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGif = getIntent().getParcelableExtra(KEY_GIF);

        setContentView(R.layout.activity_gif_detail);
        mUnbinder = ButterKnife.bind(this);

        mToolbar.setTitle(mGif.getShareUrl());
        setActionBar(mToolbar);

        mStaticImageView.setVisibility(View.VISIBLE);
        mGifImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mElasticDragDismissFrameLayout.addListener(dragDismissListener);

        mStaticImageView.setTransitionName(mGif.getId());

        //wait to perform the transition until we have loaded the preview image
        postponeEnterTransition();

        setupTransition();

    }

    void setupTransition() {
        Transition sharedElementEnterTransition = getWindow().getSharedElementEnterTransition();
        sharedElementEnterTransition.addListener(new TransitionEndListener() {

            @Override
            public void onTransitionCompleted(Transition transition) {
                Log.d(TAG, "onTransitionCompleted. will load gif");
                transitionEndTime = System.currentTimeMillis();

                mProgressBar.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).asGif().load(mGif.getDownsizeMediumGifUrl()).listener(new RequestListener<GifDrawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean
                        isFirstResource) {
                        Toast.makeText(GifViewActivity.this, "Error loading Gif. Please try again later", Toast.LENGTH_LONG)
                            .show();
                        finishAfterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource
                        dataSource, boolean isFirstResource) {
                        Log.d(TAG, "main gif loaded from " + dataSource);

                        //reset the transition for the pop
                        mGifImageView.setTransitionName(mGif.getId());

                        long loadDiff = System.currentTimeMillis() - transitionEndTime;
                        if (loadDiff < DISPLAY_THRESHOLD) {
                            long timeToWait = DISPLAY_THRESHOLD - loadDiff;
                            Log.d(TAG, "gif loaded too fast. Will wait to swap views after this many seconds : " + timeToWait);
                            mGifImageView.postDelayed(gifLoadedRunnable, timeToWait);
                        } else {
                            mGifImageView.post(gifLoadedRunnable);
                        }
                        return false;
                    }
                }).into(mGifImageView);
            }
        });

        //first load the preview image
        final RequestOptions staticOption = new RequestOptions().dontAnimate();
        Glide.with(getApplicationContext()).asBitmap().apply(staticOption).load(mGif.getImageUrlSmall()).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean
                isFirstResource) {
                Toast.makeText(GifViewActivity.this, "Error loading Gif. Please try again later", Toast.LENGTH_LONG).show();
                finishAfterTransition();
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean
                isFirstResource) {
                Log.d(TAG, "static image ready. will start transition");
                startPostponedEnterTransition();
                return false;
            }
        }).into(mStaticImageView);
    }

    /**
     * used to set visibility on views once the gif has loaded
     */
    private Runnable gifLoadedRunnable = new Runnable() {
        @Override
        public void run() {
            mProgressBar.setVisibility(View.INVISIBLE);
            mStaticImageView.setVisibility(View.INVISIBLE);
            mGifImageView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, mGif.getShareUrl()));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        mGifImageView.removeCallbacks(gifLoadedRunnable);
        Log.d(TAG, "onDestroy");
        Glide.with(getApplicationContext()).clear(mStaticImageView);
        Glide.with(getApplicationContext()).clear(mGifImageView);
        mElasticDragDismissFrameLayout.removeListener(dragDismissListener);

        mUnbinder.unbind();
        super.onDestroy();
    }

    private final ElasticDragDismissCallback dragDismissListener = new ElasticDragDismissCallback() {
        @Override
        public void onDragDismissed() {
            finishAfterTransition();
        }
    };

}
