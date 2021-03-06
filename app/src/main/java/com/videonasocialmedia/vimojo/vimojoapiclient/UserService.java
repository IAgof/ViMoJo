package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by alvaro on 25/01/18.
 */

import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.UserId;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Class describing user services.
 */
public interface UserService {
  @GET("user/{id}")
  Call<UserDto> getUser(@Path("id") String id);

  @GET("user/getId")
  Call<UserId> getUserId(@Query("prehisteric") String prehisteric);
}
