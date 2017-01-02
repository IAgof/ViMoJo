package com.videonasocialmedia.vimojo.galleryprojects.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by alvaro on 14/12/16.
 */

public class DeleteProjectUseCase {

  protected ProjectRepository projectRepository = new ProjectRealmRepository();
  protected VideoRepository videoRepository = new VideoRealmRepository();

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
