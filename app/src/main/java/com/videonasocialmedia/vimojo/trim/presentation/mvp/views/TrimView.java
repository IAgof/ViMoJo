package com.videonasocialmedia.vimojo.trim.presentation.mvp.views;

import android.widget.RadioButton;

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

}
