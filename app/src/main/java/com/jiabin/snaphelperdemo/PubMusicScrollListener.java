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
            int firstChildPosition = mLayoutManager.findFirstVisibleItemPosition();
            if (firstChildPosition == RecyclerView.NO_POSITION) {
                mCurrentPage = lastPage;
            }else {
                View firstChildView = mLayoutManager.findViewByPosition(firstChildPosition);
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

    public void setPageSelected(int curPage){
        if (mOnPageChangeCallback != null) {
            mOnPageChangeCallback.onPageSelected(curPage, curPage);
        }
    }


}
