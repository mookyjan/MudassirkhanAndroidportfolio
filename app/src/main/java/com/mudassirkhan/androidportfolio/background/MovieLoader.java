package com.mudassirkhan.androidportfolio.background;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;

import com.mudassirkhan.androidportfolio.ui.fragments.BaseMovieRecyclerViewFragment;

public class MovieLoader extends AsyncTaskLoader<String> {

    //Declaration of the MovieLoader's member variables
    private Fragment mCurrentLoadingFragment;
    private String mData;

    //MovieLoader's Constructor
    public MovieLoader(Context context, Fragment currentLoadingFragment) {
        super(context);
        mCurrentLoadingFragment = currentLoadingFragment;
    }

    //StartLoading Callback
    @Override
    protected void onStartLoading() {
        //If we have cached data, we use it
        if (mData != null) {
            deliverResult(mData);
        }
        //If not, we start loading
        else {
            forceLoad();
        }
    }

    //Getting the movies on a background thread
    @Override
    public String loadInBackground() {

        //Getting an instance of the currently loading fragment, to execute its loading method on a background thread
        BaseMovieRecyclerViewFragment currentFragment = (BaseMovieRecyclerViewFragment) mCurrentLoadingFragment;
        return currentFragment.loaderBackgroundAction();
    }

    //Method that saves the downloaded data for later retrieval
    @Override
    public void deliverResult(String data) {
        mData = data;
        super.deliverResult(data);
    }
}

