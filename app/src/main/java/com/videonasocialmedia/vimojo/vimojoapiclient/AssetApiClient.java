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
import android.util.Log;

import com.videonasocialmedia.vimojo.asset.domain.model.Asset;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetDto;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetQuery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
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
  private String LOG_TAG = AssetApiClient.class.getCanonicalName();
  private static final String ASSET_API_KEY_NAME = "name";
  private static final String ASSET_API_KEY_TYPE = "type";
  private static final String ASSET_API_KEY_HASH = "hash";
  private static final String ASSET_API_KEY_PATH = "path";
  private static final String ASSET_API_KEY_DATE = "date";
  private static final String ASSET_API_MEDIA_ID = "mediaId";
  private static final String ASSET_API_KEY_ID = "id";

  @Inject
  public AssetApiClient() {
  }

  /**
   * Make a upload video call to send video to platform
   *
   * @param accessToken valid token to validate call to service
   * @param asset       Model for enqueue video uploads to vimojo platform.
   * @return the video upload response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public AssetDto addAsset(String accessToken, Asset asset)
          throws VimojoApiException, FileNotFoundException {
    // create upload service client
    AssetService assetService = getAssetService(accessToken);
    MultipartBody.Part body = null;

    // add another part within the multipart request
    RequestBody requestBodyName = createPartFromString(asset.getName());
    RequestBody requestBodyType = createPartFromString(asset.getType());
    RequestBody requestBodyHash = createPartFromString(asset.getHash());
    RequestBody requestBodyPath = createPartFromString(asset.getPath());
    RequestBody requestBodyDate = createPartFromString(asset.getDate());
    RequestBody requestBodyMediaId = createPartFromString(asset.getMediaId());

    HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>();
    requestBodyHashMap.put(ASSET_API_KEY_NAME, requestBodyName);
    requestBodyHashMap.put(ASSET_API_KEY_TYPE, requestBodyType);
    requestBodyHashMap.put(ASSET_API_KEY_HASH, requestBodyHash);
    requestBodyHashMap.put(ASSET_API_KEY_PATH, requestBodyPath);
    requestBodyHashMap.put(ASSET_API_KEY_DATE, requestBodyDate);
    requestBodyHashMap.put(ASSET_API_MEDIA_ID, requestBodyMediaId);

    // (jliarte): 23/07/18 handle asset with hash already exists
    // TODO(jliarte): 14/08/18 add createdBy
    AssetQuery query = AssetQuery.Builder.create().withHash(asset.getHash())
            .withCreatedBy(asset.getCreatedBy()).build();
    List<AssetDto> existingAssets = this.query(accessToken, query);
    if (existingAssets == null ) {
      throw new VimojoApiException(-1, VimojoApiException.QUERY_ERROR);
    }
    if (existingAssets.size() == 0) {
      File file = new File(asset.getPath());
      RequestBody requestFile = RequestBody.create(okhttp3.MediaType.parse(MIME_TYPE_VIDEO), file);
      // MultipartBody.Part is used to send also the actual file name
      body = MultipartBody.Part.createFormData(MULTIPART_NAME_DATA, file.getName(), requestFile);
    } else {
      requestBodyHashMap.put(ASSET_API_KEY_ID, createPartFromString(existingAssets.get(0).getId()));
    }

    // TODO(jliarte): 13/08/18 set project id
    Call<AssetDto> assetUploadTask = assetService.addAsset("confihack", requestBodyHashMap,
            body);
    try {
      // TODO(jliarte): 18/07/18 check if asset with hash **** is already present in backend
      // else
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

  /**
   * Query asset API endpoint
   *
   * @param accessToken valid token to validate call to service
   * @param query       Query
   * @return the video upload response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public List<AssetDto> query(String accessToken, AssetQuery query)
          throws VimojoApiException {
    AssetService assetService = getAssetService(accessToken);

    // TODO(jliarte): 13/08/18 set project id
    Call<List<AssetDto>> assetUploadTask = assetService.query("confihack", query.toMap());
    try {
      // TODO(jliarte): 18/07/18 check if asset with hash **** is already present in backend
      // else
      Response<List<AssetDto>> response = assetUploadTask.execute();
      if (response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException e) {
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    return Collections.emptyList();
  }

  @NonNull
  private RequestBody createPartFromString(String descriptionString) {
    if (descriptionString == null)
      return RequestBody.create(MultipartBody.FORM, "");
    return RequestBody.create(
            MultipartBody.FORM, descriptionString);
  }

  // TODO(jliarte): 10/07/18 return AssetDto
  public void getProjectAssets(String accessToken, String folderDownloadPath)
          throws VimojoApiException {
    // create upload service client
    AssetService assetService = getAssetService(accessToken);

    Call<List<AssetDto>> response = assetService.getProjectAssets();
    response.enqueue(new Callback<List<AssetDto>>() {
      @Override
      public void onFailure(Call<List<AssetDto>> call, Throwable t) {
        // TODO: 9/7/18 manage retrofit failures 
        Log.d(LOG_TAG, "onFailure asset service asset list to download");
      }

      @Override
      public void onResponse(Call<List<AssetDto>> call, Response<List<AssetDto>> response) {
        List<AssetDto> assetList = response.body();
        for (AssetDto assetDto : assetList) {
          donwloadAssetFile(accessToken, folderDownloadPath, assetDto);
        }
        // TODO: 9/7/18 Notify download completed
      }
    });
  }

  private void donwloadAssetFile(String accessToken, String folderDownloadPath, AssetDto assetDto) {
    // create upload service client
    AssetService assetService = getAssetService(accessToken);

    Call<ResponseBody> response = assetService.downloadAssetFile(folderDownloadPath, assetDto);
    response.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.d(LOG_TAG, "onFailure asset service download asset");
      }

      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
          Log.d(LOG_TAG, "server contacted and has file");
          String filePath = folderDownloadPath + File.separator + assetDto.getName();
          Thread thread = new Thread(() -> {
            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), filePath);
            Log.d(LOG_TAG, "file download was a success? " + writtenToDisk + " - "
                    + filePath);
          });
          thread.start();
        } else {
          Log.d(LOG_TAG, "server contact failed");
        }
      }
    });
  }

  private boolean writeResponseBodyToDisk(ResponseBody body, String filePath) {
    try {
      // todo change the file location/name according to your needs
      File file = new File(filePath);
      InputStream inputStream = null;
      OutputStream outputStream = null;
      try {
        //buffer size 4096
        byte[] fileReader = new byte[4096];
        long fileSize = body.contentLength();
        long fileSizeDownloaded = 0;
        inputStream = body.byteStream();
        outputStream = new FileOutputStream(file);
        while (true) {
          int read = inputStream.read(fileReader);
          if (read == -1) {
            break;
          }
          outputStream.write(fileReader, 0, read);
          fileSizeDownloaded += read;
          Log.d(LOG_TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
        }
        outputStream.flush();
        return true;
      } catch (IOException e) {
        return false;
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
        if (outputStream != null) {
          outputStream.close();
        }
      }
    } catch (IOException e) {
      return false;
    }
  }

  public AssetDto get(String id, String accessToken) throws VimojoApiException {
    try {
      Response<AssetDto> response = getAssetService(accessToken).get(id).execute();
      if (response.isSuccessful()) {
        return response.body();
      } else {
        parseError(response);
      }
    } catch (IOException e) {
      throw new VimojoApiException(-1, VimojoApiException.NETWORK_ERROR);
    }
    throw new VimojoApiException(); // TODO(jliarte): 19/07/18 default unknown error - check when this path is reached
  }

  private AssetService getAssetService(String accessToken) {
    return getService(AssetService.class, accessToken);
  }
}
