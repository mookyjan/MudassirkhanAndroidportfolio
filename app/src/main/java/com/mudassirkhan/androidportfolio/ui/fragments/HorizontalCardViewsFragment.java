package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.adapters.MovieAdapter;
import com.mudassirkhan.androidportfolio.background.HorizontalRecyclerViewsMovieLoader;
import com.mudassirkhan.androidportfolio.models.Movie;
import com.mudassirkhan.androidportfolio.utils.DataUtils;
import com.mudassirkhan.androidportfolio.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class HorizontalCardViewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, MovieAdapter.OnMovieItemClickListener, View.OnClickListener {

    //Declaration, as a constant String array, of the Loaders' IDs
    public static final int[] MOVIE_LOADER_ID_ARRAY = {31, 32, 33, 34};

    //Declaration of the RecyclerViews' member variables
    private RecyclerView[] mRecyclerViewArray;
    private MovieAdapter[] mMovieAdapterArray;
    private List[] mMovieListArray;
    private List[] mMovieListArrayForAddingItems;

    //Declaration of the Floating Action Buttons's member variables
    private FloatingActionButton[] mFloatingActionButtonArray;

    //Declaration of the Alert Dialogs' member variables
    private AlertDialog mOverviewAlertDialog;
    private AlertDialog mDeleteAlertDialog;

    //Declaration of the fragment's root view's member variable
    private View mFragmentRootView;


    //Empty constructor
    public HorizontalCardViewsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_horizontal_recyclerviews, container, false);

        //Setting up the RecyclerViews
        setUpRecyclerViews();

        //Setting up the Floating Action Buttons
        setUpFabs();

        //Setting up the OverView Alert Dialog
        setUpAlertDialog();

        //Starting the Loaders
        startLoaders();

        return mFragmentRootView;
    }



    /* Setting up the Fragment */


    //Setting up the RecyclerViews, using a loop and arrays to avoid writing some repeating code
    private void setUpRecyclerViews() {

        //Instantiating the RecyclerView related arrays
        mRecyclerViewArray = new RecyclerView[4];
        RecyclerView.LayoutManager[] recyclerViewLayoutManagerArray = new RecyclerView.LayoutManager[4];
        mMovieAdapterArray = new MovieAdapter[4];
        mMovieListArray = new List[4];
        mMovieListArrayForAddingItems = new List[4];

        for (int i = 0; i < mRecyclerViewArray.length; i++) {
            //Getting the RecyclerViews' references
            int recyclerViewId = getResources().getIdentifier("horizontal_recyclerviews_fragment_number" + i, "id", getActivity().getPackageName());
            mRecyclerViewArray[i] = (RecyclerView) mFragmentRootView.findViewById(recyclerViewId);

            //Instantiating the MovieAdapters, with empty lists
            mMovieAdapterArray[i] = new MovieAdapter(getActivity(), new ArrayList<Movie>(), R.layout.movie_item_cardview_for_horizontal_recyclerviews, false, this, recyclerViewId, false);

            //Instantiating the LayoutManagers
            recyclerViewLayoutManagerArray[i] = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

            //Wiring up the RecyclerViews
            mRecyclerViewArray[i].setHasFixedSize(true);
            mRecyclerViewArray[i].setNestedScrollingEnabled(false);
            mRecyclerViewArray[i].setLayoutManager(recyclerViewLayoutManagerArray[i]);
            mRecyclerViewArray[i].setAdapter(mMovieAdapterArray[i]);
        }
    }


    //Setting up the Floating Action Buttons
    private void setUpFabs() {
        //Instantiating the Floating Action Button related array
        mFloatingActionButtonArray = new FloatingActionButton[4];

        //Getting the Floating Action Buttons' references
        for (int i = 0; i < mFloatingActionButtonArray.length; i++) {
            int fabId = getResources().getIdentifier("horizontal_recyclerview_fragments_fab" + i, "id", getActivity().getPackageName());
            mFloatingActionButtonArray[i] = (FloatingActionButton) mFragmentRootView.findViewById(fabId);
        }
    }


    //Setting up the Overview Alert Dialog
    private void setUpAlertDialog() {
        mOverviewAlertDialog = new AlertDialog.Builder(getActivity()).create();
    }


    //Starting the Loaders
    private void startLoaders() {
        //We get a reference to the Loader Manager
        LoaderManager loaderManager = getLoaderManager();

        //We start the Loaders
        for (int i = 0; i < MOVIE_LOADER_ID_ARRAY.length; i++) {
            loaderManager.initLoader(MOVIE_LOADER_ID_ARRAY[i], null, this);
            mRecyclerViewArray[i].scrollToPosition(0);
        }
    }



    /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //When clicked on the first Floating Action Button, we add a random movie to the first list
            case R.id.horizontal_recyclerview_fragments_fab0:
                addMovieToList(0);
                break;

            //When clicked on the second Floating Action Button, we add a random movie to the second list
            case R.id.horizontal_recyclerview_fragments_fab1:
                addMovieToList(1);
                break;

            //When clicked on the third Floating Action Button, we add a random movie to the third list
            case R.id.horizontal_recyclerview_fragments_fab2:
                addMovieToList(2);
                break;

            //When clicked on the fourth Floating Action Button, we add a random movie to the fourth list
            case R.id.horizontal_recyclerview_fragments_fab3:
                addMovieToList(3);
                break;
        }

    }


    //Handling when a movie item is clicked
    @Override
    public void onMovieItemClick(Movie movie, int position, View view) {
        //We get the movie's data out of the movie object
        String movieTitle = movie.getTitle();
        String movieOverview = movie.getOverview();
        List<Integer> genreListIds = movie.getGenreList();
        String genreString = DataUtils.getMovieTypeListString(getActivity(), genreListIds);

        //We open the Overview Alert Dialog, displaying the corresponding data
        if (!mOverviewAlertDialog.isShowing()) {
            showMovieOverviewSimpleAlertDialog(movieTitle, genreString, movieOverview);
        }
    }


    //Handling when a movie item is long clicked
    @Override
    public void onMovieItemLongClick(Movie movie, int position, int recyclerViewId) {

        switch (recyclerViewId) {

            //When long clicked on the first movie item, we show the Delete Alert Dialog, proposing to delete it
            case R.id.horizontal_recyclerviews_fragment_number0:
                showOnLongClickDeletingItemAlertDialog(movie, position, 0);
                break;

            //When long clicked on the second movie item, we show the Delete Alert Dialog, proposing to delete it
            case R.id.horizontal_recyclerviews_fragment_number1:
                showOnLongClickDeletingItemAlertDialog(movie, position, 1);
                break;

            //When long clicked on the third movie item, we show the Delete Alert Dialog, proposing to delete it
            case R.id.horizontal_recyclerviews_fragment_number2:
                showOnLongClickDeletingItemAlertDialog(movie, position, 2);
                break;

            //When long clicked on the fourth movie item, we show the Delete Alert Dialog, proposing to delete it
            case R.id.horizontal_recyclerviews_fragment_number3:
                showOnLongClickDeletingItemAlertDialog(movie, position, 3);
                break;
        }

    }



    /* Loader callbacks */


    //When the Loader is created, this method returns a HorizontalRecyclerViewsMovieLoader instance
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new HorizontalRecyclerViewsMovieLoader(getActivity());
    }


    //Triggered when Loader is reset
    @Override
    public void onLoaderReset(Loader<String> loader) {

        //If Loader 1 is reset, we reset its data set
        if (loader.getId() == MOVIE_LOADER_ID_ARRAY[0]) {
            mMovieAdapterArray[0].swapData(null);
        }
        //If Loader 2 is reset, we reset its data set
        else if (loader.getId() == MOVIE_LOADER_ID_ARRAY[1]) {
            mMovieAdapterArray[1].swapData(null);
        }
        //If Loader 3 is reset, we reset its data set
        else if (loader.getId() == MOVIE_LOADER_ID_ARRAY[2]) {
            mMovieAdapterArray[2].swapData(null);
        }
        //If Loader 4 is reset, we reset its data set
        else if (loader.getId() == MOVIE_LOADER_ID_ARRAY[3]) {
            mMovieAdapterArray[3].swapData(null);
        }
    }


    //Triggered when loading is finished
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        //We make sure that the returned data is neither null nor empty
        if (data != null && !data.isEmpty()) {

            //We get movie lists out of it
            List<Movie> movies = JsonUtils.getMovieListFromJsonThroughGson(getActivity(), data);
            List<Movie> moviesForAddingItems = JsonUtils.getMovieListFromJsonThroughGson(getActivity(), data);

            //If the Loader that just finished loading is the first one, we then set its RecyclerView related stuff
            if (loader.getId() == MOVIE_LOADER_ID_ARRAY[0]) {
                mMovieListArray[0] = movies;
                mMovieListArrayForAddingItems[0] = moviesForAddingItems;
                mMovieAdapterArray[0].swapData(movies);
                mFloatingActionButtonArray[0].setOnClickListener(this);
            }

            //If the Loader that just finished loading is the second one, we then set its RecyclerView related stuff
            else if (loader.getId() == MOVIE_LOADER_ID_ARRAY[1]) {
                mMovieListArray[1] = movies;
                mMovieListArrayForAddingItems[1] = moviesForAddingItems;
                mMovieAdapterArray[1].swapData(movies);
                mFloatingActionButtonArray[1].setOnClickListener(this);
            }

            //If the Loader that just finished loading is the third one, we then set its RecyclerView related stuff
            else if (loader.getId() == MOVIE_LOADER_ID_ARRAY[2]) {
                mMovieListArray[2] = movies;
                mMovieListArrayForAddingItems[2] = moviesForAddingItems;
                mMovieAdapterArray[2].swapData(movies);
                mFloatingActionButtonArray[2].setOnClickListener(this);
            }

            //If the Loader that just finished loading is the fourth one, we then set its RecyclerView related stuff
            else if (loader.getId() == MOVIE_LOADER_ID_ARRAY[3]) {
                mMovieListArray[3] = movies;
                mMovieListArrayForAddingItems[3] = moviesForAddingItems;
                mMovieAdapterArray[3].swapData(movies);
                mFloatingActionButtonArray[3].setOnClickListener(this);
            }
        }
    }




    /* Helper methods */


    //Method used to add a random movie to the RecyclerView corresponding to the clicked Floating Action Button
    private void addMovieToList(int clickedFabPosition) {
        //We add the movie to the corresponding list
        mMovieListArray[clickedFabPosition].add(0, DataUtils.getRandomMovie(mMovieListArrayForAddingItems[clickedFabPosition]));

        //We update the corresponding MovieAdapter
        mMovieAdapterArray[clickedFabPosition].swapData(mMovieListArray[clickedFabPosition]);

        //In case we come from having no movies in the list to having one, we need to set the list layout's height to WRAP_CONTENT again
        if (mMovieListArray[clickedFabPosition].size() == 1) {
            ViewGroup.LayoutParams params = mRecyclerViewArray[clickedFabPosition].getLayoutParams();
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            mRecyclerViewArray[clickedFabPosition].setLayoutParams(params);
        }
        //If there were already movie items in the list before adding, we just scroll back to the begging of the RecyclerView
        else {
            mRecyclerViewArray[clickedFabPosition].smoothScrollToPosition(0);
        }
    }


    //Method used to delete a movie item from a RecyclerView
    private void removeMoveFromList(int position, int recyclerviewPosition) {
        mMovieListArray[recyclerviewPosition].remove(position);
        mMovieAdapterArray[recyclerviewPosition].swapData(mMovieListArray[recyclerviewPosition]);
    }


    //Method used to show the Overview Alert Dialog
    private void showMovieOverviewSimpleAlertDialog(String movieTitle, String genreList, String movieOverview) {
        mOverviewAlertDialog = DataUtils.getSimpleClickAlertDialogBuilder(getActivity(), movieTitle, genreList, movieOverview);
        mOverviewAlertDialog.show();
    }


    //Method used to show the Delete Alert Dialog
    private void showOnLongClickDeletingItemAlertDialog(Movie movie, final int position, final int recyclerviewPosition) {
        //Creating the Delete Alert Dialog instance
        mDeleteAlertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(movie.getTitle())
                .setMessage(getString(R.string.various_fragments_delete_dialog_delete_confirmation))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Handling the user's response and deleting the chosen movie item
                        removeMoveFromList(position, recyclerviewPosition);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create();

        //Showing the Delete Alert Dialog
        mDeleteAlertDialog.show();
    }



    /* Fragment Lifecycle callbacks */


    //When the Fragment is stopped, we dismiss the dialogs to prevent painful crashes when the device is being rotated
    @Override
    public void onStop() {
        super.onStop();
        if (mOverviewAlertDialog != null) {
            mOverviewAlertDialog.dismiss();
        }
        if (mDeleteAlertDialog != null) {
            mDeleteAlertDialog.dismiss();
        }
    }
}
