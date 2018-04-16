package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mudassirkhan.androidportfolio.R;

public class SharedPreferencesFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences mSharedPreferences;

    //Declaration of the fragment's views' member variables
    private View mFragmentRootView;
    private EditText mFirstNameEditText;
    private EditText mFamilyNameEditText;
    private EditText mAgeEditText;
    private AppCompatCheckBox mJavaCheckbox;
    private AppCompatCheckBox mKotlinCheckbox;
    private AppCompatCheckBox mCSharpCheckbox;
    private AppCompatCheckBox mRubyCheckbox;
    private AppCompatCheckBox mPythonCheckbox;
    private AppCompatCheckBox mOthersCheckbox;
    private CardView mCloseCardViewButton;


    //Empty constructor
    public SharedPreferencesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_shared_preferences, container, false);

        //Setting up the fragment's views
        setUpViews();

        //Setting up the fragment's views' initial values
        setUpViewsInitialValue();

        //Setting up the activity's accent color on the fragment, so that we can paint our button
        setCustomColorOnFragment();

        return mFragmentRootView;
    }


    //Setting up the fragment's views
    private void setUpViews() {
        //Getting the fragment's views's references
        mFirstNameEditText = (EditText) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_firstname_name_edit_text);
        mFamilyNameEditText = (EditText) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_familyname_edit_text);
        mAgeEditText = (EditText) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_age_edit_text);
        mJavaCheckbox = (AppCompatCheckBox) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_java_checkbox);
        mKotlinCheckbox = (AppCompatCheckBox) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_kotlin_checkbox);
        mCSharpCheckbox = (AppCompatCheckBox) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_csharp_checkbox);
        mRubyCheckbox = (AppCompatCheckBox) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_ruby_checkbox);
        mPythonCheckbox = (AppCompatCheckBox) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_python_checkbox);
        mOthersCheckbox = (AppCompatCheckBox) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_others_checkbox);
        mCloseCardViewButton = (CardView) mFragmentRootView.findViewById(R.id.shared_preferences_fragment_close_cardview_button);

        //Setting a click listener on the button
        mCloseCardViewButton.setOnClickListener(this);
    }


    //Setting up the fragment's views' initial values
    private void setUpViewsInitialValue() {
        //Getting a reference to the Shared Preferences
        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        //Setting the Shared Preferences values to the fragment's views
        mFirstNameEditText.setText(mSharedPreferences.getString(getString(R.string.shared_preferences_fragment_first_name), null));
        mFamilyNameEditText.setText(mSharedPreferences.getString(getString(R.string.shared_preferences_fragment_family_name), null));
        mAgeEditText.setText(mSharedPreferences.getString(getString(R.string.shared_preferences_fragment_age), null));
        mJavaCheckbox.setChecked(mSharedPreferences.getBoolean(getString(R.string.shared_preferences_fragment_java), false));
        mKotlinCheckbox.setChecked(mSharedPreferences.getBoolean(getString(R.string.shared_preferences_fragment_kotlin), false));
        mCSharpCheckbox.setChecked(mSharedPreferences.getBoolean(getString(R.string.shared_preferences_fragment_csharp), false));
        mRubyCheckbox.setChecked(mSharedPreferences.getBoolean(getString(R.string.shared_preferences_fragment_ruby), false));
        mPythonCheckbox.setChecked(mSharedPreferences.getBoolean(getString(R.string.shared_preferences_fragment_python), false));
        mOthersCheckbox.setChecked(mSharedPreferences.getBoolean(getString(R.string.shared_preferences_fragment_others), false));
    }


    //Setting up the activity's accent color on the fragment, so that we can paint our button
    private void setCustomColorOnFragment() {

        //Getting the color in which we will paint the button
        Intent intent = getActivity().getIntent();
        int activityAccentColor = ContextCompat.getColor(getActivity(), intent.getIntExtra(
                getActivity().getString(R.string.main_from_detail_activity_background_color_tag), 0));

        //We paint the button
        mCloseCardViewButton.setCardBackgroundColor(activityAccentColor);
    }




    /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //When clicked on the button, we close the activity
            case R.id.shared_preferences_fragment_close_cardview_button:
                closeActivity();
                break;
        }

    }



    /* Helper methods */


    //Method used to close the activity
    private void closeActivity() {
        getActivity().finish();
    }




    /* Fragment Lifecycle callbacks */


    //When the fragment is stopped, we save all the views'values into the Shared Preferences
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getString(R.string.shared_preferences_fragment_first_name), mFirstNameEditText.getText().toString());
        editor.putString(getString(R.string.shared_preferences_fragment_family_name), mFamilyNameEditText.getText().toString());
        editor.putString(getString(R.string.shared_preferences_fragment_age), mAgeEditText.getText().toString());
        editor.putBoolean(getString(R.string.shared_preferences_fragment_java), mJavaCheckbox.isChecked());
        editor.putBoolean(getString(R.string.shared_preferences_fragment_kotlin), mKotlinCheckbox.isChecked());
        editor.putBoolean(getString(R.string.shared_preferences_fragment_csharp), mCSharpCheckbox.isChecked());
        editor.putBoolean(getString(R.string.shared_preferences_fragment_ruby), mRubyCheckbox.isChecked());
        editor.putBoolean(getString(R.string.shared_preferences_fragment_python), mPythonCheckbox.isChecked());
        editor.putBoolean(getString(R.string.shared_preferences_fragment_others), mOthersCheckbox.isChecked());
        editor.apply();

    }

}
