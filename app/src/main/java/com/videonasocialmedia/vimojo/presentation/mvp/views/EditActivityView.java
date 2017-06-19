/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * @author Juan Javier Cabanas Abascal
 */
public interface EditActivityView {
    void goToShare(String videoToSharePath);
    void showProgressDialog();
    void hideProgressDialog();
    void showError(int causeTextResource);
    void showMessage(int stringToast);
    void bindVideoList(List<Video> movieList);
    void setMusic(Music music);
    void setVoiceOver(Music voiceOver);
    void setVideoFadeTransitionAmongVideos();
    void setAudioFadeTransitionAmongVideos();
    void updateProject();
    void enableEditActions();
    void disableEditActions();
    void enableBottomBar();
    void disableBottomBar();
    void changeAlphaBottomBar(float alpha);
    void expandFabMenu();
    void resetPreview();
    void showDialogMediasNotFound();
    void enableFabText(boolean isEnable);
    void setVideoVolume(float volume);
    void setVideoMute();
    void setVoiceOverVolume(float volume);
    void setMusicVolume(float volume);

}
