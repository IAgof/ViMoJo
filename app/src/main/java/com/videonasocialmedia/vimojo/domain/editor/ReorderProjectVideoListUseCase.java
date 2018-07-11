package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;

/**
 * Created by Alejandro on 21/10/16.
 */

public class ReorderProjectVideoListUseCase {
  public void reorderVideoList(Project project) {
    for (Media media : project.getMediaTrack().getItems()) {
      media.setPosition(project.getMediaTrack().getItems().indexOf(media));
    }
  }
}
