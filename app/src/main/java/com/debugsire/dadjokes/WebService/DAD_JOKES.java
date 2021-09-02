package com.debugsire.dadjokes.WebService;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface DAD_JOKES {
    @Headers("Accept:application/json")
    @GET("https://icanhazdadjoke.com/")
    Call<JsonObject> getJoke();
}
