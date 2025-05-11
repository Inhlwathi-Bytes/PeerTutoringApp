package com.inhlwathibytes.peertutoringapp.network;

import com.inhlwathibytes.peertutoringapp.models.UserResponseMode;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserModeApi {
    @GET("api/tutorship/has-tutorship") // adjust if your controller route is different
    Call<UserResponseMode> getUserMode(@Header("Authorization") String token);
}

