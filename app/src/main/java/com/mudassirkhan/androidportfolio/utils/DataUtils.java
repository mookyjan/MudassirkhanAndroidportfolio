package com.mudassirkhan.androidportfolio.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.application.MyApplication;
import com.mudassirkhan.androidportfolio.data.ContactContract;
import com.mudassirkhan.androidportfolio.models.Movie;
import com.mudassirkhan.androidportfolio.models.PortfolioItem;
import com.mudassirkhan.androidportfolio.models.Tab;
import com.mudassirkhan.androidportfolio.ui.activities.PortfolioItemsActivity;
import com.mudassirkhan.androidportfolio.ui.fragments.BaseMovieRecyclerViewFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.BroadcastReceiversFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.ContentProviderBisFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.ContentProviderFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.FileFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.GridCardViewsFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.GridRecyclerViewFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.GsonParsingFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.HorizontalCardViewsFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.JobSchedulingFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.NativeHttpFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.NativeJsonParsingFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.NativeSqliteFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.NotificationsFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.OkhttpFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.PicassoFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.RealmFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.RetrofitGsonFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.SharedPreferencesFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.VerticalRecyclerViewFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.VolleyFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.WidgetFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DataUtils {

    //Constant List that contains the list of Portfolio items
    public final static List<PortfolioItem> PORTFOLIO_ITEMS_LIST = getAllPortfolioItems(MyApplication.getContext());


    //Static method that returns the list of Portfolio items for the Main Activity's RecyclerView
    public static List<PortfolioItem> getAllPortfolioItems(final Context context) {
        return new ArrayList<PortfolioItem>() {{
            add(new PortfolioItem(R.mipmap.list_icon, context.getString(R.string.portfolio_item_recyclerviews_title), context.getString(R.string.portfolio_item_recyclerviews_description), R.color.recyclerviews_activity_accent_color));
            add(new PortfolioItem(R.mipmap.network_icon, context.getString(R.string.portfolio_item_networking_title), context.getString(R.string.portfolio_item_networking_description), R.color.network_activity_accent_color));
            add(new PortfolioItem(R.mipmap.database_icon, context.getString(R.string.portfolio_item_data_persistence_title), context.getString(R.string.portfolio_item_data_persistence_description), R.color.data_persistence_activity_accent_color));
            add(new PortfolioItem(R.mipmap.json_icon, context.getString(R.string.portfolio_item_json_parsing_title), context.getString(R.string.portfolio_item_json_parsing_description), R.color.json_parsing_activity_accent_color));
            add(new PortfolioItem(R.mipmap.gradle_icon, context.getString(R.string.portfolio_item_third_party_libraries_title), context.getString(R.string.portfolio_item_third_party_libraries_description), R.color.third_party_librairies_activity_accent_color));
            add(new PortfolioItem(R.mipmap.system_icon, context.getString(R.string.portfolio_item_system_integration_title), context.getString(R.string.portfolio_item_system_integration_description), R.color.system_integration_activity_accent_color));
            add(new PortfolioItem(R.mipmap.settings_icon, context.getString(R.string.portfolio_item_preferences_title), context.getString(R.string.portfolio_item_preferences_description), R.color.colorPrimarypreferences_activity_accent_color));
        }};
    }


    //Static method that returns the list of Tabs for the Details Activities
    public static List<Tab> getTabList(Context context, String activityName) {

        List<Tab> tabs = new ArrayList<>();

        if (activityName.equals(context.getString(R.string.portfolio_item_networking_title))) {
            tabs.add(new Tab(new NativeHttpFragment(), context.getString(R.string.fragment_tab_name_native_http)));
            tabs.add(new Tab(new VolleyFragment(), context.getString(R.string.fragment_tab_name_Volley)));
            tabs.add(new Tab(new OkhttpFragment(), context.getString(R.string.fragment_tab_name_okhttp)));
            tabs.add(new Tab(new RetrofitGsonFragment(), context.getString(R.string.fragment_tab_name_retrofit)));
        } else if (activityName.equals(context.getString(R.string.portfolio_item_recyclerviews_title))) {
            tabs.add(new Tab(new VerticalRecyclerViewFragment(), context.getString(R.string.fragment_tab_name_linear_vertical_recyclerview)));
            tabs.add(new Tab(new GridCardViewsFragment(), context.getString(R.string.fragment_tab_name_card_grid_recyclerview)));
            tabs.add(new Tab(new GridRecyclerViewFragment(), context.getString(R.string.fragment_tab_name_grid_recyclerview)));
            tabs.add(new Tab(new HorizontalCardViewsFragment(), context.getString(R.string.fragment_tab_name_linear_horizontal_recyclerview)));
        } else if (activityName.equals(context.getString(R.string.portfolio_item_json_parsing_title))) {
            tabs.add(new Tab(new NativeJsonParsingFragment(), context.getString(R.string.fragment_tab_name_native_json_parsing)));
            tabs.add(new Tab(new GsonParsingFragment(), context.getString(R.string.fragment_tab_name_gson_parsing)));
        } else if (activityName.equals(context.getString(R.string.portfolio_item_third_party_libraries_title))) {
            tabs.add(new Tab(new PicassoFragment(), context.getString(R.string.fragment_tab_name_picasso)));
            tabs.add(new Tab(new VolleyFragment(), context.getString(R.string.fragment_tab_name_Volley)));
            tabs.add(new Tab(new OkhttpFragment(), context.getString(R.string.fragment_tab_name_okhttp)));
            tabs.add(new Tab(new RetrofitGsonFragment(), context.getString(R.string.fragment_tab_name_retrofit)));
            tabs.add(new Tab(new GsonParsingFragment(), context.getString(R.string.fragment_tab_name_gson_parsing)));
            tabs.add(new Tab(new RealmFragment(), context.getString(R.string.fragment_tab_name_realm)));
        } else if (activityName.equals(context.getString(R.string.portfolio_item_data_persistence_title))) {
            tabs.add(new Tab(new NativeSqliteFragment(), context.getString(R.string.fragment_tab_name_sqlite)));
            tabs.add(new Tab(new ContentProviderFragment(), context.getString(R.string.fragment_tab_name_content_provider)));
            tabs.add(new Tab(new RealmFragment(), context.getString(R.string.fragment_tab_name_realm)));
            tabs.add(new Tab(new ContentProviderBisFragment(), context.getString(R.string.fragment_tab_name_content_provider_bis)));
            tabs.add(new Tab(new SharedPreferencesFragment(), context.getString(R.string.fragment_tab_name_shared_preferences)));
            tabs.add(new Tab(new FileFragment(), context.getString(R.string.fragment_tab_name_files)));
        } else if (activityName.equals(context.getString(R.string.portfolio_item_system_integration_title))) {
            tabs.add(new Tab(new NotificationsFragment(), context.getString(R.string.fragment_tab_name_notifications)));
            tabs.add(new Tab(new JobSchedulingFragment(), context.getString(R.string.fragment_tab_name_job_scheduling)));
            tabs.add(new Tab(new BroadcastReceiversFragment(), context.getString(R.string.fragment_tab_name_broadcast_receivers)));
            tabs.add(new Tab(new WidgetFragment(), context.getString(R.string.fragment_tab_name_widgets)));
        }
        return tabs;
    }


    //Static method that returns a Bundle containing the information corresponding to the fragment's name passed in as a parameter
    public static Bundle getFragmentInformation(Context context, String selectedFragmentName) {

        Bundle bundle = new Bundle();

        //Getting the fragment's information
        if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_linear_vertical_recyclerview))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_linear_vertical_recyclerview));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_offline_movie_recyclerviews_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_card_grid_recyclerview))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_card_grid_recyclerview));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_offline_movie_recyclerviews_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_grid_recyclerview))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_grid_recyclerview));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_offline_movie_recyclerviews_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_linear_horizontal_recyclerview))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_linear_horizontal_recyclerview));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_offline_horizontal_movie_recyclerviews_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_native_http))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_native_http));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_online_movie_recyclerviews_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_Volley))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_volley));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_online_movie_recyclerviews_possible_actions));
            bundle.putString((PortfolioItemsActivity.SELECTED_FRAGMENT_REPEAT), context.getString(R.string.fragment_info_repeating_in_networking_activity));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_okhttp))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_okhttp));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_online_movie_recyclerviews_possible_actions));
            bundle.putString((PortfolioItemsActivity.SELECTED_FRAGMENT_REPEAT), context.getString(R.string.fragment_info_repeating_in_networking_activity));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_retrofit))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_retrofit));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_online_movie_recyclerviews_possible_actions));
            bundle.putString((PortfolioItemsActivity.SELECTED_FRAGMENT_REPEAT), context.getString(R.string.fragment_info_repeating_in_networking_activity));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_sqlite))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_sqlite));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_databases_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_content_provider))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_content_provider));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_databases_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_realm))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_realm));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_databases_possible_actions));
            bundle.putString((PortfolioItemsActivity.SELECTED_FRAGMENT_REPEAT), context.getString(R.string.fragment_info_repeating_in_data_persistence_activity));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_content_provider_bis))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_content_provider_bis));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_content_provider_bis_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_shared_preferences))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_shared_preferences));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_shared_preferences_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_files))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_files));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_files_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_native_json_parsing))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_native_json_parsing));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_offline_movie_recyclerviews_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_gson_parsing))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_gson_parsing));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_offline_movie_recyclerviews_possible_actions));
            bundle.putString((PortfolioItemsActivity.SELECTED_FRAGMENT_REPEAT), context.getString(R.string.fragment_info_repeating_in_json_parsing_activity));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_picasso))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_picasso));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_picasso_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_notifications))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_notifications));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_notifications_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_job_scheduling))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_job_scheduling));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_job_scheduling_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_broadcast_receivers))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_broadcast_receivers));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_broadcast_receivers_possible_actions));
        } else if (selectedFragmentName.equals(context.getString(R.string.fragment_tab_name_widgets))) {
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_DESCRIPTION, context.getString(R.string.fragment_info_widgets));
            bundle.putString(PortfolioItemsActivity.SELECTED_FRAGMENT_POSSIBLE_ACTIONS, context.getString(R.string.fragment_info_widget_possible_actions));
        }
        return bundle;
    }


    //Static method that returns the theme that has to be set to the activity that calls it
    public static int getActivityStyle(Context context, String activityTitle) {

        int style = R.style.AppTheme;

        if (activityTitle.equals(context.getString(R.string.portfolio_item_recyclerviews_title))) {
            style = R.style.RecyclerviewsActivityTheme;
        } else if (activityTitle.equals(context.getString(R.string.portfolio_item_networking_title))) {
            style = R.style.NetworkingActivityTheme;
        } else if (activityTitle.equals(context.getString(R.string.portfolio_item_data_persistence_title))) {
            style = R.style.DataPersistenceActivityTheme;
        } else if (activityTitle.equals(context.getString(R.string.portfolio_item_json_parsing_title))) {
            style = R.style.JsonParsingActivityTheme;
        } else if (activityTitle.equals(context.getString(R.string.portfolio_item_third_party_libraries_title))) {
            style = R.style.ThirdPartyLibrairiesActivityTheme;
        } else if (activityTitle.equals(context.getString(R.string.portfolio_item_system_integration_title))) {
            style = R.style.SystemIntegrationActivityTheme;
        }
        return style;
    }


    //Static method that returns the initial dummy data for the our Contact SQLite database
    public static List<ContentValues> getSQLiteDatabaseInitialDummyData() {

        final ContentValues neymarContentValue = new ContentValues();
        neymarContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_NAME, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_neymar_name));
        neymarContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_neymar_phone));
        neymarContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL_ADDRESS, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_neymar_email));
        neymarContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_CITY, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_neymar_city));
        neymarContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_PICTURE_URI, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_neymar_picture_uri));

        final ContentValues willSmithContentValue = new ContentValues();
        willSmithContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_NAME, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_will_smith_name));
        willSmithContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_will_smith_phone));
        willSmithContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL_ADDRESS, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_will_smith_email));
        willSmithContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_CITY, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_will_smith_city));
        willSmithContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_PICTURE_URI, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_will_smith_picture_uri));

        final ContentValues zidaneContentValue = new ContentValues();
        zidaneContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_NAME, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_zidane_name));
        zidaneContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_zidane_phone));
        zidaneContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL_ADDRESS, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_zidane_email));
        zidaneContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_CITY, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_zidane_city));
        zidaneContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_PICTURE_URI, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_zidane_picture_uri));

        final ContentValues gaelContentValue = new ContentValues();
        gaelContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_NAME, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_my_name));
        gaelContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_my_phone));
        gaelContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL_ADDRESS, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_my_email));
        gaelContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_CITY, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_my_city));
        gaelContentValue.put(ContactContract.ContactEntry.COLUMN_NAME_PICTURE_URI, MyApplication.getContext().getString(R.string.sqlite_database_initial_dummy_data_my_picture_uri));

        return new ArrayList<ContentValues>() {{
            add(neymarContentValue);
            add(willSmithContentValue);
            add(zidaneContentValue);
            add(gaelContentValue);
        }};

    }


    //Static method that returns the list of movie type in String format, corresponding to the list of movies types passed as a parameter
    public static String getMovieTypeListString(Context context, List<Integer> jsonIdsList) {

        String movieTypeListString = "";

        if (jsonIdsList != null && jsonIdsList.size() != 0) {

            for (int i = 0; i < jsonIdsList.size(); i++) {

                String movieType = getMovieType(context, jsonIdsList.get(i));

                if (movieType != null) {

                    movieTypeListString = movieTypeListString + movieType;

                    if (i < jsonIdsList.size() - 1) {

                        movieTypeListString = movieTypeListString + ", ";

                    }

                }

            }
        }
        return movieTypeListString;

    }


    //Static method that returns the movie type in String format, corresponding to the movie type passed as an int parameter
    private static String getMovieType(Context context, int movieTypeId) {
        switch (movieTypeId) {
            case 28:
                return context.getString(R.string.movie_type_action);
            case 12:
                return context.getString(R.string.movie_type_adventure);
            case 16:
                return context.getString(R.string.movie_type_animation);
            case 35:
                return context.getString(R.string.movie_type_comedy);
            case 80:
                return context.getString(R.string.movie_type_crime);
            case 99:
                return context.getString(R.string.movie_type_documentary);
            case 18:
                return context.getString(R.string.movie_type_drama);
            case 10751:
                return context.getString(R.string.movie_type_family);
            case 14:
                return context.getString(R.string.movie_type_fantasy);
            case 36:
                return context.getString(R.string.movie_type_history);
            case 27:
                return context.getString(R.string.movie_type_horror);
            case 10402:
                return context.getString(R.string.movie_type_music);
            case 9648:
                return context.getString(R.string.movie_type_mystery);
            case 10749:
                return context.getString(R.string.movie_type_romance);
            case 878:
                return context.getString(R.string.movie_type_science_fiction);
            case 10770:
                return context.getString(R.string.movie_type_tv_movie);
            case 53:
                return context.getString(R.string.movie_type_thriller);
            case 10752:
                return context.getString(R.string.movie_type_war);
            case 37:
                return context.getString(R.string.movie_type_western);
            default:
                return null;

        }

    }


    //Static method used to format dates
    public static String changingDateFormat(String releaseDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }


    //Static method that returns the movie info AlertDialog, which is used in several fragments
    public static AlertDialog getSimpleClickAlertDialogBuilder(Context context, String movieTitle, String genreList, String movieOverview) {

        if (movieTitle == null || movieTitle.isEmpty()) {
            movieTitle = context.getString(R.string.base_movie_recyclerview_dialog_title_not_found);
        }

        if (genreList == null || genreList.isEmpty()) {
            genreList = context.getString(R.string.base_movie_recyclerview_dialog_genre_not_found);
        }

        if (movieOverview == null || movieOverview.isEmpty()) {
            movieOverview = context.getString(R.string.base_movie_recyclerview_dialog_overview_not_found);
        }

        String message = context.getString(R.string.base_movie_recyclerview_dialog_genre) + genreList + "\n\n" + movieOverview;

        return new AlertDialog.Builder(context)
                .setTitle(movieTitle)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();

    }


    //Static method that returns the movie delete AlertDialog, which is used in several fragments
    public static AlertDialog getLongClickAlertDialogBuilder(Context context, final Fragment fragment, Movie movie, int position) {

        final BaseMovieRecyclerViewFragment baseMovieRecyclerViewFragment = (BaseMovieRecyclerViewFragment) fragment;
        final int deletedPosition = position;

        return new AlertDialog.Builder(context)
                .setTitle(movie.getTitle())
                .setMessage(context.getString(R.string.various_fragments_delete_dialog_delete_confirmation))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        baseMovieRecyclerViewFragment.removeMoveFromList(deletedPosition);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create();
    }


    //Static method that returns a random Movie from the list passed in as a parameter
    public static Movie getRandomMovie(List<Movie> movieList) {
        Random random = new Random();
        int randomNum = random.nextInt(movieList.size());
        return movieList.get(randomNum);
    }


    //Static method used to check an email address validity
    public static boolean isEmailValid(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    //Static method that returns a Spanned object from the String html passed in as as parameter
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

}