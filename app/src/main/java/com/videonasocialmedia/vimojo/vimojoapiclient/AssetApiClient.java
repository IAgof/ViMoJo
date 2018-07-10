/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */


/**
 * Created by alvaro on 21/6/18.
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MIME_TYPE_VIDEO;
import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MULTIPART_NAME_DATA;

/**
 * Api client for asset service.
 * <p>
 * <p>Handles asset service calls. Asset are used to store files in a project
 * (video, music, sound, etc.). </p>
 */
public class AssetApiClient extends VimojoApiClient {
  private static final String ASSET_API_KEY_NAME = "name";
  private static final String ASSET_API_KEY_TYPE = "type";
  private static final String ASSET_API_KEY_HASH = "hash";
  private static final String ASSET_API_KEY_DATE = "date";

  /**
   * Make a upload video call to send video to platform
   *
   * @param authToken   valid token to validate call to service
   * @param assetUpload Model for enqueue video uploads to vimojo platform.
   * @return the video upload response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public AssetDto addAsset(String authToken, AssetUpload assetUpload)
      throws VimojoApiException, FileNotFoundException {

    // create upload service client
    AssetService assetService = getService(AssetService.class, authToken);

    File file = new File(assetUpload.getMediaPath());
    RequestBody requestFile = RequestBody
        .create(okhttp3.MediaType.parse(MIME_TYPE_VIDEO), file);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body =
        MultipartBody.Part.createFormData(MULTIPART_NAME_DATA, file.getName(), requestFile);
    // add another part within the multipart request
    RequestBody requestBodyName = createPartFromString(assetUpload.getName());
    RequestBody requestBodyType = createPartFromString(assetUpload.getType());
    RequestBody requestBodyHash = createPartFromString(assetUpload.getHash());
    RequestBody requestBodyDate = createPartFromString(assetUpload.getDate());

    HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>();
    requestBodyHashMap.put(ASSET_API_KEY_NAME, requestBodyName);
    requestBodyHashMap.put(ASSET_API_KEY_TYPE, requestBodyType);
    requestBodyHashMap.put(ASSET_API_KEY_HASH, requestBodyHash);
    requestBodyHashMap.put(ASSET_API_KEY_DATE, requestBodyDate);
    Call<AssetDto> assetUploadTask = assetService.addAsset(assetUpload.getId(),
        requestBodyHashMap, body);
    try {
      Response<AssetDto> response = assetUploadTask.execute();
      if (response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (FileNotFoundException fileError) {
      throw fileError;
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