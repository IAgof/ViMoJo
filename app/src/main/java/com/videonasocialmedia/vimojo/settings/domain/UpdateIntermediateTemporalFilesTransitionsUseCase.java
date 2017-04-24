package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.settings.presentation.mvp.views.OnRelaunchTemporalFileListener;

import java.util.List;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateIntermediateTemporalFilesTransitionsUseCase {

  private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

  public UpdateIntermediateTemporalFilesTransitionsUseCase(GetMediaListFromProjectUseCase
                                                               getMediaListFromProjectUseCase){
    this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
  }

  public void execute(OnRelaunchTemporalFileListener listener) {
    Project project = Project.getInstance(null, null, null);
    List<Media> items = getMediaListFromProjectUseCase.getMediaListFromProject();
    if (items.size() > 0)    {
      for (Media media : items) {
        Video video = (Video) media;
        video.setTempPathFinished(false);
        video.setTempPath(project.getProjectPathIntermediateFiles());
        listener.videoToRelaunch(video.getUuid(), project.getProjectPathIntermediateFileAudioFade());
      }
    }
  }
}
