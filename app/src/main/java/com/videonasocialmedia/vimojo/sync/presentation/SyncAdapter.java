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

import com.crashlytics.android.Crashlytics;
import com.squareup.tape2.ObjectQueue;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.repository.upload.UploadRepository;
import com.videonasocialmedia.vimojo.sync.UploadToPlatformQueue;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.io.IOException;
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
  private UploadToPlatformQueue uploadToPlatformQueue;
  private UploadRepository uploadRepository;

  /**
   * Set up the sync adapter
   */
  public SyncAdapter(Context context, boolean autoInitialize,
                     UploadToPlatform uploadToPlatform,
                     UploadToPlatformQueue uploadToPlatformQueue,
                     UploadRepository uploadRepository) {
    super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
    this.context = context;
    this.uploadToPlatform = uploadToPlatform;
    this.uploadToPlatformQueue = uploadToPlatformQueue;
    this.uploadRepository = uploadRepository;
    Log.d(LOG_TAG, "created SyncAdapter...");
  }


  @Override
  public void onPerformSync(Account account, Bundle bundle, String s,
                            ContentProviderClient contentProviderClient, SyncResult syncResult) {
    Log.d(LOG_TAG, "onPerformSync");
    if (bundle.getBoolean(IntentConstants.ACTION_PAUSE_UPLOAD)) {
      Log.d(LOG_TAG, "onPerformSync ACTION_PAUSE_UPLOAD");
      String videoUploadUuid = bundle.getString(IntentConstants.VIDEO_UPLOAD_UUID);
      VideoUpload videoUpload = uploadRepository.getVideoToUploadByUUID(videoUploadUuid);
      if (videoUpload != null) {
        uploadToPlatform.pauseUploadByUser(videoUpload);
      }
    } else {
      if (bundle.getBoolean(IntentConstants.ACTION_CANCEL_UPLOAD)) {
        Log.d(LOG_TAG, "onPerformSync ACTION_CANCEL_UPLOAD");
        String videoUploadUuid = bundle.getString(IntentConstants.VIDEO_UPLOAD_UUID);
        VideoUpload videoUpload = uploadRepository.getVideoToUploadByUUID(videoUploadUuid);
        if (videoUpload != null) {
          uploadToPlatform.cancelUploadByUser(videoUpload);
        }
      } else {
        if (bundle.getBoolean(IntentConstants.ACTION_ACTIVATE_UPLOAD)) {
          Log.d(LOG_TAG, "onPerformSync ACTION_ACTIVATE_UPLOAD");
          String videoUploadUuid = bundle.getString(IntentConstants.VIDEO_UPLOAD_UUID);
          VideoUpload videoUpload = uploadRepository.getVideoToUploadByUUID(videoUploadUuid);
          if (videoUpload != null) {
            uploadToPlatform.processAsyncUpload(videoUpload);
          }
        } else {
          if (bundle.getBoolean(IntentConstants.ACTION_REMOVE_UPLOAD)) {
            Log.d(LOG_TAG, "onPerformSync ACTION_REMOVE_UPLOAD");
            uploadToPlatform.removeUploadByUser();
          } else {
            // Pending videos to upload.
            for (VideoUpload video : uploadRepository.getAllVideosToUpload()) {
              Log.d(LOG_TAG, "video to upload: " + video.getUuid());
              if (!video.isUploading() && (video.getNumTries() < VideoUpload.MAX_NUM_TRIES_UPLOAD)) {
                Log.d(LOG_TAG, "launching video to upload: ");
                if (areThereNetworksConnected(video.isAcceptedUploadMobileNetwork())) {
                  uploadToPlatform.processAsyncUpload(video);
                }
              }
            }
          }
        }
      }
    }

    // QUEUE model
    ObjectQueue<VideoUpload> queue = uploadToPlatformQueue.getQueue();
    if (!queue.isEmpty()) {
      try {
        while (uploadToPlatformQueue.getQueue().iterator().hasNext()) {
          Log.d(LOG_TAG, "launchingQueue");
          boolean isAcceptedUploadMobileNetwork = queue.peek().isAcceptedUploadMobileNetwork();
          if (areThereNetworksConnected(isAcceptedUploadMobileNetwork)) {
            // TODO(jliarte): 5/03/18 will stuck on item that not meet network criteria, maybe
            // reimplement this loop
            uploadToPlatformQueue.processNextQueueItem();
          }
          sleep(); // TODO(jliarte): 9/03/18 when looping while, waiting for network, high CPU usage
        }
      } catch (IOException ioException) {
        Log.d(LOG_TAG, ioException.getMessage());
        if (BuildConfig.DEBUG) {
          // TODO(jliarte): 5/03/18 I'm sometimes getting an error here, even with a non empty queue
          // file (maybe it gets corrupted somehow?) not able to reproduce properly. deeply
          // investigate how to deal with it
          ioException.printStackTrace();
        }
        Crashlytics.log("Error getting queue element, isAcceptedUploadMobileNetwork");
        Crashlytics.logException(ioException);
      }
    }
  }

  private void sleep() {
    try {
      TimeUnit.SECONDS.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private boolean areThereNetworksConnected(boolean isAcceptedUploadMobileNetwork) {
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
