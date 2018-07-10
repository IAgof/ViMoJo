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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;

/**
 * Created by alvaro on 1/2/18.
 */

/**
 * BroadcastReceiver to detect Wifi/Mobile network connection
 * If connect is detected, launch SyncAdapter to check queue of videos pending to uploads.
 */
public class WifiMobileNetworkReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    ConnectivityManager connManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    NetworkInfo mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    if(wifi.isConnected() || mobileNetwork.isConnected()) {
      // TODO: 28/6/18 inject RunSyncAdapterHelper to constructor
      RunSyncAdapterHelper runSyncAdapterHelper = new RunSyncAdapterHelper(context);
      runSyncAdapterHelper.runNowSyncAdapter();
    }
  }
}
