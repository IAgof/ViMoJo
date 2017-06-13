package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;

import java.util.LinkedList;

/**
 * Created by Alejandro on 21/10/16.
 */

public class ReorderProjectVideoListUseCase {
  public void reorderVideoList() {
    Project project = getCurrentProject();

    for (Media media : project.getMediaTrack().getItems()) {
      media.setPosition(project.getMediaTrack().getItems().indexOf(media));
    }
  }

  private Project getCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }
}
