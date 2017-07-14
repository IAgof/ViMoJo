package com.videonasocialmedia.vimojo.record.presentation.views.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import com.videonasocialmedia.vimojo.utils.IntentConstants;


public class BatteryReceiver extends BroadcastReceiver {
  private int status;

  @Override
  public void onReceive(Context context, Intent intent) {
    status= getStatusBattery(intent);
    sendStatusBattery(context, status);
  }

  private int getStatusBattery(Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
      status = BatteryManager.BATTERY_STATUS_CHARGING;
    } else {
      intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
      status = BatteryManager.BATTERY_STATUS_NOT_CHARGING;
    }
    return status;
  }

  private void sendStatusBattery(Context context, int status) {
    Intent statusBatteryIntent= new Intent(IntentConstants.BATTERY_NOTIFICATION);
    statusBatteryIntent.putExtra(IntentConstants.BATTERY_STATUS,status);
    context.sendBroadcast(statusBatteryIntent);
  }
}
