package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.asset.domain.usecase.GetCompositionAssets;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.GetCompositions;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.DeleteComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class GalleryProjectListPresenter extends VimojoPresenter {
  private static final String LOG_TAG = GalleryProjectListPresenter.class.getSimpleName();
  private ProjectRepository projectRepository;
  private GalleryProjectListView galleryProjectListView;
  private SharedPreferences sharedPreferences;
  private DuplicateProjectUseCase duplicateProjectUseCase;
  private DeleteComposition deleteComposition;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  private ProjectInstanceCache projectInstanceCache;
  private SaveComposition saveComposition;
  private UpdateComposition updateComposition;
  private GetCompositions getCompositions;
  private GetCompositionAssets getCompositionAssets;

  @Inject
  public GalleryProjectListPresenter(
          GalleryProjectListView galleryProjectListView, SharedPreferences sharedPreferences,
          ProjectRepository projectRepository,
          CreateDefaultProjectUseCase createDefaultProjectUseCase,
          DuplicateProjectUseCase duplicateProjectUseCase,
          DeleteComposition deleteComposition, ProjectInstanceCache projectInstanceCache,
          SaveComposition saveComposition, UpdateComposition updateComposition,
          GetCompositions getCompositions, GetCompositionAssets getCompositionAssets) {
    this.galleryProjectListView = galleryProjectListView;
    this.sharedPreferences = sharedPreferences;
    this.projectRepository = projectRepository;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.duplicateProjectUseCase = duplicateProjectUseCase;
    this.deleteComposition = deleteComposition;
    this.projectInstanceCache = projectInstanceCache;
    this.saveComposition = saveComposition;
    this.updateComposition = updateComposition;
    this.getCompositions = getCompositions;
    this.getCompositionAssets = getCompositionAssets;
  }

  public void init() {
    updateProjectList();
  }

  public void duplicateProject(Project project) {
    // TODO(jliarte): 11/07/18 move calls to background as they call repos and copy files
    try {
      Project newProject = duplicateProjectUseCase.duplicate(project);
      // TODO(jliarte): 11/07/18 change to runnable
      Futures.addCallback(executeUseCaseCall(() -> {
        saveComposition.saveComposition(newProject);
      }), new FutureCallback<Object>() {
        @Override
        public void onSuccess(@Nullable Object result) {
          updateProjectList();
        }

        @Override
        public void onFailure(Throwable t) {
          t.printStackTrace();
          updateProjectList(); // TODO(jliarte): 13/07/18 needed? presenter.onErrorDuplicating -> show error msg!
        }
      });
    } catch (IllegalItemOnTrack illegalItemOnTrack) {
      Log.d(LOG_TAG, "Error duplicating project");
      illegalItemOnTrack.printStackTrace();
    }
  }

  public void deleteProject(Project project) {
    // TODO(jliarte): 10/08/18 from both
    Futures.addCallback(executeUseCaseCall(() -> deleteComposition.delete(project)), new FutureCallback<Object>() {
      @Override
      public void onSuccess(@Nullable Object result) {
        updateCurrentProjectInstance();
        updateProjectList();
      }

      @Override
      public void onFailure(Throwable t) {
        // TODO(jliarte): 10/08/18 show/track error
        updateProjectList();
      }
    });
  }

  public void deleteLocalProject(Project project) {
    // TODO(jliarte): 10/08/18 only local
    Futures.addCallback(executeUseCaseCall(() -> deleteComposition.deleteOnlyLocal(project)),
            new FutureCallback<Object>() {
      @Override
      public void onSuccess(@Nullable Object result) {
        updateCurrentProjectInstance();
        updateProjectList();
      }

      @Override
      public void onFailure(Throwable t) {
        updateProjectList();
      }
    });
  }


  private void updateCurrentProjectInstance() {
    // TODO(jliarte): 11/07/18 this is a use case!
    Project lastModifiedProject = projectRepository.getLastModifiedProject();
    if (lastModifiedProject != null) {
      projectInstanceCache.setCurrentProject(lastModifiedProject);
    }
  }

  public void updateProjectList() {
    galleryProjectListView.showLoading();
    Futures.addCallback(
            executeUseCaseCall(() -> getCompositions.getListProjectsByLastModificationDescending()),
            new FutureCallback<List<Project>>() {
              @Override
              public void onSuccess(@Nullable List<Project> projectList) {
                galleryProjectListView.hideLoading();
                if (projectList != null && projectList.size() > 0) {
                  galleryProjectListView.showProjectList(projectList);
                } else {
                  galleryProjectListView.createDefaultProject();
                }
              }

              @Override
              public void onFailure(Throwable t) {
                // TODO(jliarte): 7/08/18 review this case
                galleryProjectListView.createDefaultProject();
              }
            });
  }

  public void createNewProject(String rootPath, String privatePath,
                               Drawable drawableFadeTransitionVideo) {
    Project project = createDefaultProjectUseCase.createProject(rootPath, privatePath,
            isWatermarkActivated(), drawableFadeTransitionVideo);
    projectInstanceCache.setCurrentProject(project);
    // TODO(jliarte): 11/07/18 move call to background
    saveComposition.saveComposition(project);
  }

  private boolean isWatermarkActivated() {
    return BuildConfig.FEATURE_FORCE_WATERMARK || sharedPreferences.getBoolean(ConfigPreferences.WATERMARK, false);
  }

  public void goToEdit(Project project) {
    projectInstanceCache.setCurrentProject(project);
    executeUseCaseCall(() -> updateComposition.updateComposition(project));
    galleryProjectListView.showUpdateAssetsProgressDialog();
    BroadcastReceiver completionReceiver = getCompositionAssets.updateAssetFiles(project,
            new GetCompositionAssets.UpdateAssetFilesListener() {
      @Override
      public void onCompletion() {
        galleryProjectListView.hideUpdateAssetsProgressDialog();
        galleryProjectListView.navigateTo(EditActivity.class);
      }

      @Override
      public void onProgress(int remaining) {
        galleryProjectListView.updateUpdateAssetsProgressDialog(remaining);
        Log.d(LOG_TAG, "Progress updating composition assets, remaining " + remaining);
      }
    });
    galleryProjectListView.registerFileUploadReceiver(completionReceiver);
  }

  public void goToShare(Project project) {
    projectInstanceCache.setCurrentProject(project);
    executeUseCaseCall(() -> updateComposition.updateComposition(project));
    galleryProjectListView.showUpdateAssetsProgressDialog();
    BroadcastReceiver completionReceiver = getCompositionAssets.updateAssetFiles(project,
            new GetCompositionAssets.UpdateAssetFilesListener() {
              @Override
              public void onCompletion() {
                galleryProjectListView.hideUpdateAssetsProgressDialog();
                galleryProjectListView.navigateTo(ShareActivity.class);
              }

              @Override
              public void onProgress(int remaining) {
                galleryProjectListView.updateUpdateAssetsProgressDialog(remaining);
                Log.d(LOG_TAG, "Progress updating composition assets, remaining " + remaining);
              }
            });
    galleryProjectListView.registerFileUploadReceiver(completionReceiver);
  }

  public void goToDetailProject(Project project) {
    projectInstanceCache.setCurrentProject(project);
    // TODO(jliarte): 20/04/18 don't change current project instance, but pass projectId to load
    // project from @DetailProjectActivity
    galleryProjectListView.navigateTo(DetailProjectActivity.class);
  }

}
