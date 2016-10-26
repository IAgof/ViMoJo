package com.videonasocialmedia.vimojo.export;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

/**
 * Created by alvaro on 25/10/16.
 */
public interface OnApplyAudioFadeInFadeOutToVideoListener {

  void OnGetAudioFadeInFadeOutError(String message, Video video, int videoId);

  void OnGetAudioFadeInFadeOutSuccess(Video video, int videoId);
}
