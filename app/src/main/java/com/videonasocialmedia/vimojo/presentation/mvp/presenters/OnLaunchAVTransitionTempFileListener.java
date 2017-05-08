package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

/**
 * Created by alvaro on 22/03/17.
 */

public interface OnLaunchAVTransitionTempFileListener {
  void videoToLaunchAVTransitionTempFile(Video video, String intermediatesTempAudioFadeDirectory);
}
