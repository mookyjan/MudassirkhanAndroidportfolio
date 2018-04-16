package com.mudassirkhan.androidportfolio.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtils {

    //Static method that returns the number of columns necessary for our movie GridLayouts, depending of the screen width
    public static int getNumberOfCarViewColumnsDependingOnScreenWidth(Context context) {

        int numberOfColumns = 3;
        int screenWidth = getScreenWidth(context);

        if (screenWidth < 1200) {
            numberOfColumns = 3;
        } else if (screenWidth >= 1200 && screenWidth < 1536) {
            numberOfColumns = 4;
        } else if (screenWidth >= 1536) {
            numberOfColumns = 5;
        }
        return numberOfColumns;
    }

    //Static method that returns the screen width of the device, in pixels
    private static int getScreenWidth(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int screenWidth = size.x;

        if (screenWidth == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            screenWidth = metrics.widthPixels;
        }
        return screenWidth;
    }

}