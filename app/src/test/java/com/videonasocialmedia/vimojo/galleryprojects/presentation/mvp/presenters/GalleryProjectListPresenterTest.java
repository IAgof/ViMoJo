package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.asset.domain.usecase.GetCompositionAssets;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.DeleteComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.GetCompositions;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * Created by alvaro on 22/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GalleryProjectListPresenterTest {

  @Mock ProjectRepository mockedProjectRepository;
  @Mock ProjectInstanceCache mockedProjectInstanceCache;
  @Mock GalleryProjectListView mockedGalleryProjectListView;
  @Mock UpdateComposition mockedUpdateComposition;
  @Mock GetCompositionAssets mockedGetCompositionAssets;
  @Mock GetCompositions mockedGetCompositions;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock CreateDefaultProjectUseCase mockedCreateDefaultUseCase;
  @Mock DuplicateProjectUseCase mockedDuplicateProjectUseCase;
  @Mock DeleteComposition mockedDeleteComposition;
  @Mock SaveComposition mockedSaveComposition;
  private Project currentProject;
  private boolean amIAVerticalApp;
  private boolean watermarkIsForced;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @Test
  public void ifProjectRepositoryHasProjectsUpdateProjectListCallsGalleryProjectListViewShow() {
    List<Project> projectList = new ArrayList<>();
    projectList.add(currentProject);
    doReturn(projectList).when(mockedGetCompositions)
            .getListProjectsByLastModificationDescending();
    GalleryProjectListPresenter spyGalleryProjectListPresenter =
        Mockito.spy(getGalleryProjectListPresenter());

    spyGalleryProjectListPresenter.updateProjectList();

    verify(mockedGalleryProjectListView).showProjectList(projectList);
  }

  @Test
  public void ifProjectRepositoryHasNotProjectAfterDeleteCreateNewDefaultProject() {
    List<Project> projectList = new ArrayList<>();
    doReturn(projectList).when(mockedGetCompositions)
            .getListProjectsByLastModificationDescending();
    GalleryProjectListPresenter spyGalleryProjectListPresenter =
        Mockito.spy(getGalleryProjectListPresenter());

    spyGalleryProjectListPresenter.updateProjectList();

    verify(mockedGalleryProjectListView).createDefaultProject();
  }

  @Test
  public void goToEditUpdateRepositoryAndNavigate() {
    doAnswer(invocation -> {
      GetCompositionAssets.UpdateAssetFilesListener listener = invocation.getArgument(1);
      listener.onCompletion();
      return null;
    }).when(mockedGetCompositionAssets).updateAssetFiles(any(Project.class),
        any(GetCompositionAssets.UpdateAssetFilesListener.class));
    GalleryProjectListPresenter galleryProjectListPresenter = getGalleryProjectListPresenter();

    galleryProjectListPresenter.goToEdit(currentProject);

    verify(mockedUpdateComposition).updateComposition(currentProject);
    verify(mockedGalleryProjectListView).showUpdateAssetsProgressDialog();
    verify(mockedGalleryProjectListView).navigateTo(EditActivity.class);
  }

  @Test
  public void goToShareUpdateRepositoryAndNavigate() {
    doAnswer(invocation -> {
      GetCompositionAssets.UpdateAssetFilesListener listener = invocation.getArgument(1);
      listener.onCompletion();
      return null;
    }).when(mockedGetCompositionAssets).updateAssetFiles(any(Project.class),
        any(GetCompositionAssets.UpdateAssetFilesListener.class));
    GalleryProjectListPresenter galleryProjectListPresenter = getGalleryProjectListPresenter();
    galleryProjectListPresenter.updateProjectList();

    galleryProjectListPresenter.goToShare(currentProject);

    verify(mockedUpdateComposition).updateComposition(currentProject);
    verify(mockedGalleryProjectListView).navigateTo(ShareActivity.class);
  }

  @Test
  public void goToDetailsProjectUpdateRepositoryAndNavigate() {
    GalleryProjectListPresenter galleryProjectListPresenter = getGalleryProjectListPresenter();
    galleryProjectListPresenter.updateProjectList();

    galleryProjectListPresenter.goToDetailProject(currentProject);

    verify(mockedGalleryProjectListView).navigateTo(DetailProjectActivity.class);
  }

  private void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }

  private GalleryProjectListPresenter getGalleryProjectListPresenter() {
    return new GalleryProjectListPresenter(mockedGalleryProjectListView, mockedSharedPreferences,
        mockedProjectRepository, mockedCreateDefaultUseCase, mockedDuplicateProjectUseCase,
        mockedDeleteComposition, mockedProjectInstanceCache, mockedSaveComposition,
        mockedUpdateComposition, mockedGetCompositions, mockedGetCompositionAssets,
        watermarkIsForced, amIAVerticalApp);
  }
}
