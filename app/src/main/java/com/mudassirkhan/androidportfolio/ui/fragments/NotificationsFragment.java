package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.services.NotificationIntentService;
import com.mudassirkhan.androidportfolio.tasks.NotificationTasks;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;

public class NotificationsFragment extends Fragment implements View.OnClickListener {

    //Declaration of the fragment's views' member variables
    private View mFragmentRootView;
    private EditText mNotificationTitleEditText;
    private EditText mNotificationContentEditText;
    private ImageView mSelectedPictureImageView;
    private CardView mStartNotificationCardViewButton;

    //Declaration of the selected picture uri's member variable
    private String mSelectedPictureUriString;


    //Empty constructor
    public NotificationsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        //Setting up the fragment's views
        settingUpViews();

        //Setting up the activity's accent color on the fragment, so that we can paint our button
        setCustomColorOnFragment();

        //Setting up the empty picture placeholder on the ImageView
        setUpEmptyPicturePlaceHolder();

        return mFragmentRootView;
    }


    //Setting up the fragment's views
    private void settingUpViews() {
        //Getting the fragment's views's references
        mNotificationTitleEditText = (EditText) mFragmentRootView.findViewById(R.id.notification_title_edittext);
        mNotificationContentEditText = (EditText) mFragmentRootView.findViewById(R.id.notification_content_edittext);
        mSelectedPictureImageView = (ImageView) mFragmentRootView.findViewById(R.id.notification_selected_picture_image_view);
        mStartNotificationCardViewButton = (CardView) mFragmentRootView.findViewById(R.id.show_notification_cardview_button);

        //Setting a click listener on the relevant views
        mStartNotificationCardViewButton.setOnClickListener(this);
        mSelectedPictureImageView.setOnClickListener(this);
    }


    //Setting up the activity's accent color on the fragment, so that we can paint our button
    private void setCustomColorOnFragment() {

        //Getting the color in which we will paint the button
        Intent intent = getActivity().getIntent();
        int activityAccentColor = ContextCompat.getColor(getActivity(), intent.getIntExtra(
                getActivity().getString(R.string.main_from_detail_activity_background_color_tag), 0));

        //We paint the button
        mStartNotificationCardViewButton.setCardBackgroundColor(activityAccentColor);
    }


    //Setting up the empty picture placeholder on the ImageView
    private void setUpEmptyPicturePlaceHolder() {
        Picasso.with(getActivity())
                .load(R.drawable.empty_picture_placeholder)
                .fit()
                .centerCrop()
                .into(mSelectedPictureImageView);
    }




    /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //When clicked on the Show Notification Button
            case R.id.show_notification_cardview_button:

                //We create a bundle, passing in the title, the content, and the picture Uri
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.notification_data_bundle_picture_uri), mSelectedPictureUriString);
                bundle.putString(getString(R.string.notification_data_bundle_title), mNotificationTitleEditText.getText().toString());
                bundle.putString(getString(R.string.notification_data_bundle_content), mNotificationContentEditText.getText().toString());

                //We create the Intent that will start our Notification Intent Service
                Intent startNotificationServiceIntent = new Intent(getActivity(), NotificationIntentService.class);

                //We pass the bundle to it
                startNotificationServiceIntent.putExtras(bundle);

                //We pass the action to it
                startNotificationServiceIntent.setAction(NotificationTasks.START_NOTIFICATION_TASK);

                //We start the Notification Intent Service
                getActivity().startService(startNotificationServiceIntent);
                break;

            //When clicked on the ImageView, we send the user to the CropImage Activity, so that they can choose a picture
            case R.id.notification_selected_picture_image_view:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getContext(), this);

                break;
        }

    }


    //Handling callback for when the activity that we started exits
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //We make sure that this callback is actually from the Activity we want
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            //We make sure that it went OK
            if (resultCode == RESULT_OK) {
                //If everything is fine, we save the picture's Uri, and show the picture in the ImageView
                if (result != null) {
                    Uri resultUri = result.getUri();
                    mSelectedPictureUriString = resultUri.toString();
                    Picasso.with(getActivity())
                            .load(mSelectedPictureUriString)
                            .fit()
                            .centerCrop()
                            .into(mSelectedPictureImageView);
                }
            }
            //If it was not OK, we show a toast to the user
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), R.string.notifications_fragment_picture_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
