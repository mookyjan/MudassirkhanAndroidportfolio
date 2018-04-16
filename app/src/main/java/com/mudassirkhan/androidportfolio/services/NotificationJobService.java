package com.mudassirkhan.androidportfolio.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.tasks.NotificationTasks;

public class NotificationJobService extends JobService {

    //Declaration of the NotificationJobService's member variable
    private AsyncTask mBackgroundTask;

    //Method that starts the job
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        //We pass some info into a bundle, so that the notification can display it
        final Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.notification_data_bundle_picture_uri), null);
        bundle.putString(getString(R.string.notification_data_bundle_title), getString(R.string.job_scheduling_fragment_notification_title));
        bundle.putString(getString(R.string.notification_data_bundle_content), getString(R.string.job_scheduling_fragment_notification_content));

        //Our notification task will be executed in a background thread
        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Context context = NotificationJobService.this;

                //We execute a notification task, passing both the START_NOTIFICATION_TASK and the bundle we just created
                NotificationTasks.executeTask(context, NotificationTasks.START_NOTIFICATION_TASK, bundle);

                //When the notification has been launched, we unschedule the job
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NotificationJobService.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.job_scheduling_fragment_job_scheduled_sharedpref), false);
                editor.apply();

                return null;
            }

            //When the task is done, we notify the JobService
            @Override
            protected void onPostExecute(Object o) {
                jobFinished(jobParameters, false);
            }
        };

        //We start our AsyncTask
        mBackgroundTask.execute();
        return true;
    }

    //When the job stops, we cancel the background thread
    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) {
            mBackgroundTask.cancel(true);
        }
        return true;
    }
}
