package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 8/02/18.
 */

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.videonasocialmedia.vimojo.sync.model.VideoUpload;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.Video;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MIME_TYPE_VIDEO;
import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MULTIPART_NAME_DATA;

/**
 * Api client for video service.
 * <p>
 * <p>Handles video upload calls.</p>
 */
public class VideoApiClient extends VimojoApiClient {

  public static final String VIDEO_API_KEY_TITLE = "title";
  public static final String VIDEO_API_KEY_DESCRIPTION = "description";
  public static final String VIDEO_API_KEY_PRODUCT_TYPE = "productType";

  /**
   * Make a upload video call to send video to platform
   *
   * @param videoUpload Model for enqueue video uploads to vimojo platform.
   * @return the video upload response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public Video uploadVideo(VideoUpload videoUpload)
          throws VimojoApiException {
    // getAuthToken

    // create upload service client
    VideoService videoService = getService(VideoService.class, videoUpload.getAuthToken());

    File file = new File(videoUpload.getMediaPath());
    RequestBody requestFile = RequestBody
            .create(okhttp3.MediaType.parse(MIME_TYPE_VIDEO), file);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body =
            MultipartBody.Part.createFormData(MULTIPART_NAME_DATA, file.getName(), requestFile);
    // add another part within the multipart request
    RequestBody requestBodyTitle = createPartFromString(videoUpload.getTitle());
    RequestBody requestBodyDescription = createPartFromString(videoUpload.getDescription());
    String productTypeList = TextUtils.join(", ", videoUpload.getProductTypeList());
    RequestBody requestBodyProductTypes = createPartFromString(productTypeList);

    HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>();
    requestBodyHashMap.put(VIDEO_API_KEY_TITLE, requestBodyTitle);
    requestBodyHashMap.put(VIDEO_API_KEY_DESCRIPTION, requestBodyDescription);
    requestBodyHashMap.put(VIDEO_API_KEY_PRODUCT_TYPE, requestBodyProductTypes);

    try {
      Response<Video> response = videoService.uploadVideo(requestBodyHashMap, body).execute();
      if (response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException e) {
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    return null;
  }

  @NonNull
  private RequestBody createPartFromString(String descriptionString) {
    if (descriptionString == null)
      return RequestBody.create(MultipartBody.FORM, "");
    return RequestBody.create(
        MultipartBody.FORM, descriptionString);
  }
}
