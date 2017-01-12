package com.videonasocialmedia.vimojo.settings.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.settings.presentation.mvp.views.OnRelaunchTemporalFileListener;

import java.util.List;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateIntermediateTemporalFilesTransitionsUseCase {

  private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

  public UpdateIntermediateTemporalFilesTransitionsUseCase(GetMediaListFromProjectUseCase getMediaListFromProjectUseCase){
    this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
  }

  public void execute(OnRelaunchTemporalFileListener listener) {
    List<Media> items = getMediaListFromProjectUseCase.getMediaListFromProject();
    for(Media media: items){
      Video video = (Video) media;
      if(video.isEdited()) {
        video.setTempPathFinished(false);
        listener.videoToRelaunch(video.getIdentifier());
      }
    }
  }
}
