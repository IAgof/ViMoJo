package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;

import java.util.List;

public class VideoListErrorCheckerDelegate {
  boolean showWarning = false;

  public VideoListErrorCheckerDelegate() {
  }

  public void checkWarningMessageVideosRetrieved(List<Video> videoList,
                                                 VideoTranscodingErrorNotifier
                                                     videoTranscodingErrorNotifier) {
    String message = "Video ";
    for (Video video : videoList) {
      ListenableFuture transcodingJob = video.getTranscodingTask();
      if ((transcodingJob != null && transcodingJob.isCancelled())
              || ((video.getVideoError() != null && !video.getVideoError().isEmpty()))) {
        // TODO(jliarte): 2/05/17 after retrieving videos from repository transcodingJob will always be null
        showWarning = true;
        if (video.getVideoError() != null) {
          message = message + video.getVideoError();
        }
      }
    }
    if (showWarning) {
      videoTranscodingErrorNotifier.showWarningTempFile();
      videoTranscodingErrorNotifier.setWarningMessageTempFile(message + " failed");
    }
  }
}