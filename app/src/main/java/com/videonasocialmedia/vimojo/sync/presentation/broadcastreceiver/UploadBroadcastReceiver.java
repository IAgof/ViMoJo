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
  private Context context;
  private String LOG_TAG = UploadBroadcastReceiver.class.getName();

  @Override
  public void onReceive(Context context, Intent intent) {
    this.context = context;
    if (context.getApplicationContext() instanceof VimojoApplication) {
      getSystemComponent().inject(this);
    }
    LOG_TAG = "UploadBroadcastReceiver";
    Log.d(LOG_TAG,  "intent " + intent.getAction().toString());
    if (intent.getAction().equals(IntentConstants.ACTION_CANCEL_UPLOAD)) {
      // UploadNotification cancel
      Log.d(LOG_TAG,  "cancel upload notification broadcast");
      String UUID = intent.getStringExtra(IntentConstants.VIDEO_UPLOAD_UUID);
      VideoUpload videoUpload = uploadRepository.getVideoToUploadByUUID(UUID);
      if (videoUpload != null) {
        uploadToPlatform.cancelUploadByUser(videoUpload);
      }
    }
    if (intent.getAction().equals(IntentConstants.ACTION_PAUSE_UPLOAD)) {
      // UploadNotification pause
      Log.d(LOG_TAG,  "pause upload notification broadcast");
      String UUID = intent.getStringExtra(IntentConstants.VIDEO_UPLOAD_UUID);
      VideoUpload videoUpload = uploadRepository.getVideoToUploadByUUID(UUID);
      if (videoUpload != null) {
        uploadToPlatform.pauseUploadByUser(videoUpload);
      }
    }
  }
  public SystemComponent getSystemComponent() {
    VimojoApplication vimojoApplication = ((VimojoApplication)context.getApplicationContext());
    return vimojoApplication.getSystemComponent();
  }
}
