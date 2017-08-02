package com.eazeup.eazehomework.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * A scroll listener for RecyclerView to load more items as you approach the end.
 *
 * Adapted from https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
public abstract class InfiniteScrollListener extends RecyclerView.OnScrollListener {

    // The minimum number of items remaining before we should loading more.
    private static final int VISIBLE_THRESHOLD = 5;

    final AtomicBoolean mIsLoading;
    private final GridLayoutManager mGridLayoutManager;

    //todo pass in some relay or oversable that tells me if I am loading anything or not
    public InfiniteScrollListener(@NonNull GridLayoutManager layoutManager, AtomicBoolean isLoadingAtomicBoolean) {
        mGridLayoutManager = layoutManager;
        mIsLoading = isLoadingAtomicBoolean;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        // bail out if scrolling upward or already loading data
        if (dy < 0 || mIsLoading.get()) {
            return;
        }

        final int visibleItemCount = recyclerView.getChildCount();
        final int totalItemCount = mGridLayoutManager.getItemCount();
        final int firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();

        if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
            onLoadMore();
        }
    }

    public abstract void onLoadMore();

}
