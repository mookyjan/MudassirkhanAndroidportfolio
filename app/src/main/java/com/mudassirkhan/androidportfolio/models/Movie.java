package com.mudassirkhan.androidportfolio.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie {

    //Declaration of Movie object's member variables
    @SerializedName("title")
    private String mTitle;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("vote_average")
    private double mVoteAverage;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("genre_ids")
    private List<Integer> mGenreList;

    //Movie object's constructor
    public Movie(String title, String overview, String posterPath, double voteAverage, String releaseDate, List<Integer> genreList) {
        mTitle = title;
        mOverview = overview;
        mPosterPath = posterPath;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mGenreList = genreList;
    }


    //Movie's getters
    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public List<Integer> getGenreList() {
        return mGenreList;
    }
}





