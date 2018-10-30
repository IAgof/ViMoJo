/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync;

/**
 * Created by alvaro on 6/2/18.
 */

import android.content.Context;
import android.util.Log;

import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.crashlytics.android.Crashlytics;
import com.squareup.moshi.Moshi;
import com.squareup.tape2.ObjectQueue;
import com.squareup.tape2.QueueFile;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.asset.domain.model.Asset;
import com.videonasocialmedia.vimojo.auth0.GetUserId;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.vimojoapiclient.AssetApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

/**
 * Class to unify video uploads to platform.
 * Create/init ObjectQueue, add objects and launchQueue.
 * FIFO, atomic ObjectQueue.
 */
public class AssetUploadQueue {
  private final String LOG_TAG = AssetUploadQueue.class.getCanonicalName();
  private final Context context;
  private final AssetApiClient assetApiClient;
  private final UserAuth0Helper userAuth0Helper;
  private final GetUserId getUserId;

  @Inject
  public AssetUploadQueue(Context context, AssetApiClient assetApiClient,
                          UserAuth0Helper userAuth0Helper, GetUserId getUserId) {
    this.context = context;
    this.assetApiClient = assetApiClient;
    this.userAuth0Helper = userAuth0Helper;
    this.getUserId = getUserId;
    Log.d(LOG_TAG, "Created sync queue...");
  }

  public ObjectQueue<Asset> getQueue() {
    Log.d(LOG_TAG, "getting queue...");
    String uploadQUEUE = "QueueUploads_" + BuildConfig.FLAVOR;
    File file = new File(context.getFilesDir(), uploadQUEUE);
    ObjectQueue<Asset> assetUploadObjectQueue = null;
    try {
      QueueFile queueFile = new QueueFile.Builder(file).build();
      Moshi moshi = new Moshi.Builder().build();
      MoshiConverter converter = new MoshiConverter(moshi, Asset.class);
      assetUploadObjectQueue = ObjectQueue.create(queueFile, converter);
    } catch (IOException ioException) {
      ioException.printStackTrace();
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error creating queue video to upload");
      Crashlytics.logException(ioException);
    }
    Log.d(LOG_TAG, "...returned queue of size " + assetUploadObjectQueue.size());
    return assetUploadObjectQueue;
  }

  public void addAssetToUpload(Asset asset) throws IOException {
    ObjectQueue<Asset> queue = getQueue();
    queue.add(asset);
  }

  public void processNextQueueItem() {
    Log.d(LOG_TAG, "processNextQueueItem");
    Log.d(LOG_TAG, "startNotification");
    Asset element = getQueue().iterator().next();
    userAuth0Helper.getAccessToken(new BaseCallback<Credentials, CredentialsManagerException>() {
      @Override
      public void onFailure(CredentialsManagerException error) {
        //No credentials were previously saved or they couldn't be refreshed
        Log.d(LOG_TAG, "processAsyncUpload, getAccessToken onFailure No credentials were " +
            "previously saved or they couldn't be refreshed");
        Crashlytics.log("Error processAsyncUpload getAccessToken");
      }

      @Override
      public void onSuccess(Credentials credentials) {
        try {
          // TODO(jliarte): 27/02/18 check what to do with plaform response
          Log.d(LOG_TAG, "uploading video ... videoApiClient.uploadVideo");
          AssetDto assetDto = assetApiClient.addAsset(credentials.getAccessToken(), element);
          // TODO(jliarte): 18/07/18 assign assetId to backend media object
          Log.d(LOG_TAG, "uploaded video ... videoApiClient.uploadVideo");
          removeHeadElement(getQueue());
          Log.d(LOG_TAG, "finish upload success");
        } catch (VimojoApiException vimojoApiException) {
          Log.d(LOG_TAG, "vimojoApiException " + vimojoApiException.getApiErrorCode());
          Crashlytics.log("Error process upload vimojoApiException");
          Crashlytics.logException(vimojoApiException);
          switch (vimojoApiException.getApiErrorCode()) {
            case VimojoApiException.UNAUTHORIZED:
              // TODO: 21/6/18 inform user
              Log.d(LOG_TAG, "VimojoApiException.UNAUTHORIZED");
              break;
            case VimojoApiException.NETWORK_ERROR:
              Log.d(LOG_TAG, "VimojoApiException.NETWORK_ERROR");
              // TODO: 21/6/18 inform user
              break;
            case VimojoApiException.QUERY_ERROR:
              Log.d(LOG_TAG, "VimojoApiException.QUERY_ERROR");
              removeHeadElement(getQueue());
              break;
            default:
              retryItemUpload(element);
          }
        } catch (FileNotFoundException fileNotFoundError) {
          if (BuildConfig.DEBUG) {
            fileNotFoundError.printStackTrace();
          }
          Log.d(LOG_TAG, "File " + element.getPath() + " trying to upload does not exists!");
          removeHeadElement(getQueue());
        }
      }
    });
  }

//  private void assignAssetIdToMedia(String assetId, String mediaId) {
//    mediaApiDataSource.get().assignAssetIdToMedia(mediaId, assetId);
//  }


  protected void retryItemUpload(Asset element) {
    incrementHeadNumTries(getQueue());
    if (element.getNumTries() > Asset.MAX_NUM_TRIES_UPLOAD) {
      removeHeadElement(getQueue());
      Log.d(LOG_TAG, "finishNotification, error");
    }
  }

  private void incrementHeadNumTries(ObjectQueue<Asset> queue) {
    try {
      queue.peek().incrementNumTries();
    } catch (IOException ioException) {
      Log.d(LOG_TAG, ioException.getMessage());
      Crashlytics.log("Error increment num tries head of queue video to upload");
      Crashlytics.logException(ioException);
    }
  }

  private void removeHeadElement(ObjectQueue<Asset> queue) {
    if (queue.iterator().hasNext()) {
      try {
        queue.remove();
      } catch (IOException ioException) {
        Log.d(LOG_TAG, ioException.getMessage());
        Crashlytics.log("Error removing queue video to upload");
        Crashlytics.logException(ioException);
      }
    }
  }
}
