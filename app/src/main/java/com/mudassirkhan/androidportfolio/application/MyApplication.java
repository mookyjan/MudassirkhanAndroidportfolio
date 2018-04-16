package com.mudassirkhan.androidportfolio.application;

import android.app.Application;
import android.content.Context;

import com.mudassirkhan.androidportfolio.models.Contact;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyApplication extends Application {

    //Declaration of a Context that we will be able to use throughout the application, in some places where we do not have one.
    private static Context mContext;

    //Declaration of an AtomicInteger, that we will use to manage our Realm's Contact objects autoincrement Ids
    public static AtomicInteger contactId = new AtomicInteger();

    //Setting up things
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        contactId = getIdByTable(realm, Contact.class);
        realm.close();

    }

    //Method that allows us to get the Context instance created above
    public static Context getContext() {
        return mContext;
    }

    //Method that allows us to initialize the AtomicInteger(s), whatever the class is.
    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass) {
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("mId").intValue()) : new AtomicInteger();
    }

    //Method that allows us to set up Realm
    private void setUpRealmConfig() {
        Realm.init(getContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}




