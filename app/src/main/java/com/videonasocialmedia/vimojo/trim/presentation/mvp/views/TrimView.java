package com.videonasocialmedia.vimojo.trim.presentation.mvp.views;

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

}
