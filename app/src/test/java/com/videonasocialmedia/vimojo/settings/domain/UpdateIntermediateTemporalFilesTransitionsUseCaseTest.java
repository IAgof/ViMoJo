package com.videonasocialmedia.vimojo.settings.domain;


import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.settings.presentation.mvp.views.OnRelaunchTemporalFileListener;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateIntermediateTemporalFilesTransitionsUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @Mock
  VideoRepository mockedVideoRepository;
  @Mock
  OnRelaunchTemporalFileListener mockedOnRelaunchTemporalFileListener;


  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void ifProjectHasTempFileUseCaseCallsVideoToRelaunchListener(){
    Project project = getAProject();
    AddVideoToProjectUseCase addVideoToProjectUseCase = new AddVideoToProjectUseCase(mockedProjectRepository);
    Video videoAdded = new Video("somepath");
    videoAdded.setTempPath("tempDirectory");
    addVideoToProjectUseCase.addVideoToProjectAtPosition(videoAdded, 0);
    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();

    new UpdateIntermediateTemporalFilesTransitionsUseCase(getMediaListFromProjectUseCase).execute(mockedOnRelaunchTemporalFileListener);

    verify(mockedOnRelaunchTemporalFileListener).videoToRelaunch(videoAdded.getIdentifier());
  }

  private Project getAProject() {
    return new Project("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
