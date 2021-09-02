package com.debugsire.dadjokes.Model;

public class FavoritesModel {
    private String key, joke;

    public FavoritesModel(String key, String joke) {
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
