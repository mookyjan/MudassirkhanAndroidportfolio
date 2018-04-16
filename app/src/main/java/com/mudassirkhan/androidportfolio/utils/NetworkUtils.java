package com.mudassirkhan.androidportfolio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.models.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public final class NetworkUtils {

    //Static method used to retrieve a string response from an HTTP request
    public static String fetchMovieDataThroughNativeWay(String requestUrl) {
        //We create the URL object
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            //We make the HTTP request
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //We return the string JSON response
        return jsonResponse;
    }


    //Static method used to create an URL object from a string passed in as a parameter
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    //Static method used to make a native HTTP request using the URL object passed in as a parameter
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        //We create the necessary variables
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            //We create and open the HTTP connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                //We get the data from an input stream
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            //We clean everything up
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        //We return the JSON string response
        return jsonResponse;
    }


    //Static method that returns a string response from the input stream passed in as a parameter
    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    //Static method that returns the base URL to get movie data from The Movie Database's web api
    public static Uri.Builder getBaseWebApiUrl(Context context) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(context.getString(R.string.networking_scheme))
                .authority(context.getString(R.string.networking_movies_list_content_authority))
                .appendPath(context.getString(R.string.networking_movies_list_first_path))
                .appendPath(context.getString(R.string.networking_movies_list_second_path));
        return builder;
    }


    //Static method that returns the complete URL to get movie data from The Movie Database's web api
    public static String getCustomMovieUrl(Context context, int movieGenre) {

        //We get a reference to the Defaults Shared Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //We get the default values for the orderByVoteAverage and yearForFilteringRequest parameters
        boolean orderByVoteAverage = context.getResources().getBoolean(R.bool.network_movie_sorted_by_vote_average_default);
        String yearForFilteringRequest = context.getResources().getString(R.string.networking_year_2016_for_http_request);

        //We try to get their actual value from the Shared Preferences
        if (sharedPreferences != null) {
            orderByVoteAverage = sharedPreferences.getBoolean(context.getString(R.string.settings_sort_by_vote_average_sharedpref), orderByVoteAverage);
            yearForFilteringRequest = sharedPreferences.getString(context.getString(R.string.settings_year_for_filtering_network_request_sharedpref), yearForFilteringRequest);
        }

        //We build our custom URL, starting by the base URL
        Uri.Builder builder = getBaseWebApiUrl(context);
        builder.appendPath(context.getString(R.string.networking_movies_list_third_path));

        //We append the movie genre parameter and value, if not null
        if (movieGenre != 0) {
            builder.appendQueryParameter(context.getString(R.string.networking_movies_list_with_genres_parameter), Integer.toString(movieGenre));
        }

        //If the user wants to order by vote average, then we append the parameter and the value
        if (orderByVoteAverage) {
            builder.appendQueryParameter(context.getString(R.string.networking_movies_list_sort_by_parameter), context.getString(R.string.networking_movies_list_vote_average_desc));
        }

        //We append the release year parameter and value, which cannot be null
        builder.appendQueryParameter(context.getString(R.string.networking_movies_list_year_release_parameter), yearForFilteringRequest);

        //We append the api key parameter and value, to be able to access to the web api
        builder.appendQueryParameter(context.getString(R.string.networking_movies_list_api_key_parameter), context.getString(R.string.the_movie_database_web_api_key));

        //We return the string url we just built
        return builder.build().toString();
    }


    //Static method that returns the string URL to get the movie poster from the passed in string path
    public static String getWebApiPosterUrl(Context context, String poster_path) {
        String posterSize = context.getString(R.string.networking_image_fourth_poster_size);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(context.getString(R.string.networking_scheme))
                .authority(context.getString(R.string.networking_image_content_authority))
                .appendPath(context.getString(R.string.networking_image_first_path))
                .appendPath(context.getString(R.string.networking_image_second_path))
                .appendPath(posterSize)
                .appendPath(poster_path);
        return builder.build().toString();
    }


    //Static method used to determine whether a device is currently connected to the internet
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    //Static method that returns a JSON string response using the passed in string URL, through the Okhttp library
    public static String getJsonStringThroughOkhttp(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    //Interface used by Retrofit to make HTTP requests and get the movie data
    public interface MovieService {
        @GET("movie")
        Call<List<Movie>> getThisYearsMovieListByVoteAverage(@Query("sort_by") String voteAverage,
                                                             @Query("primary_release_year") String releaseYear,
                                                             @Query("api_key") String apiKey);


        @GET("movie")
        Call<List<Movie>> getThisYearsMovieList(@Query("primary_release_year") String releaseYear,
                                                @Query("api_key") String apiKey);
    }

}