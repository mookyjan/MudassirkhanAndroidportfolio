package com.mudassirkhan.androidportfolio.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.utils.ImageUtils;
import com.mudassirkhan.androidportfolio.utils.PermissionsUtils;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class FileFragment extends Fragment implements View.OnClickListener {

    //Declaration, as a constant Integer, of a flag that will be returned by the CropImage Activity when it is done
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    //Declaration, as a constant Integer, of the permission flag we will need in this fragment
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 15;

    //Declaration, as a constant String, of the File Provider Content Authority
    private static final String FILE_PROVIDER_CONTENT_AUTHORITY = "com.example.android.fileprovider";

    //Declaration of the fragment's different views' member variables
    private View mFragmentRootView;
    private ImageView mPictureImageView;
    private FloatingActionButton mSaveFab;
    private FloatingActionButton mClearFab;

    //Declaration of the chosen picture's member variables
    private Uri mPictureUri;
    private String mTempPicturePath;


    //Empty constructor
    public FileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_file, container, false);

        //Setting up the fragment's views
        setUpView();

        return mFragmentRootView;
    }



    /* Setting up the Fragment */


    //Setting up the fragment's views
    private void setUpView() {
        //Getting the fragment's views' references
        mPictureImageView = (ImageView) mFragmentRootView.findViewById(R.id.picture_imageview);
        mSaveFab = (FloatingActionButton) mFragmentRootView.findViewById(R.id.save_fab);
        mClearFab = (FloatingActionButton) mFragmentRootView.findViewById(R.id.clear_fab);
        FloatingActionButton takePictureFab = (FloatingActionButton) mFragmentRootView.findViewById(R.id.take_picture_fab);

        //Setting a click listener on the relevant ones
        mSaveFab.setOnClickListener(this);
        mClearFab.setOnClickListener(this);
        takePictureFab.setOnClickListener(this);

        //Setting an image resource on the ImageView
        mPictureImageView.setImageResource(R.drawable.empty_picture_placeholder);
    }



    /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //When clicked on the Take Picture Fab, we launch the Camera
            case R.id.take_picture_fab:
                takePicture();
                break;

            //When clicked on the Save Fab, we save the image
            case R.id.save_fab:
                save();
                break;

            //When clicked on the Clear fab, we delete the temporary image file and reset the fragment views
            case R.id.clear_fab:
                clearImage();
                break;
        }

    }


    //Handling callback for when the activity that we started exits
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //We make sure that this callback is actually from the Activity we want and that the result is OK
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //If it is, we act upon it, saving the data returned and changing the fragment's look
            setPictureToImageView();
            mSaveFab.setVisibility(View.VISIBLE);
            mClearFab.setVisibility(View.VISIBLE);

        }
        //If not, we delete the temporary file
        else {
            ImageUtils.deleteFile(getActivity(), mTempPicturePath);
        }

    }



    /* Helper methods */


    //Method triggered when clicked on the Take Picture Fab
    public void takePicture() {

        //First of all, we check if we have the permission to store in the external storage.
        if (!PermissionsUtils.checkIfHasPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            //If we do not have it, we ask for it
            PermissionsUtils.requestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_WRITE_EXTERNAL_STORAGE);

        }
        //Otherwise, we launch the Camera activity
        else {
            if (mTempPicturePath != null) {
                ImageUtils.deleteFile(getActivity(), mTempPicturePath);
            }
            launchCameraActivity();
        }
    }


    //Method that creates a temporary Image File and launches the camera.
    public void launchCameraActivity() {

        //We create the intent to launch the camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //We make sure we have an App that can handle the intent
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

            File pictureFile = null;

            try {

                //We try to create the temporary image file
                pictureFile = ImageUtils.createTemporaryImageFile(getActivity());

            } catch (IOException e) {

                e.printStackTrace();

            }

            //If the file is not null
            if (pictureFile != null) {

                //We get its absolute path
                mTempPicturePath = pictureFile.getAbsolutePath();

                //We get the Uri for this image file
                mPictureUri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER_CONTENT_AUTHORITY, pictureFile);

                //We pass the Uri to the Intent, so that the camera App knows were to store the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureUri);

                //Finally, we start the Camera App's activity, passing the number ID of our request, so that we can handle it when it comes back
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

            }

        }

    }


    //Method used to set the Picture on the ImageView
    private void setPictureToImageView() {
        Glide.with(getActivity())
                .load(mPictureUri)
                .centerCrop()
                .into(mPictureImageView);
    }


    //Method triggered when clicked on the Save Fab
    public void save() {
        //We save the picture
        ImageUtils.savePicture(getActivity(), mTempPicturePath);
        //We delete the temporary file
        ImageUtils.deleteFile(getActivity(), mTempPicturePath);
        //We hide the Save and Clear Fabs
        mSaveFab.setVisibility(View.GONE);
        mClearFab.setVisibility(View.GONE);
    }


    //Method triggered when clicked on the Clear Fab
    public void clearImage() {
        //We remove the image from the ImageView and hide the Save and Clear Fabs
        mPictureImageView.setImageResource(R.drawable.empty_picture_placeholder);
        mSaveFab.setVisibility(View.GONE);
        mClearFab.setVisibility(View.GONE);

        //We delete the temporary file and send a confirmation toast to the user
        boolean hasBeenDeleted = ImageUtils.deleteFile(getActivity(), mTempPicturePath);
        if (hasBeenDeleted) {
            Toast.makeText(getActivity(), R.string.file_fragment_image_deleted_correctly, Toast.LENGTH_SHORT).show();
        }
    }

}