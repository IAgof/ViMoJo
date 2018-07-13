package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * Created by alvaro on 22/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GalleryProjectListPresenterTest {

  @Mock
  ProjectRepository mockedProjectRepository;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @InjectMocks GalleryProjectListPresenter injectedPresenter;
  @Mock GalleryProjectListView mockedGalleryProjectListView;

  private Project currentProject;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Test
  public void ifProjectRepositoryHasProjectsUpdateProjectListCallsGalleryProjectListViewShow() {
    List<Project> projectList = new ArrayList<>();
    projectList.add(currentProject);
    doReturn(projectList).when(mockedProjectRepository)
            .getListProjectsByLastModificationDescending();

    injectedPresenter.updateProjectList();

    verify(mockedGalleryProjectListView).showProjectList(projectList);
  }

  @Test
  public void ifProjectRepositoryHasNotProjectAfterDeleteCreateNewDefaultProject() {
    List<Project> projectList = new ArrayList<>();
    doReturn(projectList).when(mockedProjectRepository)
            .getListProjectsByLastModificationDescending();

    injectedPresenter.updateProjectList();

    verify(mockedGalleryProjectListView).createDefaultProject();
  }

  @Test
  public void goToEditUpdateRepositoryAndNavigate() {
    injectedPresenter.updateProjectList();

    injectedPresenter.goToEdit(currentProject);

    verify(mockedProjectRepository).update(currentProject);
    verify(mockedGalleryProjectListView).navigateTo(EditActivity.class);
  }

  @Test
  public void goToShareUpdateRepositoryAndNavigate() {
    injectedPresenter.updateProjectList();

    injectedPresenter.goToShare(currentProject);

    verify(mockedProjectRepository).update(currentProject);
    verify(mockedGalleryProjectListView).navigateTo(ShareActivity.class);
  }

  @Test
  public void goToDetailsProjectUpdateRepositoryAndNavigate() {
    injectedPresenter.updateProjectList();

    injectedPresenter.goToDetailProject(currentProject);

    verify(mockedGalleryProjectListView).navigateTo(DetailProjectActivity.class);
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
