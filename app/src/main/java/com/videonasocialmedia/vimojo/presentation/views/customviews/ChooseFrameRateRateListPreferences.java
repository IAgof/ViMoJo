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
import com.videonasocialmedia.vimojo.domain.editor.UpdateVideoFrameRateToProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.util.ArrayList;

class ChooseFrameRateRateListPreferences extends ListPreference {

    private Context mContext;
    private CharSequence[] entries;
    private CharSequence[] entryValues;
    private SharedPreferences sharedPreferences;
    private Project currentProject;
    private UpdateVideoFrameRateToProjectUseCase updateVideoFrameRateToProjectUseCase;

    public ChooseFrameRateRateListPreferences(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        sharedPreferences =  mContext.getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        updateVideoFrameRateToProjectUseCase = new UpdateVideoFrameRateToProjectUseCase();
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

   /*
    @Override
    public CharSequence getSummary() {
        return super.getEntry();
    }
    */

    @Override
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {

        entries = getEntries();
        entryValues = getEntryValues();

        String prefsFrameRate;

        if(sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_25FPS_SUPPORTED, false)){
            prefsFrameRate = sharedPreferences.getString(ConfigPreferences.KEY_LIST_PREFERENCES_FRAME_RATE,
                    mContext.getResources().getString(R.string.good_frame_rate_name));
        } else {
            if (sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_24FPS_SUPPORTED, false)) {
                prefsFrameRate = sharedPreferences.getString(ConfigPreferences.KEY_LIST_PREFERENCES_FRAME_RATE,
                        mContext.getResources().getString(R.string.low_frame_rate_name));
            } else {
                prefsFrameRate = sharedPreferences.getString(ConfigPreferences.KEY_LIST_PREFERENCES_FRAME_RATE, "0");
            }
        }

        if (entries == null || entryValues == null || entries.length != entryValues.length) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");

        }

        builder.setTitle(R.string.frame_rate);

        int positionItemSelected = 0;
        int numItemSupported = 0;

        ArrayList<String> itemsSupported = new ArrayList<String>(mContext.getResources().getStringArray(R.array.camera_frame_rate_names).length);

        if(sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_24FPS_SUPPORTED, false)){
            itemsSupported.add(numItemSupported++,mContext.getResources().getString(R.string.low_frame_rate_name));
        }
        if(sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_25FPS_SUPPORTED, false)){
            itemsSupported.add(numItemSupported++,mContext.getResources().getString(R.string.good_frame_rate_name));
        }
        if(sharedPreferences.getBoolean(ConfigPreferences.CAMERA_FRAME_RATE_30FPS_SUPPORTED, false)){
            itemsSupported.add(numItemSupported++,mContext.getResources().getString(R.string.high_frame_rate_name));
        }

        final String[] items = new String[numItemSupported];

        for (int i=0; i<numItemSupported; i++){
            items[i] = itemsSupported.get(i);
            if ( itemsSupported.get(i).compareTo(prefsFrameRate) == 0){
                positionItemSelected = i;
            }
        }

        builder.setSingleChoiceItems(items, positionItemSelected,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setValue(items[which]);
                        updateProfileProject(items[which]);
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

    private void updateProfileProject(String item) {

        VideoFrameRate.FrameRate frameRate = Utils.getFrameRateFromItemName(mContext, item);
        updateVideoFrameRateToProjectUseCase.updateFrameRate(frameRate);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

    }

}
