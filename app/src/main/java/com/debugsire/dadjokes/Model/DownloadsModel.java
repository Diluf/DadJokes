package com.debugsire.dadjokes.Model;

public class DownloadsModel {
    private String key, joke;

    public DownloadsModel(String key, String joke) {
        this.key = key;
        this.joke = joke;
    }

    public String getKey() {
        return key;
    }

    public String getJoke() {
        return joke;
    }

}
