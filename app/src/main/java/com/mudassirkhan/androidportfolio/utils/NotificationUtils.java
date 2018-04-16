package com.mudassirkhan.androidportfolio.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.services.NotificationIntentService;
import com.mudassirkhan.androidportfolio.tasks.NotificationTasks;

import java.util.Calendar;

public class NotificationUtils {

    //Declaration of a constant integer used as an id for our notification
    private static final int NOTIFICATION_ID = 12345;

    //Declaration of a constant integer used as an id for our System Integration Activity Pending Intent
    private static final int SYSTEM_INTEGRATION_ACTIVITY_PENDING_INTENT_ID = 118218;

    //Declaration of a constant integer used as an id for our Dismiss Notifications Pending Intent
    private static final int DISMISS_NOTIFICATIONS_PENDING_INTENT_ID = 13579;


    //Static method that starts our notification
    public static void showCustomNotification(Context context, Bundle bundle) {

        //We retrieve the large icon uri string passed in in the bundle from the activity
        String largeIconUriString = bundle.getString(context.getString(R.string.notification_data_bundle_picture_uri));

        //We retrieve the title passed in in the bundle from the activity. If there is nothing, we will display a default title
        String notificationTitle = bundle.getString(context.getString(R.string.notification_data_bundle_title));
        if (notificationTitle == null || notificationTitle.isEmpty()) {
            notificationTitle = context.getString(R.string.notifications_fragment_no_custom_title);
        }

        //We retrieve the content passed in in the bundle from the activity. If there is nothing, we will display a default content
        String notificationContent = bundle.getString(context.getString(R.string.notification_data_bundle_content));
        if (notificationContent == null || notificationContent.isEmpty()) {
            notificationContent = context.getString(R.string.notifications_fragment_no_custom_content);
        }

        //We build our notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        notificationBuilder.setSmallIcon(R.mipmap.gm_icon);
        notificationBuilder.setLargeIcon(ImageUtils.getResizedCircleBitmap(context, largeIconUriString, R.drawable.mudassir_khan_pic));
        notificationBuilder.setContentTitle(notificationTitle);
        notificationBuilder.setContentText(notificationContent);
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationBuilder.setContentIntent(startSystemIntegrationActivityPendingIntent(context));
        notificationBuilder.addAction(goToSystemIntegrationActivityAction(context));
        notificationBuilder.addAction(dismissNotificationsAction(context));
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

        //We get a reference to the Notification Manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //We launch our notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        //If the notification was started by the Job scheduled in the Job Scheduling fragment, we change the fragment's state
        if (notificationTitle.equals(context.getString(R.string.job_scheduling_fragment_notification_title))) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(context.getString(R.string.job_scheduling_fragment_job_scheduled_sharedpref), false);
            editor.apply();
        }
    }


    //Static method used to clear all notifications from our App
    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    //Static method used to create the Pending Intent that will start our System Integration Activity
    private static PendingIntent startSystemIntegrationActivityPendingIntent(Context context) {
        //We create an Intent that will start our NotificationIntentService
        Intent startSystemIntegrationActivityIntent = new Intent(context, NotificationIntentService.class);
        //We pass in the chosen action, which will start the System Integration Activity in this case
        startSystemIntegrationActivityIntent.setAction(NotificationTasks.LAUNCH_SYSTEM_INTEGRATION_ACTIVITY_TASK);
        //We return our Pending Intent
        return PendingIntent.getService(
                context,
                SYSTEM_INTEGRATION_ACTIVITY_PENDING_INTENT_ID,
                startSystemIntegrationActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    //Static method used to create the action from the Pending Intent created just above
    private static NotificationCompat.Action goToSystemIntegrationActivityAction(Context context) {
        return new NotificationCompat.Action(R.mipmap.home_icon,
                context.getString(R.string.notifications_fragment_go_to_app_action),
                startSystemIntegrationActivityPendingIntent(context));
    }


    //Static method used to create the action that will dismiss all notifications from our App
    private static NotificationCompat.Action dismissNotificationsAction(Context context) {
        //We create an Intent that will start our NotificationIntentService
        Intent dismissNotificationsIntent = new Intent(context, NotificationIntentService.class);
        //We pass in the chosen action, which will dismiss all the notifications from our App in this case
        dismissNotificationsIntent.setAction(NotificationTasks.DISMISS_NOTIFICATION_TASK);
        //We create the corresponding Pending Intent
        PendingIntent dismissNotificationsPendingIntent = PendingIntent.getService(
                context,
                DISMISS_NOTIFICATIONS_PENDING_INTENT_ID,
                dismissNotificationsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //We return the corresponding action
        return new NotificationCompat.Action(R.mipmap.cancel_notification_icon,
                context.getString(R.string.notifications_fragment_dismiss_action),
                dismissNotificationsPendingIntent);
    }


    //Static method that will be used to schedule a not recurring job, from the Job Scheduling fragment
    public static void scheduleNotificationJob(final Context context, long time) {

        //We get the current time
        Calendar jobTime = Calendar.getInstance();

        //We add the amount of seconds chosen by the user to it
        jobTime.add(Calendar.SECOND, (int) time);

        //We get the PendingIntent that we will use to start our notification
        PendingIntent pendingIntent = getJobSchedulingPendingIntent(context);

        //We get a reference to the AlarmManager
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //We set the alarm
        manager.setExact(AlarmManager.RTC, jobTime.getTimeInMillis(), pendingIntent);
    }


    //Static method that will be used to unschedule the job from the Job scheduling fragment
    public static void unscheduleNotificationJob(final Context context) {

        //We get a reference to the AlarmManager
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //We get a PendingIntent similar to the one used to schedule the job
        PendingIntent pendingIntent = getJobSchedulingPendingIntent(context);

        //We cancel the alarm
        manager.cancel(pendingIntent);
    }


    //Static method used to create the Pending Intent that will start our notification from the Job scheduled
    private static PendingIntent getJobSchedulingPendingIntent(Context context) {

        //We pass some info into a bundle, so that the notification can display it
        final Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.notification_data_bundle_picture_uri), null);
        bundle.putString(context.getString(R.string.notification_data_bundle_title), context.getString(R.string.job_scheduling_fragment_notification_title));
        bundle.putString(context.getString(R.string.notification_data_bundle_content), context.getString(R.string.job_scheduling_fragment_notification_content));

        //We create the Intent that will start our Notification Intent Service
        Intent startNotificationServiceIntent = new Intent(context, NotificationIntentService.class);

        //We pass the bundle to it
        startNotificationServiceIntent.putExtras(bundle);

        //We pass the action to it
        startNotificationServiceIntent.setAction(NotificationTasks.START_NOTIFICATION_TASK);

        //We return the PendingIntent
        return PendingIntent
                .getService(context, 0, startNotificationServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
