package com.mudassirkhan.androidportfolio.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ContactContract {

    static final String CONTENT_AUTHORITY = "com.gaelmarhic.gaelmarhicsandroidportfolio";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Inner class corresponding to our Contact's table
    public static class ContactEntry implements BaseColumns {

        //Contact's table path and Uri to access it
        static final String PATH_CONTACTS = "contacts";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CONTACTS)
                .build();

        //Table's name
        public static final String TABLE_NAME = "contacts";

        //Fields' name
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_EMAIL_ADDRESS = "email_address";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_PICTURE_URI = "picture_uri";

        //Method that allows us to build the Uri to get a contact in the database, passing its id
        static Uri buildContactUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }
}
