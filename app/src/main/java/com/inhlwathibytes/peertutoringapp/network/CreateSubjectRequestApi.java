package com.inhlwathibytes.peertutoringapp.network;

import com.inhlwathibytes.peertutoringapp.models.CreateSubjectRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Header;

public interface CreateSubjectRequestApi {

    @POST("api/tutorship/subject")
    Call<Void> createSubject(
            @Header("Authorization") String authToken,
            @Body CreateSubjectRequest request
    );
}
