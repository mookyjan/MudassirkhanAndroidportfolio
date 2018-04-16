package com.mudassirkhan.androidportfolio.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.mudassirkhan.androidportfolio.tasks.NotificationTasks;

public class NotificationIntentService extends IntentService {

    //NotificationIntentService's constructor
    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    //NotificationIntentService's main method, which will run in a background thread
    @Override
    protected void onHandleIntent(Intent intent) {

        //We get the action and the extra bundle from the intent
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();

        //We execute a notification task, passing both the action and the intent we just got
        NotificationTasks.executeTask(this, action, bundle);
    }
}