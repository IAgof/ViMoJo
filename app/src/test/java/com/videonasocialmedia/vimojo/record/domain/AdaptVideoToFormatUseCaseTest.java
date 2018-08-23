package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 5/04/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class AdaptVideoToFormatUseCaseTest {
  @Mock VideoToAdaptRepository videoToAdaptRepository;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock private AdaptVideoToFormatUseCase.AdaptListener mockedAdaptListener;

  @InjectMocks AdaptVideoToFormatUseCase injectedAdaptVideoToFormatUseCase;
  private Project currentProject;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Test
  public void adaptVideoRecordedCallsTranscoderAdaptVideoToFormat() throws IOException {
    String tempDirectory = currentProject.getProjectPathIntermediateAudioMixedFiles();
    injectedAdaptVideoToFormatUseCase.transcoderHelper = mockedTranscoderHelper;
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    String destVideoPath = "dcim/vimojo/masters";
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destVideoPath, 1, 0, 0);
    VideonaFormat videoFormat = new VideonaFormat();
    int rotation = 0;

    injectedAdaptVideoToFormatUseCase.adaptVideo(currentProject, videoToAdapt, videoFormat,
            mockedAdaptListener);

    verify(mockedTranscoderHelper).adaptVideoWithRotationToDefaultFormatAsync(eq(video),
        eq(videoFormat), eq(destVideoPath), eq(rotation),
            any(AdaptVideoToFormatUseCase.AdaptVideoListener.class), eq(tempDirectory));
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.H_720P,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
