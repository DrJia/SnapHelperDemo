package com.jiabin.snaphelperdemo;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


public class PubMusicScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager mLayoutManager;
    private boolean mScrolled = false;
    private int mCurrentPage = 0;
    private OnPageSelectedCallback mOnPageChangeCallback;
    private OrientationHelper mHorizontalHelper;

    public interface OnPageSelectedCallback {
        void onPageSelected(int currentPos, int lastPos);
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public LinearLayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public PubMusicScrollListener(LinearLayoutManager layoutManager, OnPageSelectedCallback onPageChangeCallback) {
        mLayoutManager = layoutManager;
        mOnPageChangeCallback = onPageChangeCallback;
        mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dx != 0 || dy != 0) {
            mScrolled = true;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
            mScrolled = false;
            final int lastPage = mCurrentPage;
//            final int firstAllVisiblePos = mLayoutManager.findFirstCompletelyVisibleItemPosition();
//            mCurrentPage = firstAllVisiblePos != RecyclerView.NO_POSITION ? firstAllVisiblePos : mLayoutManager.findFirstVisibleItemPosition();
            int firstChildPosition = mLayoutManager.findFirstVisibleItemPosition();
            if (firstChildPosition == RecyclerView.NO_POSITION) {
                mCurrentPage = lastPage;
            }else {
                View firstChildView = mLayoutManager.findViewByPosition(firstChildPosition);
                //如果第一个ItemView被遮住的长度没有超过一半，就取该ItemView作为snapView
                //超过一半，就把下一个ItemView作为snapView
                if (mHorizontalHelper.getDecoratedEnd(firstChildView) >= mHorizontalHelper.getDecoratedMeasurement(firstChildView) / 2 && mHorizontalHelper.getDecoratedEnd(firstChildView) > 0) {
                    mCurrentPage = firstChildPosition;
                } else {
                    mCurrentPage = firstChildPosition + 1;
                }
            }
            if (mCurrentPage != lastPage) {
                if (mOnPageChangeCallback != null) {
                    mOnPageChangeCallback.onPageSelected(mCurrentPage, lastPage);
                }
            }
        }
    }


}
