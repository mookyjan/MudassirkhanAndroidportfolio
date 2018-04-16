package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import com.mudassirkhan.androidportfolio.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {


    //Triggered when the fragment is being created
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        //We add the Preference layout
        addPreferencesFromResource(R.xml.preferences);

        //We get a reference to the EditText Preference and set a change listener on it, so that we can check the entered value
        Preference editTextPreference = findPreference(getString(R.string.job_scheduling_fragment_job_scheduled_default_time));
        editTextPreference.setOnPreferenceChangeListener(this);

        //We get a reference to our Preference Screen and our Shared Preferences
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();

        int count = preferenceScreen.getPreferenceCount();

        //We iterate through the Preference Screen in order to set the initial summary to the List Preference and the EditText Preference
        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }



    /* Helper methods */


    //Method used to determine and set the summary for the List Preference and the EditText Preference
    private void setPreferenceSummary(Preference preference, String value) {
        //If the Preference passed in as a parameter is a List Preference, we set its summary
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        //If the Preference passed in as a parameter is an EditText Preference, we set its summary
        else if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }
    }




    /* Preferences & Shared Preferences callback */


    //Method triggered when the Shared Preferences change
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //We get a reference to the corresponding Preference
        Preference preference = findPreference(key);
        if (preference != null) {
            //If the Shared Preference that just changed is not a Checkbox, then we update the summary of its corresponding Preference
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }


    //Method triggered when a Preference changes (happens before it is saved into the Shared Preferences)
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        //We prepare the error toast, just in case
        Toast errorToast = Toast.makeText(getContext(), R.string.settings_fragment_number_validation, Toast.LENGTH_SHORT);

        //We make sure that the Preference that just changed is the EditText one, so that we can control the entered value
        String key = getString(R.string.job_scheduling_fragment_job_scheduled_default_time);
        if (preference.getKey().equals(key)) {

            String stringTime = ((String) (newValue)).trim();

            //If the EditText Preference is empty, we show a toast and do not save its value into the Shared Preferences
            if (stringTime.isEmpty()) {
                errorToast.show();
                return false;
            }

            try {
                //If the EditText Preference has a value superior than 9999, we show a toast and do not save its value into the Shared Preferences
                int time = Integer.parseInt(stringTime);
                if (time > 9999 || time < 1) {
                    errorToast.show();
                    return false;

                }
            } catch (NumberFormatException e) {
                errorToast.show();
                return false;
            }
        }

        //If everything is correct, we save the value into the Shared Preferences
        return true;
    }


    /* Fragment Lifecycle callbacks */


    //When the fragment is created, we register the OnSharedPreferenceChangeListener
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }


    //When the fragment is destroyed, we unregister the OnSharedPreferenceChangeListener
    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
