package com.mudassirkhan.androidportfolio.models;

import com.mudassirkhan.androidportfolio.application.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Contact extends RealmObject {

    //Declaration of Contact object's member variables
    @PrimaryKey
    private long mId;
    @Required
    private String mName;
    @Required
    private String mPhoneNumber;
    @Required
    private String mEmailAddress;
    @Required
    private String mCity;
    private String mPictureUri;


    //Empty constructor, required by Realm
    public Contact() {
    }

    //Other constructor required by Realm
    public Contact(String name, String phoneNumber, String emailAddress, String city, String pictureUri) {
        mId = MyApplication.contactId.incrementAndGet();
        mName = name;
        mPhoneNumber = phoneNumber;
        mEmailAddress = emailAddress;
        mCity = city;
        mPictureUri = pictureUri;
    }

    //Constructor used in other cases
    public Contact(long id, String name, String phoneNumber, String emailAddress, String city, String pictureUri) {
        mId = id;
        mName = name;
        mPhoneNumber = phoneNumber;
        mEmailAddress = emailAddress;
        mCity = city;
        mPictureUri = pictureUri;
    }

    //Contact's getters
    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public String getCity() {
        return mCity;
    }

    public String getPictureUri() {
        return mPictureUri;
    }

    public long getId() {
        return mId;
    }


    //Contact's setters
    public void setName(String name) {
        mName = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void setPictureUri(String pictureUri) {
        mPictureUri = pictureUri;
    }
}
