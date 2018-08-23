/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sync.presentation;

/**
 * Created by alvaro on 31/1/18.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.videonasocialmedia.vimojo.main.SystemComponent;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.repository.upload.UploadDataSource;
import com.videonasocialmedia.vimojo.sync.AssetUploadQueue;

import javax.inject.Inject;

/**
 * Define a Service that returns an IBinder for the
 * sync adapter class, allowing the sync adapter framework to call
 * onPerformSync().
 */
public class SyncService extends Service {
  private static final String LOG_TAG = SyncService.class.getSimpleName();
  // Storage for an instance of the sync adapter
  private SyncAdapter sSyncAdapter = null;
  // Object to use as a thread-safe lock
  private static final Object sSyncAdapterLock = new Object();

  @Inject UploadToPlatform uploadToPlatform;
  @Inject AssetUploadQueue assetUploadQueue;
  @Inject UploadDataSource uploadRepository;

  /*
   * Instantiate the sync adapter object.
   */
  @Override
  public void onCreate() {
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
    getSystemComponent().inject(this);
    Log.d(LOG_TAG, "onCreate SyncService...");

    synchronized (sSyncAdapterLock) {
      if (sSyncAdapter == null) {
        sSyncAdapter = new SyncAdapter(getApplicationContext(), true,
            uploadToPlatform, assetUploadQueue, uploadRepository);
      }
    }
  }

  public SystemComponent getSystemComponent() {
    return ((VimojoApplication)getApplication()).getSystemComponent();
  }

  /**
   * Return an object that allows the system to invoke
   * the sync adapter.
   */
  @Override
  public IBinder onBind(Intent intent) {
        /*
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
    return sSyncAdapter.getSyncAdapterBinder();
  }
}
