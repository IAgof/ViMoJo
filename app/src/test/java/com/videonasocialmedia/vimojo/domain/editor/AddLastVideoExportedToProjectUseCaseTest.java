package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.utils.DateUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 13/12/16.
 */

public class AddLastVideoExportedToProjectUseCaseTest {

  @Mock
  ProjectRealmRepository mockedProjectRepository;
  @InjectMocks
  AddLastVideoExportedToProjectUseCase injectedUseCase;

  Project actualProject;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
    actualProject = getAProject();
  }


  @Test
  public void ifAddLastVideoExportedToProjectLastModificationAndVideoExportedDateAreEqual(){

    String date = DateUtils.getDateRightNow();
    injectedUseCase.addLastVideoExportedToProject("newVideoExported", date);

    Project currentProject = Project.getInstance(null, null, null, null);

    assertThat("Date of last modification and videoExportedNavigateToShareActivity are equal ",
        currentProject.getLastModification(), is(currentProject.getDateLastVideoExported()));
  }

  @Test
  public void testAddLastVideoExportedToProjectUpdateProjectRepository(){

    Project currentProject = Project.getInstance(null, null, null, null);
    String date = DateUtils.getDateRightNow();
    injectedUseCase.addLastVideoExportedToProject("somePath", date);
    verify(mockedProjectRepository).updateWithDate(currentProject, date);

  }

  private Project getAProject() {
    return Project.getInstance("title", "/path", "private/path", Profile
        .getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25));
  }

}
