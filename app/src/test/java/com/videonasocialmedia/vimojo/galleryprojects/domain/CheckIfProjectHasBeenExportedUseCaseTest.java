package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.OnProjectExportedListener;
import com.videonasocialmedia.vimojo.model.entities.editor.LastVideoExported;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by alvaro on 22/12/16.
 */

public class CheckIfProjectHasBeenExportedUseCaseTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @InjectMocks
  CheckIfProjectHasBeenExportedUseCase injectedUseCase;
  @Mock
  OnProjectExportedListener mockedOnProjectExportedListener;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void ifDateOfLastModificationAndVideoExportedAreEqualsCallsExportNewVideo(){

    Project project = getAProjectWithVideoExportedAndSameDates();

    injectedUseCase.compareDate(project, mockedOnProjectExportedListener);

    verify(mockedOnProjectExportedListener, never()).exportProject(project);
    verify(mockedOnProjectExportedListener, times(1)).videoExportedNavigateToShareActivity(project);
  }

  @Test
  public void ifDateOfLastModificationAndVideoExportedAreDifferentCallsVideoExported(){

    Project project = getAProjectWithVideoExportedAndSameDates();
    project.setLastModification("fakeDate");

    injectedUseCase.compareDate(project, mockedOnProjectExportedListener);

    verify(mockedOnProjectExportedListener, times(1)).exportProject(project);
    verify(mockedOnProjectExportedListener, never()).videoExportedNavigateToShareActivity(project);
  }

  private Project getAProjectWithVideoExportedAndSameDates() {

    Project project =
        Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));

    String date = DateUtils.getDateRightNow();
    project.setLastModification(date);

    LastVideoExported lastVideoExported = new LastVideoExported("somePath", date);
    project.setLastVideoExported(lastVideoExported);

    return project;
  }

}