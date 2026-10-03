package com.example.kisanbandhu.utils;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("disease-detection-file")
    Call<JsonObject> detectDisease(
            @Part MultipartBody.Part file
    );
}