package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.models.Movie;
import com.mudassirkhan.androidportfolio.utils.JsonUtils;
import com.mudassirkhan.androidportfolio.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class RetrofitGsonFragment extends BaseMovieRecyclerViewFragment {


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
        return true;
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
        startDownloading();
    }




    /* Implementation of the other methods declared in the parent fragment */


    //Method where the way we get the data will be defined, in case we are not using a Loader
    @Override
    protected void normalThreadAction() {
        //If we are connected to internet, we start downloading
        if (NetworkUtils.isConnected(getActivity())) {
            startDownloading();
        }
        //If not, we show the empty view
        else {
            isNotShowingData(getString(R.string.broadcast_receiver_fragment_not_connected), R.mipmap.offline_icon);
        }

    }



    /* Helper methods */


    //Method used to start downloading data from internet using Retrofit
    private void startDownloading() {
        //Toggle between the layout's views to show the Loading Indicator
        isDownloading();

        //We start the HTTP request using Retrofit
        Call<List<Movie>> movieListCall = getRetrofitCallObject();
        movieListCall.enqueue(gettingRetrofitCallbackObject());

        //We scroll to the top of the RecyclerView
        scrollToTop();
    }


    //Method that returns the Retrofit Call Object used to make the HTTP request
    private Call<List<Movie>> getRetrofitCallObject() {

        //We get the base Movie Database URL
        String url = NetworkUtils.getBaseWebApiUrl(getActivity()).build().toString() + "/";

        //We instantiate a Retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(new JsonUtils.MyCustomJsonConverterFactory())
                .baseUrl(url)
                .build();

        //We create a MovieService from our custom Retrofit interface
        NetworkUtils.MovieService movieService = retrofit.create(NetworkUtils.MovieService.class);

        //We get the default values for the orderByVoteAverage and yearForFilteringRequest parameters
        boolean orderByVoteAverage = getActivity().getResources().getBoolean(R.bool.network_movie_sorted_by_vote_average_default);
        String yearForFilteringRequest = getActivity().getResources().getString(R.string.networking_year_2016_for_http_request);

        //We try to get their actual value from the Shared Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPreferences != null) {
            orderByVoteAverage = sharedPreferences.getBoolean(getString(R.string.settings_sort_by_vote_average_sharedpref), orderByVoteAverage);
            yearForFilteringRequest = sharedPreferences.getString(getString(R.string.settings_year_for_filtering_network_request_sharedpref), yearForFilteringRequest);
        }

        //If the user wants to order by vote average, then we start the Retrofit Service by calling the corresponding method
        if (orderByVoteAverage) {
            String voteAverage = getActivity().getString(R.string.networking_movies_list_vote_average_desc);
            return movieService.getThisYearsMovieListByVoteAverage(voteAverage, yearForFilteringRequest, getString(R.string.the_movie_database_web_api_key));
        }
        //If not, we start the Retrofit Service by calling the other method
        else {
            return movieService.getThisYearsMovieList(yearForFilteringRequest, getString(R.string.the_movie_database_web_api_key));
        }
    }


    //Method that returns the Callback used by the Retrofit Service
    private Callback<List<Movie>> gettingRetrofitCallbackObject() {
        return new Callback<List<Movie>>() {

            //Triggered if we get a response
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull retrofit2.Response<List<Movie>> response) {
                //We check that we have data in the response
                if (response != null && response.body().size() != 0) {
                    //If that is the case, we pass the data to the RecyclerView's adapter, and we show the RecyclerView
                    List<Movie> movies = response.body();
                    refreshAdapter(movies);
                    isShowingData();
                }
                //If we do not have data, we show the empty view
                else {
                    isNotShowingData(getString(R.string.base_movie_recyclerview_fragment_cannot_get_data), R.mipmap.error_icon);
                }
            }

            //Triggered if we get an error
            @Override
            public void onFailure(@NonNull Call<List<Movie>> call, @NonNull Throwable t) {
                isNotShowingData(getString(R.string.base_movie_recyclerview_fragment_cannot_get_data), R.mipmap.error_icon);
            }
        };
    }




    /* Shared Preferences callback */


    //Method triggered when the Shared Preferences change
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //We check that the Shared Preference that changed is one the following two
        if (key.equals(getString(R.string.settings_sort_by_vote_average_sharedpref)) || key.equals(getString(R.string.settings_year_for_filtering_network_request_sharedpref))) {
            //If that is the case and we are connected to internet, we start downloading
            if (NetworkUtils.isConnected(getActivity())) {
                startDownloading();
            }
            //If that is the case but we are not connected to internet, we show the empty view
            else {
                isNotShowingData(getString(R.string.broadcast_receiver_fragment_not_connected), R.mipmap.offline_icon);
            }
        }
    }

}
