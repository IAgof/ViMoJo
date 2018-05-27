/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.videonasocialmedia.vimojo.main.SystemComponent;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import javax.inject.Inject;

/**
 * Created by alvaro on 27/5/18.
 */

public class CancelUploadBroadcastReceiver  extends BroadcastReceiver {

  @Inject UploadToPlatformQueue uploadToPlatformQueue;
  private Context context;
  private String LOG_TAG = CancelUploadBroadcastReceiver.class.getName();

  @Override
  public void onReceive(Context context, Intent intent) {
    this.context = context;
    if (context.getApplicationContext() instanceof VimojoApplication) {
      getSystemComponent().inject(this);
    }
    LOG_TAG = "CancelUploadBroadcastReceiver";
    Log.d(LOG_TAG,  "intent " + intent.getAction().toString());
    if (intent.getAction().equals(IntentConstants.ACTION_CANCEL_UPLOAD)) {
      // UploadNotification cancel
      Log.d(LOG_TAG,  "cancel upload notification broadcast");
      uploadToPlatformQueue.cancelUploadByUser();
    }
  }
  public SystemComponent getSystemComponent() {
    VimojoApplication vimojoApplication = ((VimojoApplication)context.getApplicationContext());
    return vimojoApplication.getSystemComponent();
  }
}
