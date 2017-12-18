package com.videonasocialmedia.vimojo.trim.presentation.mvp.views;

import android.widget.RadioButton;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by jca on 8/7/15.
 */
public interface TrimView {

    void showTrimBar(int videoStartTime, int videoStopTime, int videoFileDuration);

    void refreshDurationTag(int duration);

    void refreshStartTimeTag(int startTime);

    void refreshStopTimeTag(int stopTime);

    void playPreview();

    void pausePreview();

    void seekTo(int timeInMsec);

    void showPreview(List<Video> movieList);

    void showText(String text, String position);

    void showError(String message);

    void updateStartTrimmingRangeSeekBar(float minValue);

    void updateFinishTrimmingRangeSeekBar(float maxValue);

    void updateProject();

    void showPlayerAdvanceLow();

    void showPlayerAdvanceMedium();

    void showPlayerAdvanceHigh();


    void updateViewToThemeDark();

    void updateViewToThemeLight();

    void updateButtonNoSelectedToThemeDark(RadioButton buttonNoSelected);

    void updateButtonNoSelectedToThemeLight(RadioButton buttonNoSelected);

    void updateButtonSelected(RadioButton buttonSelected);
}
