package com.videonasocialmedia.vimojo.export;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by alvaro on 25/10/16.
 */
public interface OnApplyAudioFadeInFadeOutToVideoListener {

  void OnGetAudioFadeInFadeOutError(String message, Video video);

  void OnGetAudioFadeInFadeOutSuccess(Video video);
}
