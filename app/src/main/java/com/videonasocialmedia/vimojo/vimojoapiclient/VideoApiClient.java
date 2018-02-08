package com.videonasocialmedia.vimojo.vimojoapiclient;

/**
 * Created by jliarte on 8/02/18.
 */

import com.videonasocialmedia.vimojo.vimojoapiclient.model.Video;

import java.io.File;
import java.io.IOException;

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
  /**
   * Make a upload video call to send video to platform
   *
   * @param authToken   valid token
   * @param mediaPath   Absolute path, video to upload
   * @param description Description of video
   * @return the video upload response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public Video uploadVideo(String authToken, String mediaPath, String description)
          throws VimojoApiException {
    // create upload service client
    VideoService videoService = getService(VideoService.class, authToken);

    File file = new File(mediaPath);
    RequestBody requestFile = RequestBody
            .create(okhttp3.MediaType.parse(MIME_TYPE_VIDEO), file);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body =
            MultipartBody.Part.createFormData(MULTIPART_NAME_DATA, file.getName(), requestFile);
    // add another part within the multipart request
    RequestBody requestBody = RequestBody.create(MultipartBody.FORM, description);

    try {
      Response<Video> response = videoService.uploadVideo(requestBody, body).execute();
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

}
