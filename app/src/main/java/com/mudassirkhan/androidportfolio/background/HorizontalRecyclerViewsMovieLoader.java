package com.mudassirkhan.androidportfolio.background;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.utils.JsonUtils;

import static com.mudassirkhan.androidportfolio.ui.fragments.HorizontalCardViewsFragment.MOVIE_LOADER_ID_ARRAY;

public class HorizontalRecyclerViewsMovieLoader extends AsyncTaskLoader<String> {

    //Declaration of the HorizontalRecyclerViewsMovieLoader's member variables
    private Context mContext;
    private String mData;

    //HorizontalRecyclerViewsMovieLoader's Constructor
    public HorizontalRecyclerViewsMovieLoader(Context context) {
        super(context);
        mContext = context;
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

        String json = "";

        //We get the movies from one of the JSON Asset files, according to which instance of the Loader is started
        if (getId() == MOVIE_LOADER_ID_ARRAY[0]) {
            json = JsonUtils.loadJsonFromAssetFolder(mContext, mContext.getString(R.string.moviesJsonFile1));
        } else if (getId() == MOVIE_LOADER_ID_ARRAY[1]) {
            json = JsonUtils.loadJsonFromAssetFolder(mContext, mContext.getString(R.string.moviesJsonFile2));
        } else if (getId() == MOVIE_LOADER_ID_ARRAY[2]) {
            json = JsonUtils.loadJsonFromAssetFolder(mContext, mContext.getString(R.string.moviesJsonFile3));
        } else if (getId() == MOVIE_LOADER_ID_ARRAY[3]) {
            json = JsonUtils.loadJsonFromAssetFolder(mContext, mContext.getString(R.string.moviesJsonFile4));
        }
        return json;
    }

    //Method that saves the downloaded data for later retrieval
    @Override
    public void deliverResult(String data) {
        mData = data;
        super.deliverResult(data);
    }
}

