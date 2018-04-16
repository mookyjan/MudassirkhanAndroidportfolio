package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.models.Movie;
import com.mudassirkhan.androidportfolio.utils.JsonUtils;
import com.mudassirkhan.androidportfolio.utils.NetworkUtils;

import java.util.List;

public class VolleyFragment extends BaseMovieRecyclerViewFragment {

    //Declaration of Volley's StringRequest member variable
    private StringRequest mStringRequest;


    /* Implementation of the RecyclerView related methods declared in the parent fragment */


    //Method where we decide what type of Layout Manager we will be using
    @Override
    protected RecyclerView.LayoutManager getMovieRecyclerViewLayoutManager() {
        return new LinearLayoutManager(getActivity());
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
        return false;
    }


    //Method where we get the Loader's ID, in case we use a Loader
    @Override
    protected int getLoaderId() {
        return 0;
    }


    //Method where the way we get the data will be defined, in case we are using a Loader
    @Override
    public String loaderBackgroundAction() {
        return null;
    }


    //Method where we choose the way we will parse the data, in case we use a Loader
    @Override
    protected List<Movie> chooseLoaderCallbackParsingMethod(String data) {
        return null;
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
        startRequest();
    }




    /* Implementation of the other methods declared in the parent fragment */


    //Method where the way we get the data will be defined, in case we are not using a Loader
    @Override
    protected void normalThreadAction() {
        startRequest();
    }



    /* Helper methods */


    //Method used to start the HTTP request
    private void startRequest() {
        //If we are connected to internet, then we start the request
        if (NetworkUtils.isConnected(getActivity())) {
            String url = NetworkUtils.getCustomMovieUrl(getActivity(), 18);
            mStringRequest = getVolleyStringRequest(url);
            mStringRequest.setShouldCache(false);
            startDownloading();
        }
        //If not, we show the empty view
        else {
            isNotShowingData(getString(R.string.broadcast_receiver_fragment_not_connected), R.mipmap.offline_icon);
        }

    }


    //Method used to start downloading from internet
    private void startDownloading() {
        //We start the HTTP request
        Volley.newRequestQueue(getActivity()).add(mStringRequest);
        //We show the Loading Indicator
        isDownloading();
        //We scroll to the top of the RecyclerView
        scrollToTop();
    }


    //Method that creates and returns Volley's StringRequest
    private StringRequest getVolleyStringRequest(String url) {
        return new StringRequest(url, new com.android.volley.Response.Listener<String>() {

            //If we get a response, we act upon it
            @Override
            public void onResponse(String response) {
                //We make sure that the response is neither null nor empty
                if (response != null && !response.isEmpty()) {
                    //If everything is OK, we parse the data, refresh the RecyclerView's adapter, and show the RecyclerView
                    List<Movie> movies = JsonUtils.getMovieListNativelyFromJson(getActivity(), response);
                    refreshAdapter(movies);
                    isShowingData();
                }
                //If the response is not OK, we show the empty view
                else {
                    isNotShowingData(getString(R.string.base_movie_recyclerview_fragment_cannot_get_data), R.mipmap.error_icon);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            //If we get an error, we show the empty view
            @Override
            public void onErrorResponse(VolleyError error) {
                isNotShowingData(getString(R.string.base_movie_recyclerview_fragment_cannot_get_data), R.mipmap.error_icon);
            }
        });

    }




    /* Shared Preferences callback */


    //Method triggered when the Shared Preferences change
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //We check that the Shared Preference that changed is one the following two
        if (key.equals(getString(R.string.settings_sort_by_vote_average_sharedpref)) || key.equals(getString(R.string.settings_year_for_filtering_network_request_sharedpref))) {
            //If that is the case, we start the request
            startRequest();
        }
    }
}
