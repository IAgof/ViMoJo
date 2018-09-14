package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 14/09/18.
 */

import com.videonasocialmedia.vimojo.vimojoapiclient.model.TrackDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Class describing vimojo API media services.
 */
public interface TrackService {
  @PUT("track/{trackId}")
  @Headers("Content-Type: application/json")
  Call<TrackDto> update(@Path("trackId") String uuid, @Body TrackDto trackDto);

  @DELETE("track/{trackId}")
  @Headers("Content-Type: application/json")
  Call<TrackDto> remove(@Path("trackId") String id);
}
