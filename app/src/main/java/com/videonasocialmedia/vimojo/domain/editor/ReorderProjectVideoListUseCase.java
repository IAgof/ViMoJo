package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import java.util.LinkedList;

/**
 * Created by Alejandro on 21/10/16.
 */

public class ReorderProjectVideoListUseCase {
  private ProjectRepository projectRepository = new ProjectRealmRepository();
  public void reorderVideoList() {
    Project project = projectRepository.getCurrentProject();
    Track track = project.getMediaTrack();

    LinkedList<Media> videoList = track.getItems();

    int position = 1;
    for (Media media : videoList) {
      media.setPosition(position);
      position++;
    }
  }
}
