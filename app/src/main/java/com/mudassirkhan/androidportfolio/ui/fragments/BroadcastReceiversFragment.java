package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class BroadcastReceiversFragment extends Fragment implements View.OnClickListener {

    //Declaration, as constants, of the different intents to filter
    public final static String[] BROADCAST_RECEIVER_NETWORK_STATUS_ACTION = {"android.net.conn.CONNECTIVITY_CHANGE"};
    private final static String[] BROADCAST_RECEIVER_BATTERY_STATE_ACTIONS = {"android.intent.action.ACTION_POWER_CONNECTED", "android.intent.action.ACTION_POWER_DISCONNECTED"};
    private final static String[] BROADCAST_RECEIVER_HEADPHONES_STATE_ACTION = {Intent.ACTION_HEADSET_PLUG};

    //Declaration of the Network Broadcast Receiver's member variables
    private NetworkBroadcastReceiver mNetworkBroadcastReceiver;
    private ImageView mNetworkBroadcastReceiverImageView;
    private TextView mNetworkBroadcastReceiverTextView;
    private TextView mNetworkBroadcastReceiverCardViewTextView;
    private boolean mIsNetworkBroadcastReceiverRegistered;

    //Declaration of the Battery State Broadcast Receiver's member variables
    private BatteryStateBroadcastReceiver mBatteryStateBroadcastReceiver;
    private ImageView mBatteryStateBroadcastReceiverImageView;
    private TextView mBatteryStateBroadcastReceiverTextView;
    private TextView mBatteryStateBroadcastReceiverCardViewTextView;
    private boolean mIsBatteryStateBroadcastReceiverRegistered;

    //Declaration of the Headphones State Broadcast Receiver's member variables
    private HeadphonesStateBroadcastReceiver mHeadphonesStateBroadcastReceiver;
    private ImageView mHeadphonesStateBroadcastReceiverImageView;
    private TextView mHeadphonesStateBroadcastReceiverTextView;
    private TextView mHeadphonesStateBroadcastReceiverCardViewTextView;
    private boolean mIsHeadphonesStateBroadcastReceiverRegistered;

    //Declaration of the Activity's accent color
    private int mActivityAccentColor;

    //Declaration of the fragment's root view's member variable
    private View mFragmentRootView;


    //Empty constructor
    public BroadcastReceiversFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_broadcast_receivers, container, false);

        //Setting up the activity's accent color on the fragment, so that we can paint our CardView Buttons
        setUpActivityAccentColor();

        //Setting up the Network Broadcast Receiver
        setUpNetworkBroadcastReceiver();

        //Setting up the Battery State Broadcast Receiver
        setUpBatteryStateBroadcastReceiver();

        //Setting up the Headphones State Broadcast Receiver
        setUpHeadphonesStateBroadcastReceiver();

        return mFragmentRootView;
    }



    /* Setting up the Fragment */


    //Setting up the activity's accent color on the fragment, so that we can paint our CardView Buttons
    private void setUpActivityAccentColor() {
        //We get the accent color from the Intent that started the activity
        Intent intent = getActivity().getIntent();

        //We set the mActivityAccentColor int variable to this value
        mActivityAccentColor = ContextCompat.getColor(getActivity(), intent.getIntExtra(
                getActivity().getString(R.string.main_from_detail_activity_background_color_tag), 0));
    }


    //Setting up the Network Broadcast Receiver
    private void setUpNetworkBroadcastReceiver() {
        //Getting the Network Broadcast Receiver's view references
        mNetworkBroadcastReceiverImageView = (ImageView) mFragmentRootView.findViewById(R.id.network_broadcast_receiver_imageview);
        mNetworkBroadcastReceiverTextView = (TextView) mFragmentRootView.findViewById(R.id.network_broadcast_receiver_textview);
        CardView networkBroadcastReceiverCardviewButton = (CardView) mFragmentRootView.findViewById(R.id.network_broadcast_receiver_cardview_button);
        mNetworkBroadcastReceiverCardViewTextView = (TextView) mFragmentRootView.findViewById(R.id.network_broadcast_receiver_cardview_textview);

        //Setting the click listener on the CardView
        networkBroadcastReceiverCardviewButton.setOnClickListener(this);

        //Setting the Network icon to the corresponding ImageView and painting its background to gray
        Picasso.with(getActivity()).load(R.mipmap.network_icon).into(mNetworkBroadcastReceiverImageView);
        mNetworkBroadcastReceiverImageView.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC);

        //Instantiating the NetworkBroadcastReceiver
        mNetworkBroadcastReceiver = new NetworkBroadcastReceiver();

        //Giving an initial value to the Network Broadcast Receiver instantiation boolean
        mIsNetworkBroadcastReceiverRegistered = false;

        //Painting the Network Broadcast Receiver CardView
        networkBroadcastReceiverCardviewButton.setCardBackgroundColor(mActivityAccentColor);
    }


    //Setting up the Battery State Broadcast Receiver
    private void setUpBatteryStateBroadcastReceiver() {
        //Getting the Battery State Broadcast Receiver's view references
        mBatteryStateBroadcastReceiverImageView = (ImageView) mFragmentRootView.findViewById(R.id.battery_state_broadcast_receiver_imageview);
        mBatteryStateBroadcastReceiverTextView = (TextView) mFragmentRootView.findViewById(R.id.battery_state_broadcast_receiver_textview);
        CardView batteryStateBroadcastReceiverCardviewButton = (CardView) mFragmentRootView.findViewById(R.id.battery_state_broadcast_receiver_cardview_button);
        mBatteryStateBroadcastReceiverCardViewTextView = (TextView) mFragmentRootView.findViewById(R.id.battery_state_broadcast_receiver_cardview_textview);

        //Setting the click listener on the CardView
        batteryStateBroadcastReceiverCardviewButton.setOnClickListener(this);

        //Setting the Battery icon to the corresponding ImageView and painting its background to gray
        Picasso.with(getActivity()).load(R.mipmap.battery_icon).into(mBatteryStateBroadcastReceiverImageView);
        mBatteryStateBroadcastReceiverImageView.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC);

        //Instantiating the BatteryStateBroadcastReceiver
        mBatteryStateBroadcastReceiver = new BatteryStateBroadcastReceiver();

        //Giving an initial value to the Battery State Broadcast Receiver instantiation boolean
        mIsBatteryStateBroadcastReceiverRegistered = false;

        //Painting the Battery State Broadcast Receiver CardView
        batteryStateBroadcastReceiverCardviewButton.setCardBackgroundColor(mActivityAccentColor);
    }


    //Setting up the Headphones State Broadcast Receiver
    private void setUpHeadphonesStateBroadcastReceiver() {
        //Getting the Headphones State Broadcast Receiver's view references
        mHeadphonesStateBroadcastReceiverImageView = (ImageView) mFragmentRootView.findViewById(R.id.headphones_state_broadcast_receiver_imageview);
        mHeadphonesStateBroadcastReceiverTextView = (TextView) mFragmentRootView.findViewById(R.id.headphones_state_broadcast_receiver_textview);
        CardView headphonesStateBroadcastReceiverCardviewButton = (CardView) mFragmentRootView.findViewById(R.id.headphones_state_broadcast_receiver_cardview_button);
        mHeadphonesStateBroadcastReceiverCardViewTextView = (TextView) mFragmentRootView.findViewById(R.id.headphones_state_broadcast_receiver_cardview_textview);

        //Setting the click listener on the CardView
        headphonesStateBroadcastReceiverCardviewButton.setOnClickListener(this);

        //Setting the Headphones icon to the corresponding ImageView and painting its background to gray
        Picasso.with(getActivity()).load(R.mipmap.headphones_icon).into(mHeadphonesStateBroadcastReceiverImageView);
        mHeadphonesStateBroadcastReceiverImageView.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC);

        //Instantiating the HeadphonesStateBroadcastReceiver
        mHeadphonesStateBroadcastReceiver = new HeadphonesStateBroadcastReceiver();

        //Giving an initial value to the Headphones State Broadcast Receiver instantiation boolean
        mIsHeadphonesStateBroadcastReceiverRegistered = false;

        //Painting the Headphones State Broadcast Receiver CardView
        headphonesStateBroadcastReceiverCardviewButton.setCardBackgroundColor(mActivityAccentColor);
    }



    /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //When the user clicks on the the Network Broadcast Receiver's "Register" Button
            case R.id.network_broadcast_receiver_cardview_button:
                //If it is not already registered, we register it
                if (!mIsNetworkBroadcastReceiverRegistered) {
                    mIsNetworkBroadcastReceiverRegistered = true;
                    registerBroadcastReceiver(BROADCAST_RECEIVER_NETWORK_STATUS_ACTION, mNetworkBroadcastReceiverCardViewTextView, mNetworkBroadcastReceiver);
                }
                //If it is already registered, we unregister it
                else {
                    mIsNetworkBroadcastReceiverRegistered = false;
                    unregisterBroadcastReceiver(mNetworkBroadcastReceiverTextView, mNetworkBroadcastReceiverCardViewTextView, mNetworkBroadcastReceiverImageView, mNetworkBroadcastReceiver);
                }
                break;

            //When the user clicks on the the Battery State Broadcast Receiver's "Register" Button
            case R.id.battery_state_broadcast_receiver_cardview_button:
                //If it is not already registered, we register it and we show the current Battery state
                if (!mIsBatteryStateBroadcastReceiverRegistered) {
                    mIsBatteryStateBroadcastReceiverRegistered = true;
                    showInitialBatteryState();
                    registerBroadcastReceiver(BROADCAST_RECEIVER_BATTERY_STATE_ACTIONS, mBatteryStateBroadcastReceiverCardViewTextView, mBatteryStateBroadcastReceiver);
                }
                //If it is already registered, we unregister it
                else {
                    mIsBatteryStateBroadcastReceiverRegistered = false;
                    unregisterBroadcastReceiver(mBatteryStateBroadcastReceiverTextView, mBatteryStateBroadcastReceiverCardViewTextView, mBatteryStateBroadcastReceiverImageView, mBatteryStateBroadcastReceiver);
                }

                break;

            //When the user clicks on the the Headphones State Broadcast Receiver's "Register" Button
            case R.id.headphones_state_broadcast_receiver_cardview_button:
                //If it is not already registered, we register it and we show the current Headphones state
                if (!mIsHeadphonesStateBroadcastReceiverRegistered) {
                    mIsHeadphonesStateBroadcastReceiverRegistered = true;
                    showInitialHeadphonesState();
                    registerBroadcastReceiver(BROADCAST_RECEIVER_HEADPHONES_STATE_ACTION, mHeadphonesStateBroadcastReceiverCardViewTextView, mHeadphonesStateBroadcastReceiver);
                }
                //If it is already registered, we unregister it
                else {
                    mIsHeadphonesStateBroadcastReceiverRegistered = false;
                    unregisterBroadcastReceiver(mHeadphonesStateBroadcastReceiverTextView, mHeadphonesStateBroadcastReceiverCardViewTextView, mHeadphonesStateBroadcastReceiverImageView, mHeadphonesStateBroadcastReceiver);
                }
                break;
        }
    }




    /* Helper methods */


    //Method that determines whether the Battery State related views should be shown as charging or not when first initialized
    private void showInitialBatteryState() {
        if (isBatteryCurrentlyCharging(getActivity())) {
            setBatteryChargingState(true);
        } else {
            setBatteryChargingState(false);
        }
    }


    //Method that changes the Battery State related views depending on the boolean value passed in as a parameter
    private void setBatteryChargingState(boolean isBatteryCharging) {
        if (isBatteryCharging) {
            setTextAndImageView(mBatteryStateBroadcastReceiverTextView, getString(R.string.broadcast_receiver_fragment_battery_charging), mBatteryStateBroadcastReceiverImageView, Color.GREEN);
        } else {
            setTextAndImageView(mBatteryStateBroadcastReceiverTextView, getString(R.string.broadcast_receiver_fragment_battery_not_charging), mBatteryStateBroadcastReceiverImageView, Color.RED);
        }
    }


    //Method that determines whether the battery is currently charging
    private boolean isBatteryCurrentlyCharging(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = context.registerReceiver(null, intentFilter);

        int batteryStatus = -1;

        if (batteryStatusIntent != null) {
            batteryStatus = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        }

        return batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                batteryStatus == BatteryManager.BATTERY_STATUS_FULL;
    }


    //Method that determines whether the Headphones State related views should be shown as plugged in or not when first initialized
    private void showInitialHeadphonesState() {
        if (areHeadphonesAlreadyPluggedIn(getActivity())) {
            setHeadPhonesPluggedInState(true);
        } else {
            setHeadPhonesPluggedInState(false);
        }
    }


    //Method that changes the Headphones State related views depending on the boolean value passed in as a parameter
    private void setHeadPhonesPluggedInState(boolean areHeadphonesPluggedIn) {
        if (areHeadphonesPluggedIn) {
            setTextAndImageView(mHeadphonesStateBroadcastReceiverTextView, getString(R.string.broadcast_receiver_fragment_headphones_plugged), mHeadphonesStateBroadcastReceiverImageView, Color.GREEN);
        } else {
            setTextAndImageView(mHeadphonesStateBroadcastReceiverTextView, getString(R.string.broadcast_receiver_fragment_headphones_unplugged), mHeadphonesStateBroadcastReceiverImageView, Color.RED);
        }
    }


    //Method that determines whether the headphones are currently plugged in
    private boolean areHeadphonesAlreadyPluggedIn(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Intent headPhonesStatusIntent = context.registerReceiver(null, intentFilter);

        int headPhonesStatus = 0;

        if (headPhonesStatusIntent != null) {
            headPhonesStatusIntent.getIntExtra("state", 0);
        }
        return headPhonesStatus == 1;
    }


    //Method used to register the Broadcast Receiver passed in as a parameter
    private void registerBroadcastReceiver(String[] intentFiltersArray, TextView textView, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        for (String anIntentFiltersArray : intentFiltersArray) {
            filter.addAction(anIntentFiltersArray);
        }
        textView.setText(R.string.broadcast_receiver_fragment_unregister);
        getActivity().registerReceiver(receiver, filter);
    }


    //Method used to unregister the Broadcast Receiver passed in as a parameter
    private void unregisterBroadcastReceiver(TextView textView, TextView buttonTextView, ImageView imageView, BroadcastReceiver receiver) {
        textView.setText(R.string.broadcast_receiver_fragment_not_registered);
        buttonTextView.setText(R.string.broadcast_receiver_fragment_register);
        imageView.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC);
        getActivity().unregisterReceiver(receiver);
    }


    //Method used to change the background color of the ImageView passed in as a parameter
    private void changeImageViewColor(ImageView imageView, int color) {
        imageView.getBackground().setColorFilter(color, PorterDuff.Mode.SRC);
    }


    //Method used to change the TextView and the ImageView passed in as parameters
    private void setTextAndImageView(TextView textView, String text, ImageView imageView, int color) {
        textView.setText(text);
        changeImageViewColor(imageView, color);
    }



        /* Broadcast Receivers inner classes */


    //Broadcast receiver used to react to Connectivity changes
    class NetworkBroadcastReceiver extends BroadcastReceiver {

        //Triggered when the Connectivity changes
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            //We make sure that this is the correct Intent that triggered the Broadcast Receiver
            if (action.equals(BROADCAST_RECEIVER_NETWORK_STATUS_ACTION[0])) {
                //If we are connected to the internet, we change the ImageView's icon and TextView's text according to it
                if (NetworkUtils.isConnected(getActivity())) {
                    setTextAndImageView(mNetworkBroadcastReceiverTextView, getString(R.string.broadcast_receiver_fragment_connected), mNetworkBroadcastReceiverImageView, Color.GREEN);
                }
                //If we are not connected to the internet, we change the ImageView's icon and TextView's text according to it
                else {
                    setTextAndImageView(mNetworkBroadcastReceiverTextView, getString(R.string.broadcast_receiver_fragment_not_connected), mNetworkBroadcastReceiverImageView, Color.RED);
                }
            }
        }
    }


    //Broadcast receiver used to react to Battery State changes
    class BatteryStateBroadcastReceiver extends BroadcastReceiver {

        //Triggered when the Battery state changes
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            //If it is the "android.intent.action.ACTION_POWER_CONNECTED" intent that triggered our Broadcast Receiver, then we set the state to "Charging"
            if (action.equals(BROADCAST_RECEIVER_BATTERY_STATE_ACTIONS[0])) {
                setBatteryChargingState(true);

            }
            //If it is the "android.intent.action.ACTION_POWER_DISCONNECTED" intent that triggered our Broadcast Receiver, then we set the state to "Not charging"
            else if (action.equals(BROADCAST_RECEIVER_BATTERY_STATE_ACTIONS[1])) {
                setBatteryChargingState(false);
            }
        }
    }


    //Broadcast receiver used to react to Headphones State changes
    class HeadphonesStateBroadcastReceiver extends BroadcastReceiver {

        //Triggered when the Headphones state changes
        @Override
        public void onReceive(Context context, Intent intent) {

            //We make sure that this is the correct Intent that triggered the Broadcast Receiver
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    //If the Headphones are plugged in, we change the ImageView's icon and TextView's text according to it
                    case 0:
                        setHeadPhonesPluggedInState(false);
                        break;

                    //If the Headphones not are plugged in, we change the ImageView's icon and TextView's text according to it
                    case 1:
                        setHeadPhonesPluggedInState(true);
                        break;
                }

            }

        }

    }

}




