package com.videonasocialmedia.vimojo.settings.domain;

import android.content.Context;
import android.content.Intent;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

/**
 * Created by alvaro on 10/01/17.
 */

public class UpdateIntermediateTemporalFilesTransitionsUseCase {

  private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

  public UpdateIntermediateTemporalFilesTransitionsUseCase(GetMediaListFromProjectUseCase getMediaListFromProjectUseCase){
    this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
  }

  public void execute() {
    List<Media> items = getMediaListFromProjectUseCase.getMediaListFromProject();
    for(Media media: items){
      launchExportTempBackgrounService((Video) media);
    }
  }

  private void launchExportTempBackgrounService(Video video) {
    Context appContext = VimojoApplication.getAppContext();
    Intent exportTempBackgroudnServiceIntent = new Intent(appContext, ExportTempBackgroundService.class);
    exportTempBackgroudnServiceIntent.putExtra(IntentConstants.VIDEO_ID, video.getIdentifier());
    exportTempBackgroudnServiceIntent.putExtra(IntentConstants.RELAUNCH_EXPORT_TEMP, true);
    // TODO:(alvaro.martinez) 22/11/16 use project tmp path
    exportTempBackgroudnServiceIntent.putExtra(IntentConstants.VIDEO_TEMP_DIRECTORY, Constants.PATH_APP_TEMP_INTERMEDIATE_FILES);
    appContext.startService(exportTempBackgroudnServiceIntent);
  }
}
