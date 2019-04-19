package com.jiabin.snaphelperdemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;

public class RecyclerResizeManager implements ViewTreeObserver.OnGlobalLayoutListener {

    private RecyclerView mRecyclerView;
    private PubMusicAdapter mAdapter;

    public RecyclerResizeManager(@NonNull RecyclerView recyclerView, @NonNull PubMusicAdapter adapter) {
        this.mRecyclerView = recyclerView;
        this.mAdapter = adapter;
    }

    public void addListener() {
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void removeListener() {
        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        mAdapter.setRecyclerViewWidth(mRecyclerView.getWidth());
    }
}
