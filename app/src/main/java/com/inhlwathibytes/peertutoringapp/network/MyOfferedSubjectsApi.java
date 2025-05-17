package com.inhlwathibytes.peertutoringapp.network;

import com.inhlwathibytes.peertutoringapp.models.MyOfferedSubjectsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface MyOfferedSubjectsApi {
    @GET("api/tutorship/subjects")
    Call<MyOfferedSubjectsResponse> getMySubjects(@Header("Authorization") String token);
}

