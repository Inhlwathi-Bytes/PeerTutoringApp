package com.inhlwathibytes.peertutoringapp.network;

import com.inhlwathibytes.peertutoringapp.models.RegisterTutorRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TutorshipApi {
    @Headers("Content-Type: application/json")
    @POST("api/tutorship/create")
    Call<Void> registerTutorship(@Header("Authorization") String token, @Body RegisterTutorRequest request);
}
