package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public interface SoundView {
    void bindVideoList(List<Video> movieList);
    void updateVideoList(List<Video> videoList);
    void bindMusicList(List<Music> musicList);
    void bindVoiceOverList(List<Music> voiceOverList);
    void hideVoiceOverCardView();
    void addVoiceOverOptionToFab();
    void setVideoFadeTransitionAmongVideos();
    void setAudioFadeTransitionAmongVideos();
    void resetPreview();
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
