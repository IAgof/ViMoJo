package com.videonasocialmedia.vimojo.sync;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.util.UserAccountUtil;

/**
 * Created by alvaro on 1/2/18.
 */

/**
 * BroadcastReceiver to detect Wifi network connection
 * If connect is detected, launch SyncAdapter to check queue of videos pending to uploads.
 */
public class WifiReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    NetworkInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
    if (wifiInfo != null && wifiInfo.isConnected()) {
      runSyncAdapter(context);
    }
  }

  private void runSyncAdapter(Context context) {
    Account account = UserAccountUtil.getAccount(context);
    String authority = context.getString(R.string.content_authority);

    // Pass the settings flags by inserting them in a bundle
    Bundle settingsBundle = new Bundle();
    settingsBundle.putBoolean(
        ContentResolver.SYNC_EXTRAS_MANUAL, true);
    settingsBundle.putBoolean(
        ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
    ContentResolver.requestSync(account, authority, settingsBundle);
  }
}
