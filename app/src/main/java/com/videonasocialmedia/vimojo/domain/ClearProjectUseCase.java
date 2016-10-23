package com.videonasocialmedia.vimojo.domain;

import android.content.Context;
import android.content.SharedPreferences;
import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

/**
 *
 */
public class ClearProjectUseCase {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;

    public void clearProject(Project project){
        sharedPreferences = VimojoApplication.getAppContext().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, 0);
        preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, 0);
        project.clear();
    }
}
