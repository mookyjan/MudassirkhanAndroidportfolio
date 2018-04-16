package com.mudassirkhan.androidportfolio.background;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;

import com.mudassirkhan.androidportfolio.data.ContactContract;
import com.mudassirkhan.androidportfolio.models.Contact;
import com.mudassirkhan.androidportfolio.ui.fragments.BaseDatabaseFragment;

import java.util.List;

public class ContactLoader extends AsyncTaskLoader<List<Contact>> {

    //Declaration of the ContactLoader's member variables
    private SQLiteDatabase mReadableDbAccess;
    private Fragment mCurrentQueryingFragment;
    private List<Contact> mData;

    //ContactLoader's Constructor
    public ContactLoader(Context context, SQLiteDatabase readableDbAccess, Fragment currentQueryingFragment) {
        super(context);
        mReadableDbAccess = readableDbAccess;
        mCurrentQueryingFragment = currentQueryingFragment;
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

    //Getting the database's contacts on a background thread
    @Override
    public List<Contact> loadInBackground() {

        //Getting an instance of the currently querying fragment, to execute its querying method on a background thread
        BaseDatabaseFragment currentFragment = (BaseDatabaseFragment) mCurrentQueryingFragment;
        return currentFragment.executeQuery(mReadableDbAccess, ContactContract.ContactEntry.TABLE_NAME);

    }

    //Method that saves the downloaded data for later retrieval
    @Override
    public void deliverResult(List<Contact> data) {
        mData = data;
        super.deliverResult(data);
    }
}

