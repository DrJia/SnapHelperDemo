package com.jiabin.snaphelperdemo;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public ItemDecoration(int space) {
        mSpace = space;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int count = layoutManager.getItemCount();
        int position = parent.getChildAdapterPosition(view);

        outRect.left = (int) (((float) (count - position)) / count * mSpace);
        outRect.right = (int) (((float) mSpace * (count + 1) / count) - outRect.left);
    }

}
