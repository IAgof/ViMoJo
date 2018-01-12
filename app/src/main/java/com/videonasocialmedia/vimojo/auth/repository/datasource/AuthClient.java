package com.videonasocialmedia.vimojo.auth.repository.datasource;

import com.videonasocialmedia.vimojo.auth.repository.model.AuthTokenRequest;
import com.videonasocialmedia.vimojo.auth.repository.model.RegisterRequest;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by jliarte on 11/01/18.
 */

public interface AuthClient {
  @POST("user/")
  @Headers("Content-Type: application/json")
  Call<Map<String, String>> register(@Body RegisterRequest requestBody);

  @POST("login")
  @Headers("Content-Type: application/json")
  Call<AuthToken> getAuthToken(@Body AuthTokenRequest requestBody);
}
