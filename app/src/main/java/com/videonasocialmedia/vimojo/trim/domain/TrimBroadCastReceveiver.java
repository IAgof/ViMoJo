package com.videonasocialmedia.vimojo.trim.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.videonasocialmedia.vimojo.R;

/**
 *
 */
public class TrimBroadCastReceveiver extends BroadcastReceiver {

    private View parent;

    public TrimBroadCastReceveiver(View parent) {
        this.parent = parent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       // Snackbar.make(parent, R.string.trimError, Snackbar.LENGTH_LONG);

        Snackbar.make(null,"error trimming", Snackbar.LENGTH_LONG);
    }
}
