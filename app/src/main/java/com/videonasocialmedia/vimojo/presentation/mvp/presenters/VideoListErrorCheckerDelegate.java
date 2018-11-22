package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoListErrorCheckerDelegate {

  public VideoListErrorCheckerDelegate() {
  }

  public void checkWarningMessageVideosRetrieved(List<Video> videoList,
                                                 VideoTranscodingErrorNotifier
                                                     videoTranscodingErrorNotifier) {
    String message = "Video ";
    ArrayList<Video> failedVideos = new ArrayList<>();
    for (Video video : videoList) {
      ListenableFuture transcodingJob = video.getTranscodingTask();
      boolean addFailedVideoToList = false;
      if ((transcodingJob != null && transcodingJob.isCancelled())
              || ((video.getVideoError() != null && !video.getVideoError().isEmpty()))) {
        addFailedVideoToList = true;
        // TODO(jliarte): 2/05/17 after retrieving videos from repository transcodingJob will always be null
        if (video.getVideoError() != null) {
          message = message + video.getVideoError();
        }
      }
      // Check temp files path
      if (video.isTranscodingTempFileFinished() && video.getTempPath() != null
          && !(new File(video.getTempPath()).exists())) {
        addFailedVideoToList = true;
      }
      if (addFailedVideoToList) {
        failedVideos.add(video);
      }
    }
    if (failedVideos.size() > 0) {
      videoTranscodingErrorNotifier.showWarningTempFile(failedVideos);
      videoTranscodingErrorNotifier.setWarningMessageTempFile(message + " failed");
    }
  }
}