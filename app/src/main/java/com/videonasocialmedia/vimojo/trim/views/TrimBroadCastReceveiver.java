package com.videonasocialmedia.vimojo.trim.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

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
        //llamar a la interfaz para que muestre el mensaje.

        Snackbar.make(parent, "The video couldn't be trimmed", Snackbar.LENGTH_LONG);
    }
}
