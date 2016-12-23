package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateTitleProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.project.RealmProject;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.internal.RealmCore;
import io.realm.internal.log.RealmLog;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;


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
  public void clickEditProjectNavigateToDetailActivity(){

    List<Project> projectList = new ArrayList<>();
    projectList.add(getAProject());
    doReturn(projectList).when(mockedProjectRepository).getListProjects();
    injectedPresenter.updateProjectList();
  }

  @Test
  public void updateCurrentProjectCallsUpdateLastModificationAndProjectInstance(){
    Project project = getAProject();
    injectedPresenter.updateCurrentProject(project);
    verify(mockedUpdateCurrentProjectUseCase).updateLastModificationAndProjectInstance(project);
  }

  @Test
  public void checkNavigationToShareCallsUpdateLastModificationAndProjectInstance(){
    Project project = getAProject();
    injectedPresenter.checkNavigationToShare(project);
    verify(mockedUpdateCurrentProjectUseCase).updateLastModificationAndProjectInstance(project);
  }

  @Test
  public void ifProjectRepositoryHasProjectsUpdateProjectListCallsGalleryProjectListViewShow(){
    List<Project> projectList = new ArrayList<>();
    projectList.add(getAProject());
    doReturn(projectList).when(mockedProjectRepository).getListProjects();
    injectedPresenter.updateProjectList();
    verify(mockedGalleryProjectListView).showProjectList(projectList);
  }

  @Test
  public void ifProjectRepositoryHasNotProjectAfterDeleteCreateNewDefaultProject(){
    List<Project> projectList = new ArrayList<>();
    doReturn(projectList).when(mockedProjectRepository).getListProjects();
    injectedPresenter.updateProjectList();
    verify(mockedGalleryProjectListView).createDefaultProject();
  }

  private Project getAProject() {
    return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}
