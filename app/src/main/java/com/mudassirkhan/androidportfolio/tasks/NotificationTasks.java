package com.mudassirkhan.androidportfolio.tasks;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.ui.activities.PortfolioItemsActivity;
import com.mudassirkhan.androidportfolio.utils.DataUtils;
import com.mudassirkhan.androidportfolio.utils.NotificationUtils;

public class NotificationTasks {

    //Declaration of NotificationTasks's constant strings, that will be used as action flags, from other classes, to start a notification task
    public static final String START_NOTIFICATION_TASK = "start-notification";
    public static final String DISMISS_NOTIFICATION_TASK = "dismiss-notification";
    public static final String LAUNCH_SYSTEM_INTEGRATION_ACTIVITY_TASK = "launch-system-integration-activity";

    //Method that will execute a notification related task, according to the action passed, and which will be called from other classes
    public static void executeTask(Context context, String action, Bundle bundle) {
        if (START_NOTIFICATION_TASK.equals(action)) {
            NotificationUtils.showCustomNotification(context, bundle);
        } else if (DISMISS_NOTIFICATION_TASK.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        } else if (LAUNCH_SYSTEM_INTEGRATION_ACTIVITY_TASK.equals(action)) {
            startSystemIntegrationActivity(context);
        }
    }

    //Static method that will be used to launch the System Integration Activity from the notification
    private static void startSystemIntegrationActivity(Context context) {

        //First of all, we clear all the open notifications from our App
        NotificationUtils.clearAllNotifications(context);

        //We pass the required information to the intent, in order to launch the desired activity
        Intent startSystemIntegrationActivityIntent = new Intent(context, PortfolioItemsActivity.class);
        for (int i = 0; i < DataUtils.PORTFOLIO_ITEMS_LIST.size(); i++) {
            if (DataUtils.PORTFOLIO_ITEMS_LIST.get(i).getTitle().equals(context.getString(R.string.portfolio_item_system_integration_title))) {
                startSystemIntegrationActivityIntent.putExtra(context.getString(R.string.main_from_detail_activity_title_tag), DataUtils.PORTFOLIO_ITEMS_LIST.get(i).getTitle());
                startSystemIntegrationActivityIntent.putExtra(context.getString(R.string.main_from_detail_activity_icon_tag), DataUtils.PORTFOLIO_ITEMS_LIST.get(i).getIcon());
                startSystemIntegrationActivityIntent.putExtra(context.getString(R.string.main_from_detail_activity_background_color_tag), DataUtils.PORTFOLIO_ITEMS_LIST.get(i).getIconBackgroundColor());
                startSystemIntegrationActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }
        }

        //We use a TaskStackBuilder in order to simulate a backstack, used for the navigation from the started activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(startSystemIntegrationActivityIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //We execute the PendingIntent we just created
        try {
            resultPendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}