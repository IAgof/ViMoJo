package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.ArrayList;

/**
 * Created by jliarte on 2/05/17.
 */

public interface VideoTranscodingErrorNotifier {
  void showWarningTempFile(ArrayList<Video> failedVideos);
  void setWarningMessageTempFile(String messageTempFile);
}
