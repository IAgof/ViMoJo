package com.videonasocialmedia.vimojo.upload.domain;

import android.provider.MediaStore;
import android.util.Log;

import com.videonasocialmedia.vimojo.upload.repository.apiclient.VimojoApi;
import com.videonasocialmedia.vimojo.upload.repository.rest.ServiceGenerator;

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

  public void uploadVideo(String apiBaseUrl, String mediaPath, final OnUploadVideoListener listener) {

    // create upload service client
    VimojoApi service = new ServiceGenerator().generateService(VimojoApi.class);

    File file = new File(mediaPath);

    RequestBody requestFile = RequestBody
        .create(okhttp3.MediaType.parse(MediaStore.Video.Media.CONTENT_TYPE), file);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body =
        MultipartBody.Part.createFormData("video", file.getName(), requestFile);

    // add another part within the multipart request
    String descriptionString = "hello, this is description speaking";
    RequestBody description =
        RequestBody.create(
            okhttp3.MultipartBody.FORM, descriptionString);

    // finally, execute the request
    Call<ResponseBody> call = service.uploadVideo(description, body);
    call.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call,
                             Response<ResponseBody> response) {
        Log.v("Upload", "success");
        if(response != null) {
          listener.onUploadVideoSuccess();
        } else {
          listener.onUploadVideoError(OnUploadVideoListener.Causes.UNKNOWN_ERROR);
        }
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.e("Upload error:", t.getMessage());
        listener.onUploadVideoError(OnUploadVideoListener.Causes.UNKNOWN_ERROR);
      }
    });
  }
}

