package com.debugsire.dadjokes;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());


        RealmConfiguration config =
                new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .allowWritesOnUiThread(true)
                        .allowQueriesOnUiThread(true)
                        .build();

        Realm.setDefaultConfiguration(config);
    }
}
