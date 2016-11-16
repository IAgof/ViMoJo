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
    Project project = Project.getInstance(null, null, null);
    Track track = project.getMediaTrack();

    LinkedList<Media> videoList = track.getItems();

    int position = 1;
    for (Media media : videoList) {
      media.setPosition(position);
      position++;
    }
  }
}
