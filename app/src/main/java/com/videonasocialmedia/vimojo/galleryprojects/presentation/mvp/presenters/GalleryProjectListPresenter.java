package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.CheckIfProjectHasBeenExportedUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DeleteProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.DuplicateProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.repository.project.ProfileSharedPreferencesRepository;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

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

  public GalleryProjectListPresenter(GalleryProjectListView galleryProjectListView,
                                     CreateDefaultProjectUseCase createDefaultProjectUseCase,
                                     ProjectRepository projectRepository) {
    this.galleryProjectListView = galleryProjectListView;
    this.createDefaultProjectUseCase = createDefaultProjectUseCase;
    this.projectRepository = projectRepository;
    updateCurrentProjectUseCase = new UpdateCurrentProjectUseCase();
    duplicateProjectUseCase = new DuplicateProjectUseCase();
    deleteProjectUseCase = new DeleteProjectUseCase();
    checkIfProjectHasBeenExportedUseCaseUseCase = new CheckIfProjectHasBeenExportedUseCase();
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

  public void createNewDefaultProject() {
    createDefaultProjectUseCase.createProject(Constants.PATH_APP);
  }

  public void updateCurrentProject(Project project) {
    updateCurrentProjectUseCase.updateLastModificationAndProjectInstance(project);
  }

  public void checkNavigationToShare(Project project) {
    updateCurrentProjectUseCase.updateLastModificationAndProjectInstance(project);
    // if video to export has been exported before and is exactly the same, use it.
    checkIfProjectHasBeenExportedUseCaseUseCase.compareDate(project, this);
  }

  @Override
  public void videoExported(String videoPath) {
    galleryProjectListView.navigateTo(ShareActivity.class, videoPath);
  }

  @Override
  public void exportNewVideo() {
    //// TODO:(alvaro.martinez) 20/12/16 Launch export process. Provisional, go to editActivity.
    galleryProjectListView.navigateTo(EditActivity.class);
  }
}
