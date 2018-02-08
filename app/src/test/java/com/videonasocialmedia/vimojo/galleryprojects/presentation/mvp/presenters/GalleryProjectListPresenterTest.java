package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

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
  @InjectMocks
  GalleryProjectListPresenter injectedPresenter;
  @Mock
  GalleryProjectListView mockedGalleryProjectListView;
  @Mock
  private SharedPreferences mockedSharePreferences;
  @Mock
  UpdateCurrentProjectUseCase mockedUpdateCurrentProjectUseCase;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void updateCurrentProjectCallsUpdateLastModificationAndProjectInstance() {
    Project project = getAProject();
    injectedPresenter.updateCurrentProject(project);
    verify(mockedUpdateCurrentProjectUseCase).updateLastModificationAndProjectInstance(project);
  }

  @Test
  public void ifProjectRepositoryHasProjectsUpdateProjectListCallsGalleryProjectListViewShow() {
    List<Project> projectList = new ArrayList<>();
    projectList.add(getAProject());
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

  private Project getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    return Project.getInstance(projectInfo, "/path", "private/path", compositionProfile);
  }
}
