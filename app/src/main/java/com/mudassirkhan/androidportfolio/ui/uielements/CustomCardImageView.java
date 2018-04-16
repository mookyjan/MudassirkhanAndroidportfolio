package com.mudassirkhan.androidportfolio.ui.uielements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

//Class that makes an ImageView spread through the available space
public class CustomCardImageView extends android.support.v7.widget.AppCompatImageView {

    //CustomCardImageView's first constructor
    public CustomCardImageView(Context context) {
        super(context);
    }


    //CustomCardImageView's second constructor
    public CustomCardImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    //CustomCardImageView's third constructor
    public CustomCardImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //Method called when the view needs to be measured. In this case, we set the width of the view to the available space
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
            setMeasuredDimension(width, height);
        } else super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

}



