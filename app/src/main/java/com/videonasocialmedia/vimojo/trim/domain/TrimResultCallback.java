package com.videonasocialmedia.vimojo.trim.domain;

/**
 * Created by jliarte on 13/09/18.
 */

import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.domain.usecase.UpdateMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.utils.Constants;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Callback class for handling {@link ModifyVideoDurationUseCase#trimVideo(Video, int, int, Project)} results.
 */
public class TrimResultCallback implements FutureCallback<Video> {
  private static final String LOG_TAG = TrimResultCallback.class.getSimpleName();
  @Inject UpdateMedia updateMedia;
  @Inject UpdateComposition updateComposition;
  private Project currentProject;
  private Video videoToEdit;

  public TrimResultCallback(Video videoToEdit, Project currentProject) {
    this.currentProject = currentProject;
    this.videoToEdit = videoToEdit;
  }

  @Override
  public void onSuccess(@Nullable Video result) {
    Log.d(LOG_TAG, "Success applying trim - onSuccessTranscoding after trim "
            + result.getTempPath());
    result.resetNumTriesToExportVideo();
    result.setTranscodingTempFileFinished(true);
    result.setVideoError(null);
    updateMedia.update(result);
    updateComposition.updateComposition(currentProject);
  }

  @Override
  public void onFailure(Throwable t) {
    videoToEdit.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());
    videoToEdit.setTranscodingTempFileFinished(true);
    updateMedia.update(videoToEdit);
//    trimView.showError(); // TODO(jliarte): 12/09/18 should show an error here? view probably will not longer be active
  }
}
