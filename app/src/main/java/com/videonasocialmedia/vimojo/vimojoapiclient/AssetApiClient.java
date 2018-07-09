/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.vimojoapiclient;

import android.support.annotation.NonNull;
import android.util.Log;

import com.videonasocialmedia.vimojo.vimojoapiclient.model.Asset;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MIME_TYPE_VIDEO;
import static com.videonasocialmedia.vimojo.vimojoapiclient.ApiConstants.MULTIPART_NAME_DATA;

/**
 * Created by alvaro on 21/6/18.
 */

public class AssetApiClient extends VimojoApiClient {

  private String LOG_TAG = AssetApiClient.class.getCanonicalName();

  public static final String ASSET_API_KEY_NAME = "name";
  public static final String ASSET_API_KEY_TYPE = "type";
  public static final String ASSET_API_KEY_HASH = "hash";
  public static final String ASSET_API_KEY_DATE = "date";

  /**
   * Make a upload video call to send video to platform
   *
   * @param authToken   valid token to validate call to service
   * @param assetUpload Model for enqueue video uploads to vimojo platform.
   * @return the video upload response of the platform service
   * @throws VimojoApiException if an error has occurred in the call.
   */
  public Asset uploadVideo(String authToken, AssetUpload assetUpload)
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
    RequestBody requestBodyName= createPartFromString(assetUpload.getName());
    RequestBody requestBodyType = createPartFromString(assetUpload.getType());
    RequestBody requestBodyHash= createPartFromString(assetUpload.getHash());
    RequestBody requestBodyDate= createPartFromString(assetUpload.getDate());

    HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>();
    requestBodyHashMap.put(ASSET_API_KEY_NAME, requestBodyName);
    requestBodyHashMap.put(ASSET_API_KEY_TYPE, requestBodyType);
    requestBodyHashMap.put(ASSET_API_KEY_HASH, requestBodyHash);
    requestBodyHashMap.put(ASSET_API_KEY_DATE, requestBodyDate);
    Call<Asset> assetUploadTask = assetService.uploadAsset(assetUpload.getId(),
        requestBodyHashMap, body);
    try {
      Response<Asset> response = assetUploadTask.execute();
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


  public void assetListToDownload(String authToken, String folderDownloadPath)
      throws VimojoApiException {
    // create upload service client
    AssetService assetService = getService(AssetService.class, authToken);

    Call<List<Asset>> response = assetService.assetListToDownload();
    response.enqueue(new Callback<List<Asset>>() {
      @Override
      public void onFailure(Call<List<Asset>> call, Throwable t) {
        // TODO: 9/7/18 manage retrofit failures 
        Log.d(LOG_TAG, "onFailure asset service asset list to download");
      }

      @Override
      public void onResponse(Call<List<Asset>> call, Response<List<Asset>> response) {
        List<Asset> assetList = response.body();
        for(Asset asset: assetList) {
          downloadAssets(authToken, folderDownloadPath, asset);
        }
        // TODO: 9/7/18 Notify download completed
      }
    });

  }

  private void downloadAssets(String authToken, String folderDownloadPath, Asset asset) {

    // create upload service client
    AssetService assetService = getService(AssetService.class, authToken);

    Call<ResponseBody> response = assetService.downloadAsset(folderDownloadPath, asset);
    response.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.d(LOG_TAG, "onFailure asset service download asset");
      }
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
          Log.d(LOG_TAG, "server contacted and has file");
          String filePath = folderDownloadPath + File.separator + asset.getName();
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

}