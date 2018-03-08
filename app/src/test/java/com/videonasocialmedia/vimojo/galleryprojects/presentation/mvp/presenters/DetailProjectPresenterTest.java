package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;


import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 22/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class DetailProjectPresenterTest {

  @Mock ProjectRepository mockedProjectRepo;
  @Mock DetailProjectView mockedDetailProjectView;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock Context mockedContext;

  @Before
  public void initDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() throws Exception {
    Project.INSTANCE.clear();
  }

  @Test
  public void initPresenterCallsProjectTitleDescriptionProductTypesAndDetailsInfo() {
    Project project = getAProject();
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    project.setProfile(compositionProfile);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    project.setProjectInfo(projectInfo);
    DetailProjectPresenter presenter = getDetailProjectPresenter();

    presenter.init();

    verify(mockedDetailProjectView).showTitleProject("title");
    verify(mockedDetailProjectView).showDescriptionProject("description");
    verify(mockedDetailProjectView).showProductTypeSelected(productType);
    verify(mockedDetailProjectView).showDetailProjectInfo(0,0,1280,50,25);
  }

  @Test
  public void setProjectInfoCallsProjectRepository() {
    Project project = getAProject();
    DetailProjectPresenter presenter = getDetailProjectPresenter();
    String titleProject = "titleProject";
    String descriptionProject = "descriptionProject";
    List<String> productTypeList = new ArrayList<>();

    presenter.setProjectInfo(titleProject, descriptionProject, productTypeList);

    verify(mockedProjectRepo).setProjectInfo(project, titleProject, descriptionProject,
        productTypeList);
    verify(mockedUserEventTracker).trackProjectInfo(project);
  }

  @NonNull
  public DetailProjectPresenter getDetailProjectPresenter() {
    return new DetailProjectPresenter(mockedContext, mockedDetailProjectView, mockedUserEventTracker,
        mockedProjectRepo);
  }

  private Project getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    return Project.getInstance(projectInfo, "/path", "private/path",
        compositionProfile);
  }

}
