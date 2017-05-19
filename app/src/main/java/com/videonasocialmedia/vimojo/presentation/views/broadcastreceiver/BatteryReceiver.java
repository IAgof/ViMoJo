package com.videonasocialmedia.vimojo.presentation.views.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import com.videonasocialmedia.vimojo.utils.IntentConstants;


public class BatteryReceiver extends BroadcastReceiver {
  private int status;
  private int level;
  private int scale;

  @Override
  public void onReceive(Context context, Intent intent) {
    level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    status= getStatusBattery(intent);
    sendParametersBattery(context, level, scale, status);
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

  private void sendParametersBattery(Context context, int level, int scale, int status) {
    Intent statusBatteryIntent= new Intent(IntentConstants.BATTERY_NOTIFICATION);
    statusBatteryIntent.putExtra(IntentConstants.BATTERY_STATUS,status);
    statusBatteryIntent.putExtra(IntentConstants.BATTERY_LEVEL, level);
    statusBatteryIntent.putExtra(IntentConstants.BATTERY_SCALE, scale);
    context.sendBroadcast(statusBatteryIntent);
  }
}
