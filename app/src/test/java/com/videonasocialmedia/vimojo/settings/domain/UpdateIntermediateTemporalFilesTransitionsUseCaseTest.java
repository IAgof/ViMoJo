package com.videonasocialmedia.vimojo.settings.domain;


import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.UpdateIntermediateTemporalFilesTransitionsUseCase;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.views.OnRelaunchTemporalFileListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateIntermediateTemporalFilesTransitionsUseCaseTest {
  @Mock ProjectRepository mockedProjectRepository;
  @Mock VideoRepository mockedVideoRepository;
  @Mock OnRelaunchTemporalFileListener mockedOnRelaunchTemporalFileListener;
  @Mock OnAddMediaFinishedListener mockedOnAddMediaFinishedListener;
  @Mock private ApplyAVTransitionsUseCase mockedApplyAVTransitionUseCase;
  private Project currentProject;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectRepository.getCurrentProject()).thenReturn(currentProject);
  }

  @Test
  public void ifProjectHasVideosCallsVideoToRelaunchListener() {
    AddVideoToProjectUseCase addVideoToProjectUseCase =
            new AddVideoToProjectUseCase(mockedProjectRepository, mockedApplyAVTransitionUseCase);
    Video videoAdded = new Video("somepath", 1f);
    videoAdded.setTempPath("tempDirectory");
    addVideoToProjectUseCase.addVideoToProjectAtPosition(currentProject, videoAdded, 0,
        mockedOnAddMediaFinishedListener);
    assert(currentProject.getVMComposition().hasVideos());

    new UpdateIntermediateTemporalFilesTransitionsUseCase()
            .execute(currentProject, mockedOnRelaunchTemporalFileListener);

    verify(mockedOnRelaunchTemporalFileListener).videoToRelaunch(videoAdded.getUuid(),
            currentProject.getProjectPathIntermediateFileAudioFade());
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
