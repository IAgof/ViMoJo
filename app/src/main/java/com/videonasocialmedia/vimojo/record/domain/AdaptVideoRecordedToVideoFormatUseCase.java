package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import java.io.IOException;

/**
 * Created by alvaro on 3/02/17.
 */

public class AdaptVideoRecordedToVideoFormatUseCase {

  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(mediaTranscoder);

  public AdaptVideoRecordedToVideoFormatUseCase() {
  }

  public void adaptVideo(final Video videoToAdapt, final VideonaFormat videoFormat,
                         final String destVideoPath, int rotation,
                         TranscoderHelperListener listener) throws IOException {
    Project currentProject = getCurrentProject();
    videoToAdapt.setTempPath(currentProject.getProjectPathIntermediateFiles());
    transcoderHelper.adaptVideoWithRotationToDefaultFormatAsync(videoToAdapt, videoFormat,
            destVideoPath, rotation, listener,
            currentProject.getProjectPathIntermediateAudioMixedFiles());
  }

  private Project getCurrentProject() {
    return Project.getInstance(null, null, null);
  }
}
