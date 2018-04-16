package com.mudassirkhan.androidportfolio.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.application.MyApplication;
import com.mudassirkhan.androidportfolio.models.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JsonUtils {

    //Static method that returns a list of movie objects from the JSON string passed in as a parameter
    public static List<Movie> getMovieListNativelyFromJson(Context context, String movieJson) {

        if (movieJson == null || TextUtils.isEmpty(movieJson)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();

        try {

            //We create the root JSONObject from the JSON string
            JSONObject baseJsonResponse = new JSONObject(movieJson);

            //We create a JSONArray containing the list of movie JSONObjects
            JSONArray movieArray = baseJsonResponse.getJSONArray(context.getString(R.string.json_file_properties_results_array));

            //We iterate through every one of these movie JSONObjects
            for (int i = 0; i < movieArray.length(); i++) {

                //We get the current movie JSONObject
                JSONObject currentMovie = movieArray.getJSONObject(i);

                //We get its properties
                String title = currentMovie.getString(context.getString(R.string.json_file_properties_title));
                String posterPath = currentMovie.getString(context.getString(R.string.json_file_properties_poster_path));
                double voteAverage = currentMovie.getDouble(context.getString(R.string.json_file_properties_vote_average));
                String overview = currentMovie.getString(context.getString(R.string.json_file_properties_overview));
                String releaseDate = currentMovie.getString(context.getString(R.string.json_file_properties_release_date));

                JSONArray genreIdsJsonArray = currentMovie.getJSONArray(context.getString(R.string.json_file_properties_genre_ids));
                List<Integer> genreIdsNormalArrayList = new ArrayList<>();
                for (int j = 0; j < genreIdsJsonArray.length(); j++) {
                    genreIdsNormalArrayList.add(genreIdsJsonArray.getInt(j));
                }

                //We create a movie object, from our model class, and add it to our list
                movies.add(new Movie(title, overview, posterPath, voteAverage, releaseDate, genreIdsNormalArrayList));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //We return the movie list
        return movies;
    }


    //Static method that returns a list of movie objects, obtained through GSON
    public static List<Movie> getMovieListFromJsonThroughGson(Context context, String movieJson) {
        String arrayPropertyKey = context.getString(R.string.json_file_properties_results_array);
        return getMovieListFromJsonThroughTypeToken(movieJson, arrayPropertyKey);
    }


    //Static method that returns a list of movie objects, deserializing through GSON
    private static List<Movie> getMovieListFromJsonThroughTypeToken(String movieJson, String arrayPropertyKey) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(movieJson).getAsJsonObject();
        JsonArray movieListJsonArray = jsonObject.getAsJsonArray(arrayPropertyKey);
        Type movieListType = new TypeToken<List<Movie>>() {}.getType();
        return new Gson().fromJson(movieListJsonArray, movieListType);
    }


    //Static method that returns a JSON string from an asset folder file name passed in as a parameter
    public static String loadJsonFromAssetFolder(Context context, String jsonFileName) {

        String json = null;
        try {
            InputStream inputStream = context.getAssets().open(jsonFileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    //Converter Factory used by Retrofit to deserialize the data through GSON
    public static class MyCustomJsonConverterFactory extends Converter.Factory {

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return JsonConverter.INSTANCE;
        }

        final static class JsonConverter implements Converter<ResponseBody, List<Movie>> {
            static final JsonConverter INSTANCE = new JsonConverter();

            @Override
            public List<Movie> convert(@NonNull ResponseBody responseBody) throws IOException {
                return getMovieListFromJsonThroughTypeToken(responseBody.string(), MyApplication.getContext().getString(R.string.json_file_properties_results_array));
            }
        }
    }
}





