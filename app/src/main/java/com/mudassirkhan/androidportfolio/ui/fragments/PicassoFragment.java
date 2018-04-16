package com.mudassirkhan.androidportfolio.ui.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.adapters.PicassoAdapter;
import com.mudassirkhan.androidportfolio.utils.NetworkUtils;
import com.mudassirkhan.androidportfolio.utils.PermissionsUtils;
import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PicassoFragment extends Fragment implements View.OnClickListener {

    //Declaration, as a constant Integer, of the permission flag we will need in this fragment
    public static final int PERMISSION_READ_EXTERNAL_MEMORY = 1;

    //Declaration of the RecyclerView's member variables
    private RecyclerView mPicassoRecyclerView;
    private PicassoAdapter mPicassoNetworkAdapter;
    private PicassoAdapter mPicassoResourceAdapter;
    private PicassoAdapter mPicassoFileAdapter;
    private List<Integer> mResourceImagesIdList;
    private String[] mNetworkUrlArray;

    //Declaration of the Empty View's member variables
    private LinearLayout mEmptyStateLinearLayout;
    private ImageView mEmptyStateImageView;
    private TextView mEmptyStateTextView;

    //Declaration of the "toggling between different modes" related member variables
    private FloatingActionButton mFloatingActionButton;
    private int mCurrentMode;

    //Declaration of the Menu Alert Dialog's member variable
    private AlertDialog mMenuDialog;

    //Declaration of the Broadcast Receiver's member variable
    private NetworkBroadcastReceiver mNetworkBroadcastReceiver;

    //Declaration of the fragment's root view's member variable
    private View mFragmentRootView;


    //Empty constructor
    public PicassoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_picasso_layout, container, false);

        //Setting up the Floating Action Button
        setUpFloatingActionButton();

        //Setting up the RecyclerView's empty view
        setUpEmptyView();

        //Setting up the Network mode Broadcast Receiver
        setBroadcastReceiver();

        //Setting up the Menu Alert Dialog
        setMenuAlertDialog();

        //Setting up the fragment's RecyclerView
        setUpRecyclerView();

        return mFragmentRootView;
    }




    /* Setting up the Fragment */


    //Setting up the Floating Action Button
    private void setUpFloatingActionButton() {
        //Getting the Floating Action Button's reference
        mFloatingActionButton = (FloatingActionButton) mFragmentRootView.findViewById(R.id.picasso_fragment_fab);

        //Setting a click listener on the Floating Action Button
        mFloatingActionButton.setOnClickListener(this);

        //Getting the color in which we will paint the button
        Intent intent = getActivity().getIntent();
        int activityAccentColor = ContextCompat.getColor(getActivity(), intent.getIntExtra(
                getActivity().getString(R.string.main_from_detail_activity_background_color_tag), 0));

        //We paint the button
        mFloatingActionButton.setColorNormal(activityAccentColor);
        mFloatingActionButton.setColorPressed(activityAccentColor);
    }


    //Setting up the RecyclerView's empty view
    private void setUpEmptyView() {
        //Getting the RecyclerView's empty view's references
        mEmptyStateLinearLayout = (LinearLayout) mFragmentRootView.findViewById(R.id.empty_state_linear_layout);
        mEmptyStateImageView = (ImageView) mFragmentRootView.findViewById(R.id.empty_state_imageview);
        mEmptyStateTextView = (TextView) mFragmentRootView.findViewById(R.id.empty_state_textview);
    }


    //Setting up the Network mode Broadcast Receiver
    private void setBroadcastReceiver() {
        //Instantiating the Broadcast Receiver
        mNetworkBroadcastReceiver = new NetworkBroadcastReceiver();
    }


    //Setting up the Menu Alert Dialog
    private void setMenuAlertDialog() {
        //Instantiating the Menu Alert Dialog
        mMenuDialog = new AlertDialog.Builder(getActivity()).create();
    }


    //Setting up the fragment's RecyclerView
    private void setUpRecyclerView() {
        //Getting the RecyclerView's reference
        mPicassoRecyclerView = (RecyclerView) mFragmentRootView.findViewById(R.id.picasso_recyclerview);

        //Instantiating the PicassoAdapters
        mPicassoResourceAdapter = new PicassoAdapter(getActivity(), R.layout.picasso_item);
        mPicassoNetworkAdapter = new PicassoAdapter(getActivity(), R.layout.picasso_item);
        mPicassoFileAdapter = new PicassoAdapter(getActivity(), R.layout.picasso_item);

        //Starting the fragment with the Resource mode
        useResourceMode();

        //Instantiating the LinearLayoutManager
        RecyclerView.LayoutManager picassoLayoutManager = new GridLayoutManager(getActivity(), 2);

        //Wiring up the RecyclerView
        mPicassoRecyclerView.setHasFixedSize(true);
        mPicassoRecyclerView.setLayoutManager(picassoLayoutManager);

        //Making the Floating Action Button hide when the RecyclerView is scrolled
        mPicassoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    mFloatingActionButton.hide(true);
                else if (dy < 0)
                    mFloatingActionButton.show(true);
            }
        });
    }




    /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //When clicked on the Floating Action Button, we show the Menu Alert Dialog
            case R.id.picasso_fragment_fab:
                showMenuDialog();
                break;
        }
    }





    /* Helper methods */


    //Method that creates and returns an instance of the Menu Alert Dialog
    private AlertDialog createMenuDialog() {

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.picasso_fragment_choose_source)
                .setIcon(R.mipmap.big_black_image_icon)
                .setItems(R.array.picasso_fragment_menu_items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //Handling the menu's click events
                        switch (which) {

                            //When the Resource option is chosen
                            case 0:
                                //If the user is not already in Resource mode, we start it
                                if (mCurrentMode != PicassoAdapter.RESOURCES_MODE) {
                                    useResourceMode();
                                }
                                //If they are already in Resource mode, we show a toast
                                else {
                                    Toast.makeText(getActivity(), R.string.picasso_fragment_resource_mode_already, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            //When the Network option is chosen
                            case 1:
                                //If the user is not already in Network mode, we start it
                                if (mCurrentMode != PicassoAdapter.NETWORK_MODE) {
                                    useNetworkMode();
                                }
                                //If they are already in Network mode, we show a toast
                                else {
                                    Toast.makeText(getActivity(), R.string.picasso_fragment_network_mode_already, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            //When the Files option is chosen
                            case 2:
                                //If the user is not already in Files mode, we start it
                                if (mCurrentMode != PicassoAdapter.FILES_MODE) {
                                    useFileMode();
                                }
                                //If they are already in Files mode, we show a toast
                                else {
                                    Toast.makeText(getActivity(), R.string.picasso_fragment_file_mode_already, Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }

                    }
                }).create();

    }


    //Method used to set the RecyclerView in Resource mode
    private void useResourceMode() {
        //If the current mode is the Network one, then we unregister the Broadcast Receiver first
        if (mCurrentMode == PicassoAdapter.NETWORK_MODE && mNetworkBroadcastReceiver.isRegistered) {
            unregisterNetworkBroadcastReceiver();
        }

        //We get the list of images
        if (mResourceImagesIdList == null) {
            mResourceImagesIdList = getResourceImagesId();
        }

        //We set the RecyclerView related stuff
        mPicassoResourceAdapter.setResourceMode(mResourceImagesIdList, PicassoAdapter.RESOURCES_MODE);
        mPicassoRecyclerView.setAdapter(mPicassoResourceAdapter);
        mPicassoRecyclerView.smoothScrollToPosition(0);

        //We change the mode to Resource mode
        mCurrentMode = PicassoAdapter.RESOURCES_MODE;

        //We show the RecyclerView
        isShowingData();
    }


    //Method used to set the RecyclerView in Network mode
    private void useNetworkMode() {
        //We register the Network Broadcast Receiver
        registerNetworkBroadcastReceiver();

        //If we are connected to internet, we get the data from the Network
        if (NetworkUtils.isConnected(getActivity())) {
            getDataForNetworkMode();
        }
        //If not, we show the empty view
        else {
            isNotShowingData(getString(R.string.broadcast_receiver_fragment_not_connected), R.mipmap.offline_icon);
        }

        //We change the mode to Network mode
        mCurrentMode = PicassoAdapter.NETWORK_MODE;
    }


    //Method used to set the RecyclerView in Files mode
    private void useFileMode() {
        //We check that we have the Permission to access the device's external storage
        if (!PermissionsUtils.checkIfHasPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If we do not, we ask for it
            PermissionsUtils.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_READ_EXTERNAL_MEMORY);
        } else {
            //If we do, we set the RecyclerView with images from the user's device
            setRecyclerViewWithLocalStorageImages();
        }
    }


    //Method used to set the RecyclerView with images from the user's device
    public void setRecyclerViewWithLocalStorageImages() {
        //We get the list of paths to the user's images
        List<String> filePathList = getFileImagesPath();

        //We check that the list is neither null nor empty
        if (filePathList != null && filePathList.size() != 0) {
            //If everything is OK, we do the RecyclerView related stuff
            mPicassoFileAdapter.setFileMode(filePathList, PicassoAdapter.FILES_MODE);
            mPicassoRecyclerView.setAdapter(mPicassoFileAdapter);
            mPicassoRecyclerView.smoothScrollToPosition(0);

            //We show the RecyclerView
            isShowingData();
        }
        //If not, we show the empty view
        else {
            isNotShowingData(getString(R.string.picasso_fragment_no_picture_in_device), R.mipmap.empty_picture_icon);
        }

        //If we were in Network mode, we unregister the BroadCast Receiver
        if (mCurrentMode == PicassoAdapter.NETWORK_MODE && mNetworkBroadcastReceiver.isRegistered) {
            unregisterNetworkBroadcastReceiver();
        }

        //We change the mode to Files mode
        mCurrentMode = PicassoAdapter.FILES_MODE;
    }


    //Method used to get the the Resource mode images
    private List<Integer> getResourceImagesId() {
        //We create an ArrayList of Integers
        List<Integer> resourceImagesIdList = new ArrayList<>();

        //We get the array containing the Images from the resource
        TypedArray resourceImagesIdTypedArray = getResources().obtainTypedArray(R.array.picasso_resource_images_list);

        //We iterate through the array and add the images to our ArrayList one by one
        for (int i = 0; i < resourceImagesIdTypedArray.length(); i++) {
            resourceImagesIdList.add(resourceImagesIdTypedArray.getResourceId(i, -1));
        }

        //We return the ArrayList of Images's resources
        return resourceImagesIdList;
    }


    //Method used to get the Network mode's data from internet
    private void getDataForNetworkMode() {
        //We get the Urls that will be used to get the data from internet
        if (mNetworkUrlArray == null) {
            mNetworkUrlArray = getNetworkImagesUrl();
        }

        //We set up the RecyclerView related stuff
        mPicassoNetworkAdapter.setNetworkMode(mNetworkUrlArray, PicassoAdapter.NETWORK_MODE);
        mPicassoRecyclerView.setAdapter(mPicassoNetworkAdapter);
        mPicassoRecyclerView.smoothScrollToPosition(0);
    }


    //Method used to get the list of paths to the user's images
    public List<String> getFileImagesPath() {

        //We create an empty list of Strings
        List<String> listOfAllImagesPath = new ArrayList<>();

        //We prepare our query
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

        //We make a query to the MediaStore through its Content Provider
        Cursor cursor = getActivity().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Images.Media._ID);

        //We iterate through the obtained cursor and add the Uris one by one to our list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    listOfAllImagesPath.add(path);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        //We return the list
        return listOfAllImagesPath;
    }


    //Method used to get the Network's images URL
    private String[] getNetworkImagesUrl() {
        return getResources().getStringArray(R.array.picasso_network_images_url_list);
    }


    //Method that shows the Menu Alert Dialog
    private void showMenuDialog() {
        if (!mMenuDialog.isShowing()) {
            mMenuDialog = createMenuDialog();
            mMenuDialog.show();
        }
    }


    //Method used to show the RecyclerView
    public void isShowingData() {
        //If the RecyclerView is not visible, we show it
        if (mPicassoRecyclerView.getVisibility() != View.VISIBLE) {
            mPicassoRecyclerView.setVisibility(View.VISIBLE);
        }
        //If the Empty View is visible, we hide it
        if (mEmptyStateLinearLayout.getVisibility() == View.VISIBLE) {
            mEmptyStateLinearLayout.setVisibility(View.INVISIBLE);
        }
    }


    //Method used to show the empty view
    private void isNotShowingData(String emptyTextViewContent, int emptyViewIconImage) {
        //We set the image passed in as a parameter to the empty view and set a text on it too
        Picasso.with(getActivity()).load(emptyViewIconImage).into(mEmptyStateImageView);
        mEmptyStateTextView.setText(emptyTextViewContent);

        //If the Empty View is not visible, then we show it
        if (mEmptyStateLinearLayout.getVisibility() != View.VISIBLE) {
            mEmptyStateLinearLayout.setVisibility(View.VISIBLE);
        }

        //If the RecyclerView is visible, then we hide it
        if (mPicassoRecyclerView.getVisibility() == View.VISIBLE) {
            mPicassoRecyclerView.setVisibility(View.INVISIBLE);
        }
    }


    //Method used to register the Network Broadcast Receiver
    private void registerNetworkBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastReceiversFragment.BROADCAST_RECEIVER_NETWORK_STATUS_ACTION[0]);
        getActivity().registerReceiver(mNetworkBroadcastReceiver, filter);
        mNetworkBroadcastReceiver.isRegistered = true;
    }


    //Method used to unregister the Network Broadcast Receiver
    private void unregisterNetworkBroadcastReceiver() {
        getActivity().unregisterReceiver(mNetworkBroadcastReceiver);
        mNetworkBroadcastReceiver.isRegistered = false;
    }




    /* Fragment Lifecycle callbacks */


    //When the Fragment is stopped, we dismiss the dialog and we unregister the Network Broadcast Receiver
    @Override
    public void onStop() {
        super.onStop();
        if (mMenuDialog != null) {
            mMenuDialog.dismiss();
        }
        if (mNetworkBroadcastReceiver.isRegistered) {
            unregisterNetworkBroadcastReceiver();
        }
    }


    //Broadcast Receiver used to handle connectivity changes in this fragment
    class NetworkBroadcastReceiver extends BroadcastReceiver {

        public boolean isRegistered;

        //Triggered when the connectivity changes
        @Override
        public void onReceive(Context context, Intent intent) {
            //If the current mode is the Network one and we are connected to internet, we download the data and show the RecyclerView
            if (mCurrentMode == PicassoAdapter.NETWORK_MODE && NetworkUtils.isConnected(getActivity())) {
                isShowingData();
                getDataForNetworkMode();
            }
        }
    }
}