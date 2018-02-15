package com.videonasocialmedia.vimojo.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.squareup.tape2.ObjectQueue;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

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
  // Global variables
  // Define a variable to contain a content resolver instance
  private final ContentResolver contentResolver;

  private UploadToPlatformQueue uploadToPlatformQueue;

  /**
   * Set up the sync adapter
   */
  public SyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
    this.contentResolver = context.getContentResolver();
    uploadToPlatformQueue = new UploadToPlatformQueue(context);
  }

  @Override
  public void onPerformSync(Account account, Bundle bundle, String s,
                            ContentProviderClient contentProviderClient, SyncResult syncResult) {
    Log.d(LOG_TAG, "onPerformSync");
    ObjectQueue<VideoUpload> queue = uploadToPlatformQueue.getQueue();
    while(uploadToPlatformQueue.getQueue().iterator().hasNext()) {
      Log.d(LOG_TAG, "launchingQueue");
      uploadToPlatformQueue.launchQueueVideoUploads();
    }

  }

}
