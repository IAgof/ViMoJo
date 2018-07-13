package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.composition.domain.usecase.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SaveComposition;
import com.videonasocialmedia.vimojo.galleryprojects.domain.CheckIfProjectHasBeenExportedUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DeleteProjectUseCase;
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

import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class GalleryProjectListPresenter extends VimojoPresenter {

  private ProjectRepository projectRepository;
  private GalleryProjectListView galleryProjectListView;
  private SharedPreferences sharedPreferences;
  private DuplicateProjectUseCase duplicateProjectUseCase;
  private DeleteProjectUseCase deleteProjectUseCase;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  private CheckIfProjectHasBeenExportedUseCase checkIfProjectHasBeenExportedUseCaseUseCase;
  private ProjectInstanceCache projectInstanceCache;
  private SaveComposition saveComposition;

  @Inject
  public GalleryProjectListPresenter(
          GalleryProjectListView galleryProjectListView, SharedPreferences sharedPreferences,
          ProjectRepository projectRepository,
          CreateDefaultProjectUseCase createDefaultProjectUseCase,
          DuplicateProjectUseCase duplicateProjectUseCase,
          DeleteProjectUseCase deleteProjectUseCase,
          CheckIfProjectHasBeenExportedUseCase checkIfProjectHasBeenExportedUseCase,
          ProjectInstanceCache projectInstanceCache,
          SaveComposition saveComposition) {
    this.galleryProjectListView = galleryProjectListView;
    this.sharedPreferences = sharedPreferences;
    this.projectRepository = projectRepository;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.duplicateProjectUseCase = duplicateProjectUseCase;
    this.deleteProjectUseCase = deleteProjectUseCase;
    this.checkIfProjectHasBeenExportedUseCaseUseCase = checkIfProjectHasBeenExportedUseCase;
    this.projectInstanceCache = projectInstanceCache;
    this.saveComposition = saveComposition;
  }

  public void init() {
    updateProjectList();
  }

  public List<Project> loadProjectList() {
    // TODO(jliarte): 11/07/18 this is a use case!
    return projectRepository.getListProjectsByLastModificationDescending();
  }

  public ListenableFuture<Void> duplicateProject(Project project) throws IllegalItemOnTrack {
    // TODO(jliarte): 11/07/18 move calls to background as they call repos and copy files
    Project newProject = duplicateProjectUseCase.duplicate(project);
    // TODO(jliarte): 11/07/18 change to runnable
    return executeUseCaseCall(() -> {
      saveComposition.saveComposition(newProject);
      return null;
    });
  }

  public void deleteProject(Project project) {
    deleteProjectUseCase.delete(project);
    updateCurrentProjectInstance();
  }

  private void updateCurrentProjectInstance() {
    // TODO(jliarte): 11/07/18 this is a use case!
    Project lastModifiedProject = projectRepository.getLastModifiedProject();
    if (lastModifiedProject != null) {
      projectInstanceCache.setCurrentProject(lastModifiedProject);
    }
  }

  public void updateProjectList() {
    List<Project> projectList = loadProjectList();
    if (projectList != null && projectList.size() > 0) {
      galleryProjectListView.showProjectList(projectList);
    } else {
      galleryProjectListView.createDefaultProject();
    }
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
    projectRepository.update(project);
    galleryProjectListView.navigateTo(EditActivity.class);
  }

  public void goToShare(Project project) {
    projectInstanceCache.setCurrentProject(project);
    projectRepository.update(project);
    galleryProjectListView.navigateTo(ShareActivity.class);
  }

  public void goToDetailProject(Project project) {
    projectInstanceCache.setCurrentProject(project);
    // TODO(jliarte): 20/04/18 don't change current project instance, but pass projectId to load
    // project from @DetailProjectActivity
    galleryProjectListView.navigateTo(DetailProjectActivity.class);
  }

}
