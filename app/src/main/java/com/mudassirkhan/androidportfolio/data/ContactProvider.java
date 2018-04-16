package com.mudassirkhan.androidportfolio.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.application.MyApplication;

public class ContactProvider extends ContentProvider {

    //Declaration of constants used by the UriMatcher to determine the type of data we are going to access within the database
    public static final int CODE_CONTACTS = 100;
    public static final int CODE_SINGLE_CONTACT = 101;

    //Declaration of contactProvider's member variables
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ContactDbHelper mDbHelper;

    //Static method that allows us to instantiate the UriMatcher
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ContactContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, ContactContract.ContactEntry.PATH_CONTACTS, CODE_CONTACTS);
        matcher.addURI(authority, ContactContract.ContactEntry.PATH_CONTACTS + "/#", CODE_SINGLE_CONTACT);
        return matcher;
    }


    //When the content provider is instantiated, we initialize the ContactDbHelper
    @Override
    public boolean onCreate() {
        mDbHelper = new ContactDbHelper(getContext());
        return true;
    }


    //Method used to query the Contact database through the content provider
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            //Since we want to get the whole database
            case CODE_CONTACTS:
                cursor = mDbHelper.getReadableDatabase().query(
                        ContactContract.ContactEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException(MyApplication.getContext().getString(R.string.content_provider_unknown_uri_exception) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        //We return the cursor that we just got
        return cursor;
    }


    //Method used to insert data into the Contact database through the content provider
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long insertedRowId = -1;

        switch (sUriMatcher.match(uri)) {

            //Since we want to insert the new contact in the whole database
            case CODE_CONTACTS:
                insertedRowId = mDbHelper.getWritableDatabase().insert(ContactContract.ContactEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new UnsupportedOperationException(MyApplication.getContext().getString(R.string.content_provider_unknown_uri_exception) + uri);
        }

        if (insertedRowId != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //In case it has worked, we return the Uri of the inserted row.
        return ContactContract.ContactEntry.buildContactUriWithId(insertedRowId);
    }


    //Method used to delete data from the Contact database through the content provider
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsDeleted = 0;

        switch (sUriMatcher.match(uri)) {

            //Since we delete only a single contact
            case CODE_SINGLE_CONTACT:
                numRowsDeleted = mDbHelper.getWritableDatabase().delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(MyApplication.getContext().getString(R.string.content_provider_unknown_uri_exception) + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //If it has worked we return the number of rows deleted (which will actually always be 1). If not, we return 0
        return numRowsDeleted;
    }


    //Method used to update data from the Contact database through the content provider
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsUpdated = 0;

        switch (sUriMatcher.match(uri)) {

            //Since we update a single contact
            case CODE_SINGLE_CONTACT:
                numRowsUpdated = mDbHelper.getWritableDatabase().update(ContactContract.ContactEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(MyApplication.getContext().getString(R.string.content_provider_unknown_uri_exception) + uri);
        }

        if (numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //In case it has worked we return the number of rows updated (which will actually always be 1). If not, we return 0
        return numRowsUpdated;
    }


    //Used to get the MIME type of the data that the content provider returns. Not useful in our case
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}
