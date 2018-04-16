package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.adapters.MovieAdapter;
import com.mudassirkhan.androidportfolio.background.MovieLoader;
import com.mudassirkhan.androidportfolio.models.Movie;
import com.mudassirkhan.androidportfolio.ui.uielements.RecyclerViewItemDecoration;
import com.mudassirkhan.androidportfolio.utils.DataUtils;
import com.mudassirkhan.androidportfolio.utils.NetworkUtils;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMovieRecyclerViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>,
        MovieAdapter.OnMovieItemClickListener,
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //Declaration of the RecyclerView's member variables
    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovieList;
    private List<Movie> mMovieListForAddingItems;
    private boolean mIsRecyclerViewEditable;

    //Declaration of connection related member variables
    private NetworkBroadcastReceiver mNetworkBroadcastReceiver;
    private boolean mGettingDataFromNetwork;

    //Declaration of Loader related member variables
    private int mLoaderId;
    private boolean mIsLoaderAlreadyStarted;

    //Declaration of Alert Dialog related member variables
    private AlertDialog mOverviewAlertDialog;
    private AlertDialog mDeleteAlertDialog;

    //Declaration of other views' member variables
    private View mFragmentRootView;
    private ProgressBar mDownloadingIndicator;
    private LinearLayout mEmptyStateLinearLayout;
    private TextView mEmptyStateTextView;
    private ImageView mEmptyStateImageView;
    private FloatingActionButton mFloatingActionButton;


    //Empty constructor
    public BaseMovieRecyclerViewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        //Setting up the Loading Indicator
        setUpLoadingIndicator();

        //Setting up the Empty View
        setUpEmptyView();

        //Setting up the Network Broadcast Receiver
        setUpBroadcastReceiver();

        //Setting up the RecyclerView
        setUpRecyclerView();

        //Setting up the Floating Action Button
        setUpFab();

        //Setting up the Alert Dialogs
        setUpAlertDialogs();

        //Setting up the SharedPreferences listener
        setUpSharedPreferences();

        //Setting up the Loader related stuff, if needed.
        setUpLoaderOrNormalThread();

        return mFragmentRootView;
    }



    /* Setting up the Fragment */


    //Setting up the Loading Indicator
    private void setUpLoadingIndicator() {
        //Getting the Loading Indicator's reference
        mDownloadingIndicator = (ProgressBar) mFragmentRootView.findViewById(R.id.downloading_indicator);
    }


    //Setting up the Empty View
    private void setUpEmptyView() {
        //Getting the Empty View's view references
        mEmptyStateLinearLayout = (LinearLayout) mFragmentRootView.findViewById(R.id.empty_state_linear_layout);
        mEmptyStateTextView = (TextView) mFragmentRootView.findViewById(R.id.empty_state_textview);
        mEmptyStateImageView = (ImageView) mFragmentRootView.findViewById(R.id.empty_state_imageview);
    }


    //Setting up the Network Broadcast Receiver
    private void setUpBroadcastReceiver() {
        mGettingDataFromNetwork = gettingDataFromInternet();
        if (mGettingDataFromNetwork) {
            //In case we are getting data from internet, we instantiate the Network Broadcast Receiver
            mNetworkBroadcastReceiver = new NetworkBroadcastReceiver();
        }
    }


    //Setting up the RecyclerView
    private void setUpRecyclerView() {
        //Getting the RecyclerView's reference
        mMoviesRecyclerView = (RecyclerView) mFragmentRootView.findViewById(R.id.base_fragment_recyclerview);

        //Getting, from the inheriting fragments, the layout we will be using
        int layout = setUpRecyclerViewItemLayout();

        //Instantiating the MovieAdapter, with empty list of data
        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>(), layout, mGettingDataFromNetwork, this, 0, hideGenreViewInPortraitInCaseItExists());

        //Instantiating the LayoutManager, from the inheriting fragments
        RecyclerView.LayoutManager moviesLayoutManager = getMovieRecyclerViewLayoutManager();

        //Wiring up the RecyclerView
        mMoviesRecyclerView.setHasFixedSize(true);
        mMoviesRecyclerView.setLayoutManager(moviesLayoutManager);
        if (useRecyclerViewItemDivider()) {
            mMoviesRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(getActivity(), R.drawable.recyclerviews_line_divider));
        }
        mMoviesRecyclerView.setAdapter(mMovieAdapter);

        //With this boolean, if its value is true, we authorize the user to add and delete movies from the list
        mIsRecyclerViewEditable = makeRecyclerViewEditable();
    }


    //Setting up the Floating Action Button
    private void setUpFab() {
        //Getting the Floating Action Button's reference
        mFloatingActionButton = (FloatingActionButton) mFragmentRootView.findViewById(R.id.recyclerview_fragments_fab);

        //If the RecyclerView is not declared as editable, then we make the Floating Action Button disappear
        if (!mIsRecyclerViewEditable) {
            mFloatingActionButton.setVisibility(View.GONE);
        }

        //Getting the color in which we will paint the Floating Action Button
        Intent intent = getActivity().getIntent();
        int activityAccentColor = ContextCompat.getColor(getActivity(), intent.getIntExtra(
                getActivity().getString(R.string.main_from_detail_activity_background_color_tag), 0));

        //Painting the Floating Action Button
        mFloatingActionButton.setColorNormal(activityAccentColor);
        mFloatingActionButton.setColorPressed(activityAccentColor);
    }


    //Setting up the Alert Dialogs
    private void setUpAlertDialogs() {
        //Instantiating the Alert Dialogs
        mOverviewAlertDialog = new AlertDialog.Builder(getActivity()).create();
        mDeleteAlertDialog = new AlertDialog.Builder(getActivity()).create();
    }


    //Setting up the SharedPreferences listener
    private void setUpSharedPreferences() {
        //Registering the OnSharedPreferenceChangeListener, that will be used in some of the inheriting fragments
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }


    //Method called to set up all the Loader related stuff, in case we use a Loader. Otherwise, we will be using the normal Thread
    private void setUpLoaderOrNormalThread() {
        //We get the Loader's ID from the inheriting fragment
        mLoaderId = getLoaderId();

        //We determine, in the inheriting fragments, if we will be using a Loader
        boolean usingBackgroundLoader = usingAsynkTaskLoader();

        //If we are getting data from internet and we are using a Loader, then we check if we are connected to internet
        if (mGettingDataFromNetwork && usingBackgroundLoader) {
            //If we are connected, then we start the Loader
            if (NetworkUtils.isConnected(getActivity())) {
                startLoader();
            }
            //If we are not connected, then we show the Empty View
            else {
                isNotShowingData(getString(R.string.broadcast_receiver_fragment_not_connected), R.mipmap.offline_icon);
            }
        }
        //If we are not getting data from internet and we are using a Loader, then we start the Loader directly
        else if (!mGettingDataFromNetwork && usingBackgroundLoader) {
            startLoader();
        }
        //If we are not using a Loader, then we will execute some code from this thread
        else if (!usingBackgroundLoader) {
            normalThreadAction();
        }
    }



    /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //When the Floating Action Button is clicked, then we add a randomly selected movie to the RecyclerView
            case R.id.recyclerview_fragments_fab:
                addMovieToList();
                break;
        }
    }


    //Handling when a Movie item is clicked
    @Override
    public void onMovieItemClick(Movie movie, int position, View view) {
        //We get the movie info
        String movieTitle = movie.getTitle();
        String movieOverview = movie.getOverview();
        List<Integer> genreListIds = movie.getGenreList();
        String genreString = DataUtils.getMovieTypeListString(getContext(), genreListIds);

        //Then, if the Dialog is not already showing, we show it with the info we just got
        if (!mOverviewAlertDialog.isShowing()) {
            showMovieOverviewSimpleAlertDialog(movieTitle, genreString, movieOverview);
        }
    }


    //Handling when a Movie item is long clicked
    @Override
    public void onMovieItemLongClick(Movie movie, int position, int recyclerviewId) {
        if (mIsRecyclerViewEditable) {
            //If the RecyclerView is declared as editable, when a movie item is long clicked, we show the Delete Dialog
            showOnLongClickDeletingItemAlertDialog(movie, position);
        }
    }



    /* Helper methods */


    //Method that toggles between the layout's views to make the Loading Indicator visible
    protected void isDownloading() {
        if (mMoviesRecyclerView.getVisibility() == View.VISIBLE) {
            mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        }
        if (mEmptyStateLinearLayout.getVisibility() == View.VISIBLE) {
            mEmptyStateLinearLayout.setVisibility(View.INVISIBLE);
        }
        if (mDownloadingIndicator.getVisibility() != View.VISIBLE) {
            mDownloadingIndicator.setVisibility(View.VISIBLE);
        }
    }


    //Method that toggles between the layout's views to make the RecyclerView visible
    protected void isShowingData() {
        if (mMoviesRecyclerView.getVisibility() != View.VISIBLE) {
            mMoviesRecyclerView.setVisibility(View.VISIBLE);
        }
        if (mEmptyStateLinearLayout.getVisibility() == View.VISIBLE) {
            mEmptyStateLinearLayout.setVisibility(View.INVISIBLE);
        }
        if (mDownloadingIndicator.getVisibility() == View.VISIBLE) {
            mDownloadingIndicator.setVisibility(View.INVISIBLE);
        }
    }


    //Method that toggles between the layout's views to make the Empty View visible and bind data to it
    protected void isNotShowingData(String emptyTextViewContent, int emptyViewIconImage) {

        //We bind data to our empty view
        Picasso.with(getActivity()).load(emptyViewIconImage).into(mEmptyStateImageView);
        mEmptyStateTextView.setText(emptyTextViewContent);

        if (mEmptyStateLinearLayout.getVisibility() != View.VISIBLE) {
            mEmptyStateLinearLayout.setVisibility(View.VISIBLE);
        }
        if (mDownloadingIndicator.getVisibility() == View.VISIBLE) {
            mDownloadingIndicator.setVisibility(View.INVISIBLE);
        }

        if (mMoviesRecyclerView.getVisibility() == View.VISIBLE) {
            mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        }
        mMovieAdapter.swapData(null);
    }


    //Method that will start the Loader, when the Loader is used
    protected void startLoader() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(mLoaderId, null, this);
        isDownloading();
        scrollToTop();
        mIsLoaderAlreadyStarted = true;
    }


    //Method that will stop the Loader
    protected void stopLoader() {
        mIsLoaderAlreadyStarted = false;
        getLoaderManager().destroyLoader(mLoaderId);
    }


    //Method that will restart the Loader
    protected void restartLoader() {
        if (!mIsLoaderAlreadyStarted) {
            getLoaderManager().restartLoader(mLoaderId, null, this);
            scrollToTop();
            isDownloading();
            mIsLoaderAlreadyStarted = true;
        }
    }


    //Method that will scroll the RecyclerView to its top
    protected void scrollToTop() {
        mMoviesRecyclerView.scrollToPosition(0);
    }


    //Method that will randomly add a movie to the list
    private void addMovieToList() {
        mMovieList.add(0, DataUtils.getRandomMovie(mMovieListForAddingItems));
        mMovieAdapter.swapData(mMovieList);
        scrollToTop();
    }


    //Method that will remove a movie from the list
    public void removeMoveFromList(int position) {
        mMovieList.remove(position);
        mMovieAdapter.swapData(mMovieList);
    }


    //Method that will refresh the MovieAdapter with a new data set
    protected void refreshAdapter(List<Movie> movies) {
        mMovieAdapter.swapData(movies);
    }


    //Method that will show the Overview Dialog
    private void showMovieOverviewSimpleAlertDialog(String movieTitle, String genreList, String movieOverview) {
        mOverviewAlertDialog = DataUtils.getSimpleClickAlertDialogBuilder(getActivity(), movieTitle, genreList, movieOverview);
        mOverviewAlertDialog.show();
    }


    //Method that will show the Delete Dialog
    private void showOnLongClickDeletingItemAlertDialog(Movie movie, final int position) {
        mDeleteAlertDialog = DataUtils.getLongClickAlertDialogBuilder(getActivity(), this, movie, position);
        mDeleteAlertDialog.show();
    }




    /* Loader callbacks */



    //When the Loader is created, this method returns a MovieLoader instance
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Fragment currentLoadingFragment = this;
        return new MovieLoader(getActivity(), currentLoadingFragment);
    }


    //Triggered when Loader is reset
    @Override
    public void onLoaderReset(Loader<String> loader) {
        mMovieAdapter.swapData(null);
    }


    //Triggered when loading is finished
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        //When the loading is finished, we make sure that we have data
        if (data != null && !data.isEmpty()) {

            //If we do have data, we will get the list of movies corresponding to it and refresh the adapter to show the brand new data
            List<Movie> movies = chooseLoaderCallbackParsingMethod(data);
            mMovieList = movies;
            mMovieListForAddingItems = chooseLoaderCallbackParsingMethod(data);
            refreshAdapter(movies);

            //Only in this case, we set the click listener on the Floating Action Button (If we had done it before, it could have led to a crash)
            if (mIsRecyclerViewEditable) {
                mFloatingActionButton.setOnClickListener(this);
            }

            //We show the data
            isShowingData();
        }
        //If we do not have data, then we show the empty view
        else {
            isNotShowingData(getString(R.string.base_movie_recyclerview_fragment_cannot_get_data), R.mipmap.error_icon);
        }
    }



    /* Fragment Lifecycle callbacks */



    //When the Fragment is resumed, we register our Network Broadcast Receiver
    @Override
    public void onResume() {
        super.onResume();
        if (mGettingDataFromNetwork) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadcastReceiversFragment.BROADCAST_RECEIVER_NETWORK_STATUS_ACTION[0]);
            getActivity().registerReceiver(mNetworkBroadcastReceiver, filter);
            mNetworkBroadcastReceiver.isRegistered = true;
        }
    }


    //When the Fragment is stopped, we dismiss the dialogs to prevent painful crashes when the device is being rotated
    //and we unregister the Network Broadcast Receiver
    @Override
    public void onStop() {
        super.onStop();
        if (mOverviewAlertDialog != null) {
            mOverviewAlertDialog.dismiss();
        }
        if (mDeleteAlertDialog != null) {
            mDeleteAlertDialog.dismiss();
        }
        if (mGettingDataFromNetwork && mNetworkBroadcastReceiver.isRegistered) {
            getActivity().unregisterReceiver(mNetworkBroadcastReceiver);
            mNetworkBroadcastReceiver.isRegistered = false;
        }
    }


    //When the Fragment is destroyed, we unregister the OnSharedPreferenceChangeListener
    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }




    /* Abstract methods implemented in the inheriting fragments */


    //Abstract method where we decide whether or not we get the data from internet
    protected abstract boolean gettingDataFromInternet();

    //Abstract method where we decide whether we use a Loader
    protected abstract boolean usingAsynkTaskLoader();

    //Abstract method where the way we get the data will be defined, in case we are not using a Loader
    protected abstract void normalThreadAction();

    //Abstract method where the way we get the data will be defined, in case we are using a Loader
    public abstract String loaderBackgroundAction();

    //Abstract method where we get the Loader's ID, in case we use a Loader
    protected abstract int getLoaderId();

    //Abstract method where we choose the way we will parse the data, in case we use a Loader
    protected abstract List<Movie> chooseLoaderCallbackParsingMethod(String data);

    //Abstract method where we implement the Action that will execute the Broadcast Receiver, in case we get the data from internet
    protected abstract void broadcastReceiverAction();

    //Abstract method where we decide what the RecyclerView's items layout will be
    protected abstract int setUpRecyclerViewItemLayout();

    //Abstract method where we decide what type of Layout Manager we will be using
    protected abstract RecyclerView.LayoutManager getMovieRecyclerViewLayoutManager();

    //Abstract method where we decide whether or not we will be using an item divider in our RecyclerView
    protected abstract boolean useRecyclerViewItemDivider();

    //Abstract method where we decide whether or not we want to hide the GenreView in Portrait mode, in case it exists
    protected abstract boolean hideGenreViewInPortraitInCaseItExists();

    //Abstract method where we decide whether we want to make the RecyclerView editable
    protected abstract boolean makeRecyclerViewEditable();


    //Broadcast Receiver used to handle connectivity changes in our inheriting fragments
    class NetworkBroadcastReceiver extends BroadcastReceiver {

        public boolean isRegistered;

        //Triggered when the connectivity changes
        @Override
        public void onReceive(Context context, Intent intent) {
            //If we are getting the data from internet, the device is connected, and we do not have data yet, we trigger the broadcastReceiverAction method
            if (mGettingDataFromNetwork && NetworkUtils.isConnected(getActivity()) && mMovieAdapter.getItemCount() == 0) {
                broadcastReceiverAction();
            }
            //If we are getting data from internet, the device is not connected, and we do not have data yet, then we show our empty view
            else if (mGettingDataFromNetwork && !NetworkUtils.isConnected(getActivity()) && mMovieAdapter.getItemCount() == 0) {
                isNotShowingData(getString(R.string.broadcast_receiver_fragment_not_connected), R.mipmap.offline_icon);
            }
        }
    }
}
