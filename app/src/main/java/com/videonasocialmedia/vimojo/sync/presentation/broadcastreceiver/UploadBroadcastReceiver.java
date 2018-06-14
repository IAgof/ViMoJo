/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.presentation.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.videonasocialmedia.vimojo.main.SystemComponent;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.repository.upload.UploadRepository;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;
import com.videonasocialmedia.vimojo.sync.presentation.UploadToPlatform;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import javax.inject.Inject;

/**
 * Created by alvaro on 27/5/18.
 */

public class UploadBroadcastReceiver extends BroadcastReceiver {

  @Inject UploadToPlatform uploadToPlatform;
  @Inject UploadRepository uploadRepository;
  @Inject RunSyncAdapterHelper runSyncAdapterHelper;
  private Context context;
  private String LOG_TAG = UploadBroadcastReceiver.class.getName();

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(LOG_TAG, "onReceive "  + intent.getData());
    this.context = context;
    if (context.getApplicationContext() instanceof VimojoApplication) {
      getSystemComponent().inject(this);
    }
    if (intent.getAction().equals(IntentConstants.ACTION_CANCEL_UPLOAD)) {
      String UUID = intent.getStringExtra(IntentConstants.VIDEO_UPLOAD_UUID);
      Log.d(LOG_TAG,  "cancel upload notification broadcast " + UUID);
      VideoUpload videoUpload = uploadRepository.getVideoToUploadByUUID(UUID);
      if (videoUpload != null) {
        Log.d(LOG_TAG,  "runSyncAdapterHelper");
        runSyncAdapterHelper.cancelUpload(videoUpload.getUuid());
      }
    }
    if (intent.getAction().equals(IntentConstants.ACTION_PAUSE_UPLOAD)) {
      String UUID = intent.getStringExtra(IntentConstants.VIDEO_UPLOAD_UUID);
      Log.d(LOG_TAG,  "pause upload notification broadcast " + UUID);
      VideoUpload videoUpload = uploadRepository.getVideoToUploadByUUID(UUID);
      if (videoUpload != null) {
        runSyncAdapterHelper.pauseUpload(videoUpload.getUuid());
      }
    }

    if (intent.getAction().equals(IntentConstants.ACTION_ACTIVATE_UPLOAD)) {
      String UUID = intent.getStringExtra(IntentConstants.VIDEO_UPLOAD_UUID);
      Log.d(LOG_TAG,  "activate upload notification broadcast " + UUID);
      VideoUpload videoUpload = uploadRepository.getVideoToUploadByUUID(UUID);
      if (videoUpload != null) {
        runSyncAdapterHelper.relaunchUpload(videoUpload.getUuid());
      }
    }

    if (intent.getAction().equals(IntentConstants.ACTION_REMOVE_UPLOAD)) {
      Log.d(LOG_TAG,  "remove, clear notification");
      if (uploadRepository.getAllVideosToUpload().size() > 0) {
        uploadRepository.removeAllVideosToUpload();
        runSyncAdapterHelper.removeVideosToUpload();
      }
    }
  }
  public SystemComponent getSystemComponent() {
    VimojoApplication vimojoApplication = ((VimojoApplication)context.getApplicationContext());
    return vimojoApplication.getSystemComponent();
  }
}
