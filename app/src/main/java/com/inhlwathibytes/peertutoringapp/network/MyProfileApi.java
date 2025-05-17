package com.inhlwathibytes.peertutoringapp.network;

import com.inhlwathibytes.peertutoringapp.models.MyProfileTutorResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface MyProfileApi {

    @GET("/api/tutorship/my-profile")  // Use the endpoint where tutor profile is returned
    Call<MyProfileTutorResponse> getTutorProfile(
            @Header("Authorization") String authToken  // Include token in Authorization header
    );
}

