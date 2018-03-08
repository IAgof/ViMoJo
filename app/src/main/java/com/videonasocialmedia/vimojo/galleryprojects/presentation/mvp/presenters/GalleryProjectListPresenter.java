package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.CheckIfProjectHasBeenExportedUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;


import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class GalleryProjectListPresenter {

  private ProjectRepository projectRepository;
  private GalleryProjectListView galleryProjectListView;
  private SharedPreferences sharedPreferences;
  private DuplicateProjectUseCase duplicateProjectUseCase;
  private DeleteProjectUseCase deleteProjectUseCase;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  private CheckIfProjectHasBeenExportedUseCase checkIfProjectHasBeenExportedUseCaseUseCase;

  @Inject
  public GalleryProjectListPresenter(GalleryProjectListView galleryProjectListView,
                                     SharedPreferences sharedPreferences,
                                     ProjectRepository projectRepository,
                                     CreateDefaultProjectUseCase createDefaultProjectUseCase,
                                     DuplicateProjectUseCase duplicateProjectUseCase,
                                     DeleteProjectUseCase deleteProjectUseCase,
                                     CheckIfProjectHasBeenExportedUseCase
                                         checkIfProjectHasBeenExportedUseCase) {
    this.galleryProjectListView = galleryProjectListView;
    this.sharedPreferences = sharedPreferences;
    this.projectRepository = projectRepository;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.duplicateProjectUseCase = duplicateProjectUseCase;
    this.deleteProjectUseCase = deleteProjectUseCase;
    checkIfProjectHasBeenExportedUseCaseUseCase = checkIfProjectHasBeenExportedUseCase;
  }

  public void init() {
    updateProjectList();
  }

  public List<Project> loadListProjects() {
    return projectRepository.getListProjectsByLastModificationDescending();
  }

  public void duplicateProject(Project project) throws IllegalItemOnTrack {
    duplicateProjectUseCase.duplicate(project);
  }

  public void deleteProject(Project project) {
    deleteProjectUseCase.delete(project);
  }

  public void updateProjectList() {
    List<Project> projectList = loadListProjects();
    if (projectList != null && projectList.size() > 0) {
      galleryProjectListView.showProjectList(projectList);
    } else {
      galleryProjectListView.createDefaultProject();
    }
  }

  public void createNewDefaultProject(String rootPath, String privatePath) {
    createDefaultProjectUseCase.createProject(rootPath, privatePath, isWatermarkActivated());
  }

  private boolean isWatermarkActivated() {
    if(BuildConfig.FEATURE_FORCE_WATERMARK) {
      return true;
    }
    return sharedPreferences.getBoolean(ConfigPreferences.WATERMARK, false);
  }

  public void goToEdit(Project project) {
    project.clear();
    projectRepository.update(project);
    galleryProjectListView.navigateTo(EditActivity.class);
  }

  public void goToShare(Project project) {
    project.clear();
    projectRepository.update(project);
    galleryProjectListView.navigateTo(ShareActivity.class);
  }


  public void goToDetailProject(Project project) {
    project.clear();
    projectRepository.update(project);
    galleryProjectListView.navigateTo(DetailProjectActivity.class);
  }

  public void updateTitleCurrentProject(Project project, String projectTitle) {
    ProjectInfo projectInfo = project.getProjectInfo();
    projectInfo.setTitle(projectTitle);
    projectRepository.setProjectInfo(project, projectInfo.getTitle(), projectInfo.getDescription(),
        projectInfo.getProductTypeList());
    updateProjectList();
  }
}
