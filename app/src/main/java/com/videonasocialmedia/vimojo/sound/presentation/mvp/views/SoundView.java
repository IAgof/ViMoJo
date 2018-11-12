package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicDetailActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.MusicListActivity;

/**
 * Created by ruth on 13/09/16.
 */
public interface SoundView {

    void hideVoiceOverTrack();

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

    void updatePlayer();

    void navigateToMusicDetail(Class<MusicDetailActivity> musicDetailActivityClass,
                               String mediaPath);

    void navigateToMusicList(Class<MusicListActivity> musicListActivityClass);
}
