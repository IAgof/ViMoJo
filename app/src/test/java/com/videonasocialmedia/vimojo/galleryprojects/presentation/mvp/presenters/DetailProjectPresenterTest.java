package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;


import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateTitleProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 22/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class DetailProjectPresenterTest {

  @Mock ProjectRepository mockedProjectRepo;
  @Mock UpdateTitleProjectUseCase mockedUseCase;
  @Mock DetailProjectView mockedDetailProjectView;

  @InjectMocks
  DetailProjectPresenter injectedPresenter;

  @Before
  public void initDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() throws Exception {
    // FIXME: tests are not independent as Project keeps state between tests
    Project singletonProject = Project.getInstance(null, null, null, null);
    singletonProject.clear();
  }

  @Test
  public void setTitleProjectCallsUpdateTitleUseCase(){
    String title = "new title";
    Project project = getAProject();
    injectedPresenter.setTitleProject(title);
    verify(mockedUseCase).setTitle(project, title);
  }

  @Test
  public void initPresenterCallsProjectTitleAndInfo(){

    Project project = getAProject();
    Profile profile = Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    project.setProfile(profile);
    injectedPresenter.init();
    verify(mockedDetailProjectView).showTitleProject(null);
    verify(mockedDetailProjectView).showDetailProjectInfo(0,0,1280,50,25);
  }

  private Project getAProject() {
    return Project.getInstance("title", "/path", "private/path",
        Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }

}
