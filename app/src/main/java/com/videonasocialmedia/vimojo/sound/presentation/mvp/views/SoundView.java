package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;

/**
 * Created by ruth on 13/09/16.
 */
public interface SoundView {

    void hideVoiceOverCardView();

    void addVoiceOverOptionToFab();

    void setVideoVolume(float volume);

    void setVoiceOverVolume(float volume);

    void setMusicVolume(float volume);

    void bindTrack(Track track);

    void showTrackVideo();

    void showTrackAudioFirst();

    void showTrackAudioSecond();

    void showWarningTempFile();

    void setWarningMessageTempFile(String messageTempFile);

    void updateProject();
}
