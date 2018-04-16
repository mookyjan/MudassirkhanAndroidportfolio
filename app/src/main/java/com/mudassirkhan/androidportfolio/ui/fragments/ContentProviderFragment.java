package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.data.ContactContract;
import com.mudassirkhan.androidportfolio.models.Contact;

import java.util.List;

public class ContentProviderFragment extends BaseDatabaseFragment {

    //Declaration, as a constant Integer, of the Loader's ID
    private static final int CONTENT_PROVIDER_FRAGMENT_LOADER_ID = 12345;


    /* Implementation of the methods declared in the parent fragment */


    //Method that returns the Loader's ID
    @Override
    protected int getLoaderId() {
        return CONTENT_PROVIDER_FRAGMENT_LOADER_ID;
    }


    //Method that returns the list of Contacts gotten from the database
    @Override
    public List<Contact> executeQuery(SQLiteDatabase readableDbAccess, String tabName) {

        //We query the Contact database through our Content Provider, and get the corresponding cursor
        Cursor cursor = getContext().getContentResolver().query(
                ContactContract.ContactEntry.CONTENT_URI,
                null,
                null,
                null,
                ContactContract.ContactEntry.COLUMN_NAME_NAME + BaseDatabaseFragment.COLLATE_NO_CASE
        );

        //We check that the cursor is not null
        if (cursor == null) {
            isNotShowingData(getString(R.string.base_database_fragment_no_data_in_database), R.mipmap.empty_folder_icon);
            return null;
        }

        //If the cursor is not null, then we get the list of Contact objects from it
        List<Contact> contactList = getContactListFromCursor(cursor);
        cursor.close();

        //We return the list of Contacts
        return contactList;
    }


    //Method that inserts a contact into the database and returns its ID
    @Override
    protected long executeInsert(SQLiteDatabase writableDbAccess, String tabName, ContentValues values) {

        //We insert a contact into the database through our Content Provider, and get the new Contact's Uri
        Uri uri = getContext().getContentResolver().insert(ContactContract.ContactEntry.CONTENT_URI, values);

        //We get the ID out of the Uri and return it
        return Long.parseLong(uri.getLastPathSegment());
    }


    //Method that updates a contact in the database and returns the number of rows updated
    @Override
    protected int executeUpdate(SQLiteDatabase writableDbAccess, String tabName, ContentValues values, String selection, String[] selectionArgs, long id) {

        //We build the required Uri to update the Contact
        Uri uri = ContactContract.ContactEntry.CONTENT_URI.buildUpon()
                .appendPath(Long.toString(id))
                .build();

        //We update the contact in the database through our Content Provider, and return the number of rows updated
        return getContext().getContentResolver().update(uri, values, selection, selectionArgs);
    }


    //Method that deletes a contact from the database and returns the number of rows deleted
    @Override
    protected int executeDelete(SQLiteDatabase writableDbAccess, String tabName, String selection, String[] selectionArgs, long id) {

        //We build the required Uri to delete the Contact
        Uri uri = ContactContract.ContactEntry.CONTENT_URI.buildUpon()
                .appendPath(Long.toString(id))
                .build();

        //We delete the contact from the database through our Content Provider, and return the number of rows deleted
        return getContext().getContentResolver().delete(uri, selection, selectionArgs);
    }

}



