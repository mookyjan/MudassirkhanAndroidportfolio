package com.mudassirkhan.androidportfolio.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mudassirkhan.androidportfolio.utils.DataUtils;

import java.util.List;

public class ContactDbHelper extends SQLiteOpenHelper {

    //Database's name and current version
    private static final String DATABASE_NAME = "Contact.db";
    private static final int DATABASE_VERSION = 1;

    //Declaration of constant strings used to operate on the database
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_CONTACT_TABLE =
            "CREATE TABLE " + ContactContract.ContactEntry.TABLE_NAME + " (" +
                    ContactContract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ContactContract.ContactEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP +
                    ContactContract.ContactEntry.COLUMN_NAME_EMAIL_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    ContactContract.ContactEntry.COLUMN_NAME_CITY + TEXT_TYPE + COMMA_SEP +
                    ContactContract.ContactEntry.COLUMN_NAME_PICTURE_URI + TEXT_TYPE + " )";
    private static final String SQL_DELETE_CONTACT_TABLE =
            "DROP TABLE IF EXISTS " + ContactContract.ContactEntry.TABLE_NAME;

    //ContactDbHelper's Constructor
    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Triggered when the App is first launched
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACT_TABLE);
        insertInitialDummyData(db);
    }

    //Triggered when we update the database's structure
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CONTACT_TABLE);
        onCreate(db);
    }

    //Helper method that allows us to insert initial dummy data when the app is first started
    private void insertInitialDummyData(SQLiteDatabase db) {
        List<ContentValues> contentValuesList = DataUtils.getSQLiteDatabaseInitialDummyData();
        for (int i = 0; i < contentValuesList.size(); i++) {
            db.insert(ContactContract.ContactEntry.TABLE_NAME, null, contentValuesList.get(i));
        }
    }

}