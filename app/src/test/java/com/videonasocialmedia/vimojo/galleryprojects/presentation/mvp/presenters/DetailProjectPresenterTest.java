package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;


import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 22/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class DetailProjectPresenterTest {

  @Mock ProjectRepository mockedProjectRepo;
  @Mock DetailProjectView mockedDetailProjectView;
  @Mock UserEventTracker mockedUserEventTracker;
  @Mock Context mockedContext;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  private Project currentProject;

  @Before
  public void initDoubles() {
    MockitoAnnotations.initMocks(this);
    getAProject();
    when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
  }

  @Test
  public void initPresenterCallsProjectTitleDescriptionProductTypesAndDetailsInfo() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    currentProject.setProfile(compositionProfile);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject.setProjectInfo(projectInfo);
    DetailProjectPresenter presenter = getDetailProjectPresenter();

    presenter.init();

    verify(mockedDetailProjectView).showTitleProject("title");
    verify(mockedDetailProjectView).showDescriptionProject("description");
    verify(mockedDetailProjectView).showProductTypeSelected(productType);
    verify(mockedDetailProjectView).showDetailProjectInfo(0,0,1280,50,25);
  }

  @Test
  public void setProjectInfoCallsProjectRepository() {

    DetailProjectPresenter spyPresenter = Mockito.spy(getDetailProjectPresenter());
    String titleProject = "titleProject";
    String descriptionProject = "descriptionProject";
    List<String> productTypeList = new ArrayList<>();
    spyPresenter.init();

    spyPresenter.setProjectInfo(titleProject, descriptionProject, productTypeList);

    verify(mockedProjectRepo).setProjectInfo(currentProject, titleProject, descriptionProject,
        productTypeList);
    verify(mockedUserEventTracker).trackProjectInfo(currentProject);
  }

  @NonNull
  public DetailProjectPresenter getDetailProjectPresenter() {
    return new DetailProjectPresenter(mockedContext, mockedDetailProjectView, mockedUserEventTracker,
        mockedProjectRepo, mockedProjectInstanceCache);
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path",
        compositionProfile);
  }

}
