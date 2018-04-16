package com.mudassirkhan.androidportfolio.ui.uielements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mudassirkhan.androidportfolio.R;

//Class used to define a separator for the RecyclerViews
public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    //Declaration of RecyclerViewItemDecoration's member variables
    private Context mContext;
    private Drawable mDivider;


    //RecyclerViewItemDecoration's constructor
    public RecyclerViewItemDecoration(Context context, int resId) {
        mContext = context;
        mDivider = ContextCompat.getDrawable(context, resId);
    }


    //Method that draws the separator
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int left = mContext.getResources().getInteger(R.integer.recyclerviews_separator_padding_left);
        int right = parent.getWidth() - mContext.getResources().getInteger(R.integer.recyclerviews_separator_padding_right);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
