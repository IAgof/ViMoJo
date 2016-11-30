package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateDefaultProjectUseCaseTest {
  @Mock ProjectRepository mockedProjectRepository;
  @InjectMocks
  CreateDefaultProjectUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setupProjectInstance() {
    if (Project.INSTANCE != null) {
      Project.INSTANCE.clear();
    }
  }

//  @Test
//  public void loadOrCreateProjectCallsGetCurrentProjectIfInstanceIsNull() {
//    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.EXCELLENT,
//            -1, Profile.ProfileType.pro);
//    Project currentProject = new Project("title", "root/path", profile);
//    assert Project.INSTANCE == null;
//    doReturn(currentProject).when(mockedProjectRepository).getCurrentProject();
//
//    injectedUseCase.loadOrCreateProject("root/path");
//
//    verify(mockedProjectRepository).getCurrentProject();
//    assertThat(Project.INSTANCE, is(currentProject));
//    verify(mockedProjectRepository).update(currentProject);
//  }

  @Test
  public void loadOrCreateProjectCallsGetCurrentProjectIfInstanceIsNull() {
    assert Project.INSTANCE == null;
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    injectedUseCase.loadOrCreateProject("root/path", profile);

    verify(mockedProjectRepository).getCurrentProject();
  }

  @Test
  public void startLoadingProjectDoesNotCallGetCurrentProjectIfNonNullInstance() {
    Project project = Project.INSTANCE = new Project(null, null, null);

    injectedUseCase.loadOrCreateProject("root/path", project.getProfile());

    verify(mockedProjectRepository, never()).getCurrentProject();
    assertThat(Project.getInstance(null, null, null), is(project));
  }

  @Test
  public void startLoadingProjectSetsProjectInstanceToCurrentProjectRetrieved() {
    assert Project.INSTANCE == null;
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    Project currentProject = new Project("current project", "current/path", profile);
    doReturn(currentProject).when(mockedProjectRepository).getCurrentProject();

    injectedUseCase.loadOrCreateProject("root/path", profile);

    assertThat(Project.getInstance(null, null, null), is(currentProject));
  }

  @Test
  public void startLoadingProjectSaveOrUpdateProjectInstance() {
    assert Project.INSTANCE == null;
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    Project currentProject = new Project("current project", "current/path", profile);
    doReturn(currentProject).when(mockedProjectRepository).getCurrentProject();

    injectedUseCase.loadOrCreateProject("root/path", profile);

    verify(mockedProjectRepository).update(currentProject);
  }

}