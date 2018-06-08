/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.presentation;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.repository.upload.UploadRepository;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;
import com.videonasocialmedia.vimojo.sync.presentation.ui.UploadNotification;
import com.videonasocialmedia.vimojo.vimojoapiclient.VideoApiClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by alvaro on 31/1/18.
 */

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

  private static final String LOG_TAG = "SyncAdapter";
  // Sync interval constants
  public static final long SECONDS_PER_MINUTE = 60L;
  public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
  public static final long SYNC_INTERVAL =
      SYNC_INTERVAL_IN_MINUTES *
          SECONDS_PER_MINUTE;
  private Context context;
  private boolean isWifiConnected;
  private boolean isMobileNetworkConnected;
  private UploadToPlatform uploadToPlatform;
  private UploadRepository uploadRepository;

  /**
   * Set up the sync adapter
   */
  public SyncAdapter(Context context, boolean autoInitialize,
                     UploadToPlatform uploadToPlatform, UploadRepository uploadRepository) {
    super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
    this.context = context;
    this.uploadToPlatform = uploadToPlatform;
    this.uploadRepository = uploadRepository;
    Log.d(LOG_TAG, "created SyncAdapter...");
  }


  @Override
  public void onPerformSync(Account account, Bundle bundle, String s,
                            ContentProviderClient contentProviderClient, SyncResult syncResult) {
    Log.d(LOG_TAG, "onPerformSync");
    for(VideoUpload video: uploadRepository.getAllVideosToUpload()) {
      Log.d(LOG_TAG, "video to upload: " + video);
      if (!video.isUploading() && (video.getNumTries() < VideoUpload.MAX_NUM_TRIES_UPLOAD)) {
        Log.d(LOG_TAG, "launching video to upload: " + video);
        UploadNotification uploadNotification = new UploadNotification(context);
        VideoApiClient videoApiClient = new VideoApiClient();
        GetAuthToken getAuthToken = new GetAuthToken();
        UploadToPlatform uploadToPlatform = new UploadToPlatform(context, uploadNotification,
            videoApiClient, getAuthToken, uploadRepository);
        uploadToPlatform.processAsyncUpload(video);
      }
    }
  }

  private boolean isThereNetworkConnected(boolean isAcceptedUploadMobileNetwork) {
    checkNetworksAvailable();
    return isWifiConnected || (isMobileNetworkConnected && isAcceptedUploadMobileNetwork);
  }

  private void checkNetworksAvailable() {
    ConnectivityManager connManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    NetworkInfo mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    isWifiConnected = wifi.isConnected();
    isMobileNetworkConnected = mobileNetwork.isConnected();
  }

}
