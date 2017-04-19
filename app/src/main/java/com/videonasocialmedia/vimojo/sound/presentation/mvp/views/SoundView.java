package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public interface SoundView {
    void bindVideoList(List<Video> movieList);
    void bindVideoTrack(float volume, boolean muteAudio, boolean soloAudio);
    void bindMusicList(List<Music> musicList);
    void bindMusicTrack(float volume, boolean muteAudio, boolean soloAudio, int position);
    void bindVoiceOverList(List<Music> voiceOverList);
    void bindVoiceOverTrack(float volume, boolean muteAudio, boolean soloAudio, int position);
    void hideVoiceOverCardView();
    void addVoiceOverOptionToFab();
    void setVideoFadeTransitionAmongVideos();
    void setAudioFadeTransitionAmongVideos();
    void resetPreview();
}
