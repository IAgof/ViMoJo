package com.videonasocialmedia.vimojo.split.domain;

/**
 * Created by jliarte on 13/09/18.
 */

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.domain.usecase.UpdateMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;

/**
 * {@link OnSplitVideoListener} implementation that updates composition and splitted video on
 * split success
 */
public class VideoAndCompositionUpdaterOnSplitSuccess implements OnSplitVideoListener {
  private static final String LOG_TAG = VideoAndCompositionUpdaterOnSplitSuccess.class.getSimpleName();
  private UpdateComposition updateComposition;
  private UpdateMedia updateMedia;

  public VideoAndCompositionUpdaterOnSplitSuccess(UpdateComposition updateComposition,
                                                  UpdateMedia updateMedia) {
    this.updateComposition = updateComposition;
    this.updateMedia = updateMedia;
  }

  @Override
  public void onSuccessSplittingVideo(Project currentProject, Video initialVideo, Video endVideo) {
    // TODO(jliarte): 13/09/18 addvideotoproject use case no longer saves video into repo
    updateMedia.update(initialVideo);
    updateMedia.update(endVideo); // TODO(jliarte): 13/09/18 save bay now, without trackId (??? not sure)
    updateComposition.updateComposition(currentProject); // TODO(jliarte): 13/09/18 here finally above created media would be linked with composition
  }

  @Override
  public void showErrorSplittingVideo() {
    // (jliarte): 13/09/18 nothing done, as split view would have been closed
    Log.e(LOG_TAG, "Error splitting video");
    Crashlytics.log("Error splitting video");
  }
}
