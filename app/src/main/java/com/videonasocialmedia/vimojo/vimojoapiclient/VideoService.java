package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by alvaro on 28/11/17.
 */

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Class describing video services.
 */
public interface VideoService {
  @Multipart
  @POST("video")
  Call<ResponseBody> uploadVideo(
          @Part("description") RequestBody description,
          @Part MultipartBody.Part file
  );
}
