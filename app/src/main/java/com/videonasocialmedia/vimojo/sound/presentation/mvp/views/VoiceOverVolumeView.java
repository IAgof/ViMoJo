package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by ruth on 19/09/16.
 */
public interface VoiceOverVolumeView {

    void bindVideoList(List<Video> movieList);
    void setMusic(Music music);
    void resetPreview();
    void goToSoundActivity();
    void setVideoFadeTransitionAmongVideos();
    void setAudioFadeTransitionAmongVideos();
    void showError(String message);
    void muteVideo();
    void muteMusic();

  void setAspectRatioVerticalVideos();
}
