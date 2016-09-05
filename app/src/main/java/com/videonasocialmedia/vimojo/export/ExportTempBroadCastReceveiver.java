package com.videonasocialmedia.vimojo.export;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 *
 */
public class ExportTempBroadCastReceveiver extends BroadcastReceiver {

    private View parent;

    public ExportTempBroadCastReceveiver(View parent) {
        this.parent = parent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //// TODO:(alvaro.martinez) 22/08/16 Here manage error trimming case, define new user story
        boolean data = intent.getBooleanExtra("videoExported", false);
        int videoId = intent.getIntExtra("videoId", 0);

      //  Snackbar.make(parent, R.string.trimError, Snackbar.LENGTH_LONG);

     //   Snackbar.make(null,"error trimming", Snackbar.LENGTH_LONG);
    }
}
