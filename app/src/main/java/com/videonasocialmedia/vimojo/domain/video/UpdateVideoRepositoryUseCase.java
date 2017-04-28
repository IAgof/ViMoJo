package com.videonasocialmedia.vimojo.domain.video;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

/**
 * Created by alvaro on 22/02/17.
 */

public class UpdateVideoRepositoryUseCase {

  private final VideoRepository videoRepository;

  public UpdateVideoRepositoryUseCase(VideoRepository videoRepository){
    this.videoRepository = videoRepository;
  }

  public void updateVideo(Video video){
    videoRepository.update(video);
  }

  public void succesTranscodingVideo(Video video){
    video.resetNumTriesToExportVideo();
    video.setTranscodingTempFileFinished(true);
    videoRepository.update(video);
  }

  public void errorTranscodingVideo(Video video, String message){
    video.setVideoError(message);
    video.setTranscodingTempFileFinished(true);
    videoRepository.update(video);
  }
}
