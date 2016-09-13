package com.videonasocialmedia.vimojo.text.presentation.mvp.views;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by ruth on 1/09/16.
 */
public interface EditTextView {

    //void showTrimBar(int videoStartTime, int videoStopTime, int videoFileDuration);

    void playPreview();

    void pausePreview();

    void seekTo(int timeInMsec);

    void showPreview(List<Video> movieList);

    void showError(String message);

    void showText(Drawable drawable);

    void initTextKeyboard(String text, String position);

}