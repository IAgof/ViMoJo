package com.videonasocialmedia.vimojo.trim.presentation.mvp.views;

import android.content.Context;
import android.widget.RadioButton;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;

/**
 * Created by jca on 8/7/15.
 */
public interface TrimView {
    void showTrimBar(int videoDuration);
    void refreshDurationTag(int duration);
    void updateStartTrimmingRangeSeekBar(float startValue);
    void updateFinishTrimmingRangeSeekBar(float finishValue);
    void updateProject();
    void updateViewToThemeDark();
    void updateViewToThemeLight();
    void updateRadioButtonToThemeDark(RadioButton buttonNoSelected);
    void updateRadioButtonToThemeLight(RadioButton buttonNoSelected);
    void navigateTo(Class<EditActivity> editActivityClass, int videoIndexOnTrack);

    // Player views
    void attachView(Context context);
    void detachView();
    void setAspectRatioVerticalVideos(int height);
    void initSingleClip(VMComposition vmComposition, int clipPosition);
    void pausePreview();
    void seekTo(int timeInMsec);
    void setVideonaPlayerListener();
}
