package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.mudassirkhan.androidportfolio.data.ContactContract;
import com.mudassirkhan.androidportfolio.models.Contact;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmFragment extends BaseDatabaseFragment {

    //Declaration of the Realm database's member variable
    private Realm mRealm = Realm.getDefaultInstance();


    /* Implementation of the methods declared in the parent fragment */


    //Method that returns the Loader's ID
    @Override
    protected int getLoaderId() {
        //Since we are not using a Loader here, we return 0
        return 0;
    }


    //Method that returns the list of Contacts gotten from the database
    @Override
    public List<Contact> executeQuery(SQLiteDatabase readableDbAccess, String tabName) {

        //We query the Realm database and get a RealmResults object, which is actually a list of Contact objects
        RealmResults<Contact> contactRealmResults = mRealm.where(Contact.class).findAllSorted("mName");

        //Then we convert it to a normal list of Contacts and we return it
        return mRealm.copyFromRealm(contactRealmResults);
    }


    //Method that inserts a contact into the database and returns its ID
    @Override
    protected long executeInsert(SQLiteDatabase writableDbAccess, String tabName, ContentValues values) {

        //We will not pass the ContentValues object to the Realm database, but we will use it to get the contact's data
        String name = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_NAME);
        String phoneNumber = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER);
        String emailAddress = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_EMAIL_ADDRESS);
        String city = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_CITY);
        String pictureUri = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_PICTURE_URI);

        //Then, we create a Contact object with the data we just got
        Contact contact = new Contact(name, phoneNumber, emailAddress, city, pictureUri);

        //We insert the Contact object into the contact database
        mRealm.beginTransaction();
        Contact returnedContact = mRealm.copyToRealm(contact);
        mRealm.commitTransaction();

        //We get the new Contact's ID and return it
        return returnedContact.getId();
    }


    //Method that updates a contact in the database and returns the number of rows updated
    @Override
    protected int executeUpdate(SQLiteDatabase writableDbAccess, String tabName, ContentValues values, String selection, String[] selectionArgs, long id) {

        //We will not pass the ContentValues object to the Realm database, but we will use it to get the contact's data
        String name = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_NAME);
        String phoneNumber = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER);
        String emailAddress = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_EMAIL_ADDRESS);
        String city = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_CITY);
        String pictureUri = values.getAsString(ContactContract.ContactEntry.COLUMN_NAME_PICTURE_URI);

        //We get the Contact object, from the Realm database, that has the Id corresponding to the one we want to update
        Contact contactFromRealm = mRealm.where(Contact.class).equalTo("mId", id).findFirst();

        //We update the Contact
        mRealm.beginTransaction();
        contactFromRealm.setName(name);
        contactFromRealm.setPhoneNumber(phoneNumber);
        contactFromRealm.setEmailAddress(emailAddress);
        contactFromRealm.setCity(city);
        contactFromRealm.setPictureUri(pictureUri);
        Contact returnedContact = mRealm.copyToRealmOrUpdate(contactFromRealm);
        mRealm.commitTransaction();

        //We make sure that the operation was successful
        int nbRowsUpdated;
        if (returnedContact == null) {
            nbRowsUpdated = 0;
        } else {
            nbRowsUpdated = 1;
        }

        //We return the number of rows updated
        return nbRowsUpdated;
    }


    //Method that deletes a contact from the database and returns the number of rows deleted
    @Override
    protected int executeDelete(SQLiteDatabase writableDbAccess, String tabName, String selection, String[] selectionArgs, long id) {

        //We get the Contact object, from the Realm database, that has the Id corresponding to the one we want to delete
        Contact contactFromRealm = mRealm.where(Contact.class).equalTo("mId", id).findFirst();

        //We delete the Contact
        mRealm.beginTransaction();
        contactFromRealm.deleteFromRealm();
        mRealm.commitTransaction();

        //We make sure that the operation was successful
        int nbRowsDeleted;
        if (!contactFromRealm.isValid()) {
            nbRowsDeleted = 1;
        } else {
            nbRowsDeleted = 0;
        }

        //We return the number of rows deleted
        return nbRowsDeleted;
    }

}
