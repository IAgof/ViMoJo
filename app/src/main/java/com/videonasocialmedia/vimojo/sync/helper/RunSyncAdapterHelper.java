/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.helper;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.os.Bundle;
import android.util.Log;

import com.videonasocialmedia.vimojo.auth.util.UserAccountUtil;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import javax.inject.Inject;

/**
 * Created by alvaro on 23/2/18.
 *
 * Helper to launch, run sync adapter.
 *
 * Manage when we want to run sync service, immediately or periodically
 *
 */

public class RunSyncAdapterHelper {

  private final String LOG_TAG = this.getClass().getSimpleName();

  private static final long SECONDS_PER_MINUTE = 60L;
  private static final long SYNC_INTERVAL_IN_MINUTES = 1L;
  private static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
  private static final long SYNC_FLEX_TIME =  SYNC_INTERVAL/3;

  private final Context context;

  // A content resolver for accessing the provider
  ContentResolver contentResolver;
  private String authority;

  @Inject
  public RunSyncAdapterHelper(Context context) {
    this.context = context;
    authority = SyncConstants.VIMOJO_CONTENT_AUTHORITY;
  }

  public void runSyncAdapterPeriodically() {
    Log.d(LOG_TAG, "runSyncAdapterPeriodic");
    // Get the content resolver for your app
    contentResolver = context.getContentResolver();
    Account account = UserAccountUtil.getAccount(context);
    // we can enable inexact timers in our periodic sync
    SyncRequest request = new SyncRequest.Builder().
        syncPeriodic(SYNC_INTERVAL, SYNC_FLEX_TIME).
        setSyncAdapter(account, SyncConstants.VIMOJO_CONTENT_AUTHORITY).
        setExtras(new Bundle()).build();
    if (account != null) {
      Log.d(LOG_TAG, "Setting auto sync...");
      contentResolver.requestSync(request);
      ContentResolver.setSyncAutomatically(account, SyncConstants.VIMOJO_CONTENT_AUTHORITY,
          true);
    }
  }

  public void runNowSyncAdapter() {
    Log.d(LOG_TAG, "Run NOW SyncAdapter...");
    Bundle settingsBundle = getBaseSettingsBundle();
    requestSync(settingsBundle);
  }

  public void startUpload(String videoUploadUuid) {
    Log.d(LOG_TAG, "Start upload " + videoUploadUuid);
    Log.d(LOG_TAG, "Run NOW SyncAdapter...");
    // Pass the settings flags by inserting them in a bundle
    Bundle settingsBundle = getBaseSettingsBundle();
    settingsBundle.putString(IntentConstants.VIDEO_UPLOAD_UUID, videoUploadUuid);
    requestSync(settingsBundle);
  }

  public void pauseUpload(String videoUploadUuid) {
    Log.d(LOG_TAG, "Pause upload " + videoUploadUuid);
    Log.d(LOG_TAG, "Run NOW SyncAdapter...");
    Bundle settingsBundle = getBaseSettingsBundle();
    settingsBundle.putString(IntentConstants.VIDEO_UPLOAD_UUID, videoUploadUuid);
    settingsBundle.putBoolean(IntentConstants.ACTION_PAUSE_ACTIVATE_UPLOAD, true);
    requestSync(settingsBundle);
  }

  public void relaunchUpload(String videoUploadUuid) {
    Log.d(LOG_TAG, "RelaunchUpload upload " + videoUploadUuid);
    Log.d(LOG_TAG, "Run NOW SyncAdapter...");
    // Pass the settings flags by inserting them in a bundle
    Bundle settingsBundle = getBaseSettingsBundle();
    settingsBundle.putString(IntentConstants.VIDEO_UPLOAD_UUID, videoUploadUuid);
    settingsBundle.putBoolean(IntentConstants.ACTION_ACTIVATE_UPLOAD, true);
    requestSync(settingsBundle);
  }

  public void cancelUpload(String videoUploadUuid) {
    Log.d(LOG_TAG, "Cancel upload " + videoUploadUuid);
    Log.d(LOG_TAG, "Run NOW SyncAdapter...");
    Bundle settingsBundle = getBaseSettingsBundle();
    settingsBundle.putString(IntentConstants.VIDEO_UPLOAD_UUID, videoUploadUuid);
    settingsBundle.putBoolean(IntentConstants.ACTION_CANCEL_UPLOAD, true);
    requestSync(settingsBundle);
  }

  private Bundle getBaseSettingsBundle() {
    Bundle settingsBundle = new Bundle();
    settingsBundle.putBoolean(
        ContentResolver.SYNC_EXTRAS_MANUAL, true);
    settingsBundle.putBoolean(
        ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    return settingsBundle;
  }

  /*
   * Request the sync for the default account, authority, and
   * manual sync settings
   */
  public void requestSync(Bundle settingsBundle) {
    Account account = UserAccountUtil.getAccount(context);
    if (account != null) {
      Log.d(LOG_TAG, "Requesting sync!");
      ContentResolver.requestSync(account, authority, settingsBundle);
    }
  }

  public void removeVideosToUpload() {
    Bundle settingsBundle = getBaseSettingsBundle();
    settingsBundle.putBoolean(IntentConstants.ACTION_REMOVE_UPLOAD, true);
    requestSync(settingsBundle);
  }
}
