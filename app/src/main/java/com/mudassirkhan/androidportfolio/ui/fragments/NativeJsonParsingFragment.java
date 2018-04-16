package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.SharedPreferences;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.models.Movie;
import com.mudassirkhan.androidportfolio.utils.JsonUtils;

import java.util.List;

public class NativeJsonParsingFragment extends BaseMovieRecyclerViewFragment {

    //Declaration, as a constant Integer, of the Loader's ID
    private static final int MOVIE_LOADER_ID = 2;


    /* Implementation of the RecyclerView related methods declared in the parent fragment */


    //Method where we decide what type of Layout Manager we will be using
    @Override
    protected RecyclerView.LayoutManager getMovieRecyclerViewLayoutManager() {
        return new GridLayoutManager(getActivity(), 2);
    }


    //Method where we decide what the RecyclerView's items layout will be
    @Override
    protected int setUpRecyclerViewItemLayout() {
        return R.layout.movie_item;
    }


    //Method where we decide whether or not we will be using an item divider in our RecyclerView
    @Override
    protected boolean useRecyclerViewItemDivider() {
        return true;
    }


    //Method where we decide whether we want to make the RecyclerView editable
    @Override
    protected boolean makeRecyclerViewEditable() {
        return true;
    }


    //Method where we decide whether or not we want to hide the GenreView in Portrait mode, in case it exists
    @Override
    protected boolean hideGenreViewInPortraitInCaseItExists() {
        return true;
    }



    /* Implementation of the Loader related methods declared in the parent fragment */


    //Method where we decide whether we use a Loader
    @Override
    protected boolean usingAsynkTaskLoader() {
        return true;
    }


    //Method where we get the Loader's ID, in case we use a Loader
    @Override
    protected int getLoaderId() {
        return MOVIE_LOADER_ID;
    }


    //Method where the way we get the data will be defined, in case we are using a Loader
    @Override
    public String loaderBackgroundAction() {
        return JsonUtils.loadJsonFromAssetFolder(getActivity(), getString(R.string.moviesJsonFile3));
    }


    //Method where we choose the way we will parse the data, in case we use a Loader
    @Override
    protected List<Movie> chooseLoaderCallbackParsingMethod(String data) {
        return JsonUtils.getMovieListNativelyFromJson(getActivity(), data);
    }



    /* Implementation of the Internet related methods declared in the parent fragment */


    //Method where we decide whether or not we get the data from internet
    @Override
    protected boolean gettingDataFromInternet() {
        return false;
    }


    //Method where we implement the Action that will execute the Broadcast Receiver, in case we get the data from internet
    @Override
    protected void broadcastReceiverAction() {
        //Not useful in this case
    }



    /* Implementation of the other methods declared in the parent fragment */


    //Method where the way we get the data will be defined, in case we are not using a Loader
    @Override
    protected void normalThreadAction() {
        //Not useful in this case
    }



    /* Shared Preferences callback */


    //Method triggered when the Shared Preferences change
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Not useful in this case
    }
}
