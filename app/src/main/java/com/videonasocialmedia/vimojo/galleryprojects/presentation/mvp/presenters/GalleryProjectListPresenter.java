package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.CheckIfProjectHasBeenExportedUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class GalleryProjectListPresenter implements OnProjectExportedListener {

  private ProjectRepository projectRepository;
  private GalleryProjectListView galleryProjectListView;
  private UpdateCurrentProjectUseCase updateCurrentProjectUseCase;

  private DuplicateProjectUseCase duplicateProjectUseCase;
  private DeleteProjectUseCase deleteProjectUseCase;
  private CreateDefaultProjectUseCase createDefaultProjectUseCase;
  private CheckIfProjectHasBeenExportedUseCase checkIfProjectHasBeenExportedUseCaseUseCase;

  @Inject
  public GalleryProjectListPresenter(GalleryProjectListView galleryProjectListView,
                                     ProjectRepository projectRepository,
                                     CreateDefaultProjectUseCase createDefaultProjectUseCase,
                                     UpdateCurrentProjectUseCase updateCurrentProjectUseCase,
                                     DuplicateProjectUseCase duplicateProjectUseCase,
                                     DeleteProjectUseCase deleteProjectUseCase,
                                     CheckIfProjectHasBeenExportedUseCase
                                         checkIfProjectHasBeenExportedUseCase) {
    this.galleryProjectListView = galleryProjectListView;
    this.projectRepository = projectRepository;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.updateCurrentProjectUseCase = updateCurrentProjectUseCase;
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

  public void createNewDefaultProject(String rootPath, String privatePath,
                                      boolean isWatermarkFeatured) {
    createDefaultProjectUseCase.createProject(rootPath, privatePath, isWatermarkFeatured);
  }

  public void updateCurrentProject(Project project) {
    updateCurrentProjectUseCase.updateLastModificationAndProjectInstance(project);
  }

  public void checkNavigationToShare(Project project) {
    // if video to export has been exported before and is exactly the same, use it.
    checkIfProjectHasBeenExportedUseCaseUseCase.compareDate(project, this);
  }

  @Override
  public void videoExportedNavigateToShareActivity(Project project) {
    updateCurrentProjectUseCase.updateLastModificationAndProjectInstance(project);
    galleryProjectListView.navigateTo(ShareActivity.class, project.getPathLastVideoExported());
  }

  @Override
  public void exportProject(Project project) {
    updateCurrentProjectUseCase.updateLastModificationAndProjectInstance(project);
    //// TODO:(alvaro.martinez) 20/12/16 Launch export process. Provisional, go to editActivity.
    galleryProjectListView.navigateTo(EditActivity.class);
  }
}
