package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.SharedPreferences;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.models.Movie;
import com.mudassirkhan.androidportfolio.utils.JsonUtils;
import com.mudassirkhan.androidportfolio.utils.NetworkUtils;
import com.mudassirkhan.androidportfolio.utils.ScreenUtils;

import java.util.List;

public class NativeHttpFragment extends BaseMovieRecyclerViewFragment {

    //Declaration, as a constant Integer, of the Loader's ID
    private static final int MOVIE_LOADER_ID = 1;


    /* Implementation of the RecyclerView related methods declared in the parent fragment */


    //Method where we decide what type of Layout Manager we will be using
    @Override
    protected RecyclerView.LayoutManager getMovieRecyclerViewLayoutManager() {
        return new GridLayoutManager(getActivity(), ScreenUtils.getNumberOfCarViewColumnsDependingOnScreenWidth(getActivity()));
    }


    //Method where we decide what the RecyclerView's items layout will be
    @Override
    protected int setUpRecyclerViewItemLayout() {
        return R.layout.movie_item_cardview;
    }


    //Method where we decide whether or not we will be using an item divider in our RecyclerView
    @Override
    protected boolean useRecyclerViewItemDivider() {
        return false;
    }


    //Method where we decide whether we want to make the RecyclerView editable
    @Override
    protected boolean makeRecyclerViewEditable() {
        return false;
    }


    //Method where we decide whether or not we want to hide the GenreView in Portrait mode, in case it exists
    @Override
    protected boolean hideGenreViewInPortraitInCaseItExists() {
        return false;
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
        String url = NetworkUtils.getCustomMovieUrl(getActivity(), 878);
        return NetworkUtils.fetchMovieDataThroughNativeWay(url);
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
        return true;
    }


    //Method where we implement the Action that will execute the Broadcast Receiver, in case we get the data from internet
    @Override
    protected void broadcastReceiverAction() {
        restartLoader();
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
        //We check that the Shared Preference that changed is one the following two
        if (key.equals(getString(R.string.settings_sort_by_vote_average_sharedpref)) || key.equals(getString(R.string.settings_year_for_filtering_network_request_sharedpref))) {
            //If that is the case, we stop the Loader
            stopLoader();
            //Then, if we are connected to internet, we restart the Loader
            if (NetworkUtils.isConnected(getActivity())) {
                startLoader();
            }
            //If we are not connected to internet, we show the empty view
            else {
                isNotShowingData(getString(R.string.broadcast_receiver_fragment_not_connected), R.mipmap.offline_icon);
            }
        }
    }
}


