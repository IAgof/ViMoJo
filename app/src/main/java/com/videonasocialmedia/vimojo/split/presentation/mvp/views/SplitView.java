package com.videonasocialmedia.vimojo.split.presentation.mvp.views;

import android.content.Context;
import android.widget.RadioButton;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;

/**
 * Created by jca on 8/7/15.
 */
public interface SplitView {
    void initSplitView(int maxSeekBar);
    void showError(int stringResourceId);
    void updateSplitSeekbar(int progress);
    void refreshTimeTag(int currentPosition);
    void updateProject();
    void navigateTo(Class<EditActivity> editActivityClass, int videoIndexOnTrack);
    void updateViewToThemeDark();
    void updateViewToThemeLight();
    void updateRadioButtonToThemeDark(RadioButton radioButton);
    void updateRadioButtonToThemeLight(RadioButton radioButton);

    // Player views
    void attachView(Context context);
    void detachView();
    void setVideonaPlayerListener();
    void setAspectRatioVerticalVideos(int height);
    void initSingleClip(VMComposition vmComposition, int clipPosition);
    void seekTo(int timeInMsec);
}
