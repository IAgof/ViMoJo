/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.views.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

public class ChooseCameraQualityListPreferences extends ListPreference {

    private Context mContext;
    private CharSequence[] entries;
    private CharSequence[] entryValues;
    private SharedPreferences sharedPreferences;

    public ChooseCameraQualityListPreferences(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        sharedPreferences =  mContext.getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder)
    {
        entries = getEntries();
        entryValues = getEntryValues();

        if (entries == null || entryValues == null || entries.length != entryValues.length )
        {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");

        }

        String prefsQuality = sharedPreferences.getString(ConfigPreferences.KEY_LIST_PREFERENCES_QUALITY,
                mContext.getResources().getString(R.string.high_quality_name));

        builder.setTitle(R.string.quality);

        //list of items
        final String[] items = mContext.getResources().getStringArray(R.array.camera_quality_names);
        int positionItemSelected = 0;

        for (String quality : items){
            if ( quality.compareTo(prefsQuality) == 0){
                break;
            }
            positionItemSelected++;
        }

        builder.setSingleChoiceItems(items, positionItemSelected,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setValue(items[which]);
                        getDialog().dismiss();
                    }
                });

        String positiveText = mContext.getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        getDialog().dismiss();
                    }
                });


        String negativeText = mContext.getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        getDialog().cancel();
                    }
                });

    }

    // NOTE:
    // The framework forgot to call notifyChanged() in setValue() on previous versions of android.
    // This bug has been fixed in android-4.4_r0.7.
    // Commit: platform/frameworks/base/+/94c02a1a1a6d7e6900e5a459e9cc699b9510e5a2
    // Time: Tue Jul 23 14:43:37 2013 -0700
    //
    // However on previous versions, we have to workaround it by ourselves.
    @Override
    public void setValue(String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.setValue(value);
        } else {
            String oldValue = getValue();
            super.setValue(value);
            if (!TextUtils.equals(value, oldValue)) {
                notifyChanged();
            }
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
    }

}
