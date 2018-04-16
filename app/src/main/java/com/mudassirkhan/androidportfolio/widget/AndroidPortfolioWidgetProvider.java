package com.mudassirkhan.androidportfolio.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.ui.activities.MainActivity;
import com.mudassirkhan.androidportfolio.ui.activities.PortfolioItemsActivity;
import com.mudassirkhan.androidportfolio.ui.fragments.ContentProviderBisFragment;

import java.util.Calendar;

public class AndroidPortfolioWidgetProvider extends AppWidgetProvider {

    //Static method that allows us to update our Widget's instances
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, boolean methodCalledByMainActivity) {

        //We get the current width of our Widget, in dips
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        RemoteViews remoteViews;

        //If it is lower than 200 dips, then we show the Application's icon and the last time the App was started
        if (width < context.getResources().getInteger(R.integer.widget_min_width_for_displaying_icon)) {

            remoteViews = getPlainRemoteView(context, methodCalledByMainActivity);

        } else { //If it is greater than or equal to 200 dips, then we show a ListView containing the Portfolio Items

            remoteViews = getPortfolioListItemRemoteView(context);
        }

        //We update the Widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }


    //Static method that returns the RemoteViews object used when the Widget width is lower than 200 dips
    private static RemoteViews getPlainRemoteView(Context context, boolean methodCalledByMainActivity) {

        String finalDateAndTimeString = null;

        //If the Widget is updated by the user, opening the App, then we get the current time
        if (methodCalledByMainActivity) {

            finalDateAndTimeString = getFinalTimeString(Calendar.getInstance());

        }
        //If the Widget is updated automatically, every xxx ms or by modifying it, then we get the last time is was started, from the Shared Preferences
        else {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            long lastTimeMillis = sharedPreferences.getLong(context.getString(R.string.widget_provider_last_time_sharedpref), 0);

            if (lastTimeMillis != 0) {

                Calendar lastTime = Calendar.getInstance();
                lastTime.setTimeInMillis(lastTimeMillis);

                finalDateAndTimeString = getFinalTimeString(lastTime);

            } else {
                finalDateAndTimeString = context.getString(R.string.widget_provider_app_never_been_opened);
            }

        }

        //We pass the appropriate time to the RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.android_portfolio_widget_provider);
        views.setTextViewText(R.id.widget_last_time_textview, finalDateAndTimeString);

        //We pass a Pending Intent to the RemoteViews, so that the user will be able to open the App by clicking on the Widget
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_picture_imageview, mainActivityPendingIntent);

        return views;
    }


    //Static method that returns the RemoteViews object used when the Widget width is greater than or equal to 200 dips
    private static RemoteViews getPortfolioListItemRemoteView(Context context) {

        //We bind the RemoteViews to the ListView and its corresponding adapter
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.android_portfolio_widget_provider_list);
        Intent intent = new Intent(context, WidgetListService.class);
        remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);

        //We make the widget's list items clickable, using a PendingIntentTemplate
        Intent portfolioItemActivityIntent = new Intent(context, PortfolioItemsActivity.class);

        //Adding a back stack to our Intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PortfolioItemsActivity.class);
        stackBuilder.addNextIntent(portfolioItemActivityIntent);

        //Creating the PendingIntent
        PendingIntent portfolioItemActivityPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_list_view, portfolioItemActivityPendingIntent);

        return remoteViews;
    }


    //Method triggered by the system, every xxx ms to update all the instantiated Widgets, depending on the time period chosen by the developer
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateWidgets(context, appWidgetManager, appWidgetIds, false);
    }


    //Method triggered by the system, every time a Widget gets modified
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId, false);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }


    //Static method called by the onUpdate method that will loop through all the instantiated Widget and update them
    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, boolean methodCalledByMainActivity) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, methodCalledByMainActivity);
        }
    }


    //Helper method that returns the appropriate time in a string format
    private static String getFinalTimeString(Calendar calendar) {

        String lastDateAppWasOpen = ContentProviderBisFragment.getStringDate(calendar);

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String minuteString = Integer.toString(minute);
        if (minuteString.length() == 1) {
            minuteString = "0" + minuteString;
        }

        String lastTimeAppWasOpen = hourOfDay + ":" + minuteString;

        return lastDateAppWasOpen + ", " + lastTimeAppWasOpen;
    }


    //Static method that allows us to update the time displayed in the Widget from the App's activities
    public static void updateLastConnection(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getString(R.string.widget_provider_last_time_sharedpref), Calendar.getInstance().getTimeInMillis());
        editor.apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, AndroidPortfolioWidgetProvider.class));
        updateWidgets(context, appWidgetManager, appWidgetIds, true);
    }


    //Not useful in our case
    @Override
    public void onEnabled(Context context) {
    }


    //Not useful in our case
    @Override
    public void onDisabled(Context context) {
    }

}

