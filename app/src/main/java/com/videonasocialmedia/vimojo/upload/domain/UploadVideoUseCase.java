package com.videonasocialmedia.vimojo.upload.domain;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.vimojoapiclient.VideoService;
import com.videonasocialmedia.vimojo.upload.repository.localsource.CachedToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.rest.ServiceGenerator;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alvaro on 28/11/17.
 */

public class UploadVideoUseCase {

  public static final String MIME_TYPE_VIDEO = "video/mp4";
  public static final String MULTIPART_NAME_DATA = "file";
  private static final String LOG_TAG = UploadVideoUseCase.class.getCanonicalName();

  public void uploadVideo(String apiBaseUrl, String mediaPath, final OnUploadVideoListener listener) {

    // create upload service client
    VideoService service = new ServiceGenerator(apiBaseUrl).generateService(VideoService.class,
        CachedToken.getToken());

    File file = new File(mediaPath);

    RequestBody requestFile = RequestBody
        .create(okhttp3.MediaType.parse(MIME_TYPE_VIDEO), file);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body =
        MultipartBody.Part.createFormData(MULTIPART_NAME_DATA, file.getName(), requestFile);

    // add another part within the multipart request
    String descriptionString = BuildConfig.FLAVOR;
    RequestBody description =
        RequestBody.create(
            okhttp3.MultipartBody.FORM, descriptionString);

    // finally, execute the request
    Call<ResponseBody> call = service.uploadVideo(description, body);
    call.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call,
                             Response<ResponseBody> response) {
        if(response != null && response.code() == 201) {
          listener.onUploadVideoSuccess();
        } else {
          // TODO:(alvaro.martinez) 28/11/17 Manage API errors code ...
          listener.onUploadVideoError(OnUploadVideoListener.Causes.UNKNOWN_ERROR);
        }
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        listener.onUploadVideoError(OnUploadVideoListener.Causes.UNKNOWN_ERROR);
        Log.e(LOG_TAG, "Error while uploading videos " + t.getMessage());
        Crashlytics.log("Error while uploading videos." +
            " Cause " + t.getCause() + " Message " + t.getMessage());
        Crashlytics.logException(t);
      }
    });
  }
}

