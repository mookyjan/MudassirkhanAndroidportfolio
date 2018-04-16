package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.utils.NotificationUtils;

public class JobSchedulingFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    //Declaration of the fragment's views' member variables
    private View mFragmentRootView;
    private EditText mTimeEditText;
    private CardView mJobCardViewButton;
    private TextView mJobCardViewTextView;

    //Declaration of the Shared Preferences' member variable
    private SharedPreferences mSharedPreferences;

    //Declaration of the Job related member variable
    private boolean mIsJobAlreadyScheduled;


    //Empty constructor
    public JobSchedulingFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_job_scheduling, container, false);

        //Setting up the fragment's views
        setUpViews();

        //Setting up the activity's accent color on the fragment, so that we can paint our button
        setCustomColorOnFragment();

        //Setting up the job initial state
        settingJobInitialState();

        return mFragmentRootView;
    }



    /* Setting up the Fragment */


    //Setting up the fragment's views
    private void setUpViews() {
        //Getting the fragment's views's references
        mTimeEditText = (EditText) mFragmentRootView.findViewById(R.id.job_scheduling_fragment_time_edittext);
        mJobCardViewButton = (CardView) mFragmentRootView.findViewById(R.id.job_scheduling_fragment_cardview_button);
        mJobCardViewTextView = (TextView) mFragmentRootView.findViewById(R.id.job_scheduling_fragment_cardview_textview);

        //Setting a click listener on the button
        mJobCardViewButton.setOnClickListener(this);
    }


    //Setting up the activity's accent color on the fragment, so that we can paint our button
    private void setCustomColorOnFragment() {

        //Getting the color in which we will paint the button
        Intent intent = getActivity().getIntent();
        int activityAccentColor = ContextCompat.getColor(getActivity(), intent.getIntExtra(
                getActivity().getString(R.string.main_from_detail_activity_background_color_tag), 0));

        //We paint the button
        mJobCardViewButton.setCardBackgroundColor(activityAccentColor);
    }


    //Setting up the job initial state
    private void settingJobInitialState() {

        //We get a reference to the Shared Preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //If the job is already scheduled, we act upon it and set the fragment's different views accordingly
        if (mSharedPreferences.getBoolean(getString(R.string.job_scheduling_fragment_job_scheduled_sharedpref), false)) {
            setJobStateToAlreadyScheduled();
            mTimeEditText.setText(Long.toString(mSharedPreferences.getLong(getString(R.string.job_scheduling_fragment_job_scheduled_delay), 0)));
        }
        //If the job is not already scheduled, we act upon it and set the fragment's different views accordingly
        else {
            setJobStateToNotScheduled();
        }
    }




    /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //When clicked on the Job button
            case R.id.job_scheduling_fragment_cardview_button:

                //If the job is already scheduled, we unschedule it
                if (mIsJobAlreadyScheduled) {
                    unscheduleJob();
                }
                //If the job is not already scheduled, we schedule it
                else {
                    scheduleJob();
                }
                break;
        }
    }




    /* Helper methods */


    //Method used to schedule a Job
    private void scheduleJob() {

        //First of all, we make sure that the EditText has a value
        if (mTimeEditText.getText().toString() == null || mTimeEditText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), R.string.job_scheduling_fragment_enter_value, Toast.LENGTH_SHORT).show();
            return;
        }

        //Then, we make sure that its value if lower than 10000
        if (mTimeEditText.getText().toString().length() > 4) {
            Toast.makeText(getActivity(), R.string.job_scheduling_fragment_value_range, Toast.LENGTH_SHORT).show();
            return;
        }

        //If everything is fine, we schedule the Job thanks to the scheduleNotificationJob method from the NotificationUtils class
        Long time = Long.parseLong(mTimeEditText.getText().toString());
        NotificationUtils.scheduleNotificationJob(getActivity(), time);

        //We change the corresponding flag boolean in the Shared Preferences
        modifyJobSchedulingSharedPreference(true, time);

        //We set the different views accordingly
        setJobStateToAlreadyScheduled();

        //And finally, we show a toast to the user
        Toast.makeText(getActivity(),
                getResources().getString(R.string.job_scheduling_fragment_job_scheduled_toast, time),
                Toast.LENGTH_SHORT).show();
    }


    //Method used to unschedule a Job
    private void unscheduleJob() {

        //We unschedule the Job thanks to the unscheduleNotificationJob method from the NotificationUtils class
        NotificationUtils.unscheduleNotificationJob(getActivity());

        //We change the corresponding flag boolean in the Shared Preferences
        modifyJobSchedulingSharedPreference(false, 0);

        //We set the different views accordingly
        setJobStateToNotScheduled();

        //And finally, we show a toast to the user
        Toast.makeText(getActivity(), R.string.job_scheduling_fragment_job_canceled, Toast.LENGTH_SHORT).show();
    }


    //Method used to set the default value, from the corresponding Shared Preference, to the EditText
    private void setDefaultValueToEditText() {
        //We get the value from the Shared Preferences
        String defaultTimeString = mSharedPreferences.getString(
                getString(R.string.job_scheduling_fragment_job_scheduled_default_time),
                Integer.toString(getResources().getInteger(R.integer.settings_edittext_default_value)));
        //If it is not null, we set it to the EditText
        if (defaultTimeString != null) {
            mTimeEditText.setText(defaultTimeString);
        }
    }


    //Method used to modify the Job scheduling related Shared Preferences
    private void modifyJobSchedulingSharedPreference(boolean isJobScheduled, long delay) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(getString(R.string.job_scheduling_fragment_job_scheduled_sharedpref), isJobScheduled);
        editor.putLong(getString(R.string.job_scheduling_fragment_job_scheduled_delay), delay);
        editor.apply();
    }


    //Method used to set the fragment's views to the scheduled state
    private void setJobStateToAlreadyScheduled() {
        mJobCardViewTextView.setText(R.string.job_scheduling_fragment_cancel_job);
        mIsJobAlreadyScheduled = true;
        mTimeEditText.setEnabled(false);
    }


    //Method used to set the fragment's views to the unscheduled state
    private void setJobStateToNotScheduled() {
        mJobCardViewTextView.setText(R.string.job_scheduling_fragment_start_job);
        mIsJobAlreadyScheduled = false;
        mTimeEditText.setEnabled(true);
        setDefaultValueToEditText();
    }



    /* Fragment Lifecycle callbacks */


    //When the fragment is created, we register the Shared Preferences Change Listener
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }


    //When the fragment is destroyed, we unregister the Shared Preferences Change Listener
    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }



    /* Shared Preferences callback */


    //Method triggered when the Shared Preferences change
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //We check if the Shared Preference that changed is the Job scheduling default time
        if (key.equals(getString(R.string.job_scheduling_fragment_job_scheduled_default_time))) {
            //If that is the case, and the Job is not already scheduled, we set the default value to the EditText
            if (!mIsJobAlreadyScheduled) {
                setDefaultValueToEditText();
            }
        }
        //We check if the Shared Preference that changed is the Job scheduling "Job scheduled" one
        else if(key.equals(getString(R.string.job_scheduling_fragment_job_scheduled_sharedpref))){
            if (mIsJobAlreadyScheduled) {
                //If that is the case, if the job is scheduled, we set the "Not scheduled" state
                setJobStateToNotScheduled();
            }
        }
    }
}
