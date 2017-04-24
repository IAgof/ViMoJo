package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.util.LinkedList;

import javax.inject.Inject;

/**
 * Created by alvaro on 14/12/16.
 */

public class DeleteProjectUseCase {

  protected ProjectRepository projectRepository;
  protected VideoRepository videoRepository;

  @Inject
  public DeleteProjectUseCase(ProjectRepository projectRepository, VideoRepository videoRepository){
    this.projectRepository = projectRepository;
    this.videoRepository = videoRepository;
  }

  public void delete(Project project){
    MediaTrack mediaTrack = project.getMediaTrack();
    LinkedList<Media> mediaList = mediaTrack.getItems();
    for (Media media : mediaList) {
      videoRepository.remove((Video) media);
    }
    projectRepository.remove(project);

    FileUtils.deleteDirectory(new File(project.getProjectPath()));
  }
}
