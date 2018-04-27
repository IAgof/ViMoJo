package com.videonasocialmedia.vimojo.settings.mainSettings.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.settings.mainSettings.presentation.mvp.views.OnRelaunchTemporalFileListener;

import java.util.List;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateIntermediateTemporalFilesTransitionsUseCase {

  public UpdateIntermediateTemporalFilesTransitionsUseCase() {
  }

  public void execute(Project currentProject, OnRelaunchTemporalFileListener listener) {
    List<Media> items = currentProject.getMediaTrack().getItems();
    if (items.size() > 0)    {
      for (Media media : items) {
        Video video = (Video) media;
        video.setTempPath(currentProject.getProjectPathIntermediateFiles());
        listener.videoToRelaunch(video.getUuid(),
            currentProject.getProjectPathIntermediateFileAudioFade());
      }
    }
  }
}
