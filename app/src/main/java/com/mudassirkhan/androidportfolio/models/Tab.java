package com.mudassirkhan.androidportfolio.models;

import android.support.v4.app.Fragment;

public class Tab {

    //Declaration of Tab object's member variables
    private Fragment mFragment;
    private String mTabName;

    //Tab's constructor
    public Tab(Fragment fragment, String tabName) {
        mFragment = fragment;
        mTabName = tabName;
    }


    //Tab's getters
    public Fragment getFragment() {
        return mFragment;
    }

    public String getTabName() {
        return mTabName;
    }
}
