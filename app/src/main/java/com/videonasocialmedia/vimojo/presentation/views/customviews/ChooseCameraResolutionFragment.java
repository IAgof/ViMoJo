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
import com.videonasocialmedia.vimojo.domain.editor.UpdateVideoResolutionToProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.util.ArrayList;

class ChooseCameraResolutionListPreferences extends ListPreference {

    private Context mContext;
    private CharSequence[] entries;
    private CharSequence[] entryValues;
    private SharedPreferences sharedPreferences;
    private UpdateVideoResolutionToProjectUseCase updateVideoResolutionToProjectUseCase;

    public ChooseCameraResolutionListPreferences(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        sharedPreferences =  mContext.getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        updateVideoResolutionToProjectUseCase = new UpdateVideoResolutionToProjectUseCase();
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
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder)
    {

        entries = getEntries();
        entryValues = getEntryValues();

        if (entries == null || entryValues == null || entries.length != entryValues.length )
        {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");

        }

        String prefsResolution = sharedPreferences.getString(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION,
                mContext.getResources().getString(R.string.low_resolution_value));

        builder.setTitle(R.string.resolution);

        //list of items

        int positionItemSelected = 0;
        int numItemSupported = 0;

        ArrayList<String> itemsSupported = new ArrayList<String>(mContext.getResources().getStringArray(R.array.camera_resolution_names).length);

        if(sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_720P_SUPPORTED, false)){
            itemsSupported.add(numItemSupported++,mContext.getResources().getString(R.string.low_resolution_name));
        }
        if(sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_1080P_SUPPORTED, false)){
            itemsSupported.add(numItemSupported++,mContext.getResources().getString(R.string.good_resolution_name));
        }
        if(sharedPreferences.getBoolean(ConfigPreferences.BACK_CAMERA_2160P_SUPPORTED, false)){
            itemsSupported.add(numItemSupported++,mContext.getResources().getString(R.string.high_resolution_name));
        }

        final String[] itemsName = new String[numItemSupported];

        for (int i=0; i<numItemSupported; i++){
            itemsName[i] = itemsSupported.get(i);
            if ( itemsSupported.get(i).compareTo(prefsResolution) == 0){
                positionItemSelected = i;
            }
        }

        builder.setSingleChoiceItems(itemsName, positionItemSelected,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setValue(itemsName[which]);
                        updateProfileProject(itemsName[which]);
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

        VideoResolution.Resolution resolution = Utils.getResolutionFromItemName(mContext,item);
        updateVideoResolutionToProjectUseCase.updateResolution(resolution);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

    }
}
