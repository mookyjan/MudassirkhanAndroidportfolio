package com.mudassirkhan.androidportfolio.adapters;


import android.content.Context;
import android.content.res.Configuration;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.models.Movie;
import com.mudassirkhan.androidportfolio.utils.DataUtils;
import com.mudassirkhan.androidportfolio.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    //Declaration of MovieAdapter's member variables
    private Context mContext;
    private List<Movie> mMovies;
    private int mLayout;
    private boolean mGettingDataFromNetwork;
    private MovieAdapter.OnMovieItemClickListener mListener;
    private int mRecyclerViewId;
    private boolean mHideGenreViewInPortraitInCaseItExists;

    //MovieAdapter's Constructor
    public MovieAdapter(Context context, List<Movie> movies, int layout, boolean gettingDataFromNetwork, OnMovieItemClickListener listener, int recyclerViewId, boolean hideGenreViewInPortraitInCaseItExists) {
        mContext = context;
        mMovies = movies;
        mLayout = layout;
        mGettingDataFromNetwork = gettingDataFromNetwork;
        mListener = listener;
        mRecyclerViewId = recyclerViewId;
        mHideGenreViewInPortraitInCaseItExists = hideGenreViewInPortraitInCaseItExists;
    }

    //Creation of the View Holder
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(mContext).inflate(mLayout, parent, false);
        return new MovieViewHolder(layoutView);
    }

    //Data binding
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        //Getting Movie object for the current adapter position
        Movie movie = mMovies.get(position);

        //Getting data out of the object
        String title = movie.getTitle();
        String poster_path = movie.getPosterPath();
        float voteAverage = (float) movie.getVoteAverage();
        String releaseDate = movie.getReleaseDate();
        String genres = DataUtils.getMovieTypeListString(mContext, movie.getGenreList());

        //Binding movie details to the item's layout
        holder.mTitle.setText(title);
        holder.mVoteAverage.setRating(voteAverage / 2);
        holder.mReleaseDate.setText(DataUtils.changingDateFormat(releaseDate));

        //Binding image to the Image View
        ImageView posterImageView = holder.mMoviePoster;
        String posterUrl = null;

        if (mGettingDataFromNetwork) { //If we get data from network

            if (poster_path != null && !poster_path.isEmpty()) {
                poster_path = poster_path.substring(1);
            }

            posterUrl = NetworkUtils.getWebApiPosterUrl(mContext, poster_path);
            Picasso.with(mContext).load(posterUrl).placeholder(R.drawable.downloading_placeholder).into(posterImageView);

        } else { //If we get data from local JSON files

            int posterId = mContext.getResources().getIdentifier(poster_path, "drawable", mContext.getPackageName());
            if (posterId != 0) {
                Picasso.with(mContext).load(posterId).into(posterImageView);
            }

        }

        //Binding Genre information to the item's layout, and hide it if we are in Portrait mode, since it does not fit
        TextView genresTextView = holder.mGenres;
        if (genresTextView != null) {
            if (mHideGenreViewInPortraitInCaseItExists && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                genresTextView.setVisibility(View.GONE);
            } else {
                genresTextView.setText(genres);
            }

        }

    }

    //Getting the count of the data source elements
    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.size();
    }

    //Method that allows us to refresh the data source from outside of the adapter
    public void swapData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    //Inner class defining the MovieViewHolder
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        //Declaration of the View Holder member variables
        private ImageView mMoviePoster;
        private TextView mTitle;
        private RatingBar mVoteAverage;
        private TextView mReleaseDate;
        private TextView mGenres;

        //MovieViewHolder's Constructor
        MovieViewHolder(View itemView) {
            super(itemView);

            //Getting references for the views from the layout
            mMoviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            mTitle = (TextView) itemView.findViewById(R.id.movie_title);
            mVoteAverage = (RatingBar) itemView.findViewById(R.id.movie_vote_average);
            mReleaseDate = (TextView) itemView.findViewById(R.id.movie_release_date);
            mGenres = (TextView) itemView.findViewById(R.id.movie_genres);

            //Setting Click Listeners on the item layout
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        //Implementation of the Adapter part of the Item Click Listener methods
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Movie clickedMovie = mMovies.get(clickedPosition);
            mListener.onMovieItemClick(clickedMovie, clickedPosition, itemView);
        }

        @Override
        public boolean onLongClick(View v) {
            int clickedPosition = getAdapterPosition();
            Movie clickedMovie = mMovies.get(clickedPosition);
            mListener.onMovieItemLongClick(clickedMovie, clickedPosition, mRecyclerViewId);
            return true;
        }
    }

    //Declaration of an Item Click Listener
    public interface OnMovieItemClickListener {
        void onMovieItemClick(Movie movie, int position, View view);

        void onMovieItemLongClick(Movie movie, int position, int recyclerViewId);
    }

}
