package com.videonasocialmedia.vimojo.sync;

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
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import java.io.IOException;


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
  private UploadToPlatformQueue uploadToPlatformQueue;

  /**
   * Set up the sync adapter
   */
  public SyncAdapter(Context context, boolean autoInitialize,
                     UploadToPlatformQueue uploadToPlatformQueue) {
    super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
    this.context = context;
    this.uploadToPlatformQueue = uploadToPlatformQueue;
  }


  @Override
  public void onPerformSync(Account account, Bundle bundle, String s,
                            ContentProviderClient contentProviderClient, SyncResult syncResult) {
    Log.d(LOG_TAG, "onPerformSync");
    ObjectQueue<VideoUpload> queue = uploadToPlatformQueue.getQueue();
    if (!queue.isEmpty()) {
      boolean isAcceptedUploadMobileNetwork = false;
      int notificationId = 0;
      try {
        isAcceptedUploadMobileNetwork = queue.peek().isAcceptedUploadMobileNetwork();
        if(!isThereNetworkConnected(isAcceptedUploadMobileNetwork)) {
          return;
        }
        while (uploadToPlatformQueue.getQueue().iterator().hasNext() &&
            isThereNetworkConnected(isAcceptedUploadMobileNetwork)) {
          Log.d(LOG_TAG, "launchingQueue");
          notificationId = queue.peek().getId();
          uploadToPlatformQueue.processNextQueueItem(notificationId);
        }
      } catch (IOException ioException) {
        Log.d(LOG_TAG, ioException.getMessage());
        Crashlytics.log("Error getting queue element, isAcceptedUploadMobileNetwork");
        Crashlytics.logException(ioException);
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
