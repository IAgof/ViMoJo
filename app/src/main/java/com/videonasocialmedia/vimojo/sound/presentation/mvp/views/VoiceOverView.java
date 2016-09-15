package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.List;

/**
 * Created by ruth on 15/09/16.
 */
public interface VoiceOverView {

    void initVoiceOverView(int startTime, int maxSeekBar);

    void playPreview();

    void pausePreview();

    void showPreview(List<Video> movieList);

    void showError(String message);

    void showText(String text, String position);
}
