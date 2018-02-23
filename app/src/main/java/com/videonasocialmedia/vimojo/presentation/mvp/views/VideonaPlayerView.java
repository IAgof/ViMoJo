/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by alvaro on 19/2/18.
 *
 * Implements Videona player features.
 *
 */

// TODO: 19/2/18 Implement and set player in app from VMComposition. VMComposition, knowing current project will be able to show and play player.
public interface VideonaPlayerView {

  void bindVideoList(List<Video> movieList);

  void bindMusic(Music music);

  void bindVoiceOver(Music voiceOver);

  void setVideoMute();

  void setVideoVolume(float volume);

  void setVoiceOverVolume(float volume);

  void setMusicVolume(float volume);

  void setVideoFadeTransitionAmongVideos();

  void setAudioFadeTransitionAmongVideos();

  void seekToClip(int clipPosition);

  void pausePreview();

  void updatePreviewTimeLists();

  void initPreviewFromVideo(List<Video> movieList);

}
