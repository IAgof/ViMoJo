package com.videonasocialmedia.vimojo.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by alvaro on 31/1/18.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = "SyncAdapter";
    public static final int SYNC_INTERVAL               = 20; //60 * 180;       // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_FLEXTIME               = SYNC_INTERVAL/3;
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver contentResolver;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");
        //Check files to upload

    }

}
