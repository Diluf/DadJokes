package com.debugsire.dadjokes.Realms;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MyDownloads extends RealmObject {
    @PrimaryKey
    private String key;
    private String joke;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }
}
