package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.data.ContactContract;
import com.mudassirkhan.androidportfolio.models.Contact;

import java.util.List;

public class NativeSqliteFragment extends BaseDatabaseFragment {

    //Declaration, as a constant Integer, of the Loader's ID
    private static final int NATIVE_SQLITE_FRAGMENT_LOADER_ID = 893;


    /* Implementation of the methods declared in the parent fragment */


    //Method that returns the Loader's ID
    @Override
    protected int getLoaderId() {
        return NATIVE_SQLITE_FRAGMENT_LOADER_ID;
    }


    //Method that returns the list of Contacts gotten from the database
    @Override
    public List<Contact> executeQuery(SQLiteDatabase readableDbAccess, String tabName) {

        //We query the Contact database and get the corresponding cursor
        Cursor cursor = readableDbAccess.query(
                tabName,
                null,
                null,
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
        //We insert a contact into the database and return its ID
        return writableDbAccess.insert(tabName, null, values);
    }


    //Method that updates a contact in the database and returns the number of rows updated
    @Override
    protected int executeUpdate(SQLiteDatabase writableDbAccess, String tabName, ContentValues values, String selection, String[] selectionArgs, long id) {
        //We update the contact in the database and return the number of rows updated
        return writableDbAccess.update(ContactContract.ContactEntry.TABLE_NAME, values, selection, selectionArgs);
    }


    //Method that deletes a contact from the database and returns the number of rows deleted
    @Override
    protected int executeDelete(SQLiteDatabase writableDbAccess, String tabName, String selection, String[] selectionArgs, long id) {
        //We delete the contact from the database and return the number of rows deleted
        return writableDbAccess.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs);
    }

}
