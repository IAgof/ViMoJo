package com.videonasocialmedia.vimojo.split.domain;

/**
 * Created by jliarte on 13/09/18.
 */

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;

import javax.inject.Inject;

/**
 * {@link OnSplitVideoListener} implementation that updates composition and splitted video on
 * split success
 */
public class VideoAndCompositionUpdaterOnSplitSuccess implements OnSplitVideoListener {
  private static final String LOG_TAG = VideoAndCompositionUpdaterOnSplitSuccess.class.getSimpleName();
  @Inject UpdateComposition updateComposition;
  @Inject MediaRepository mediaRepository; // TODO(jliarte): 13/09/18 should we use use cases here?
  private Project project;

  public VideoAndCompositionUpdaterOnSplitSuccess(Project project) {
    this.project = project;
  }

  @Override
  public void onSuccessSplittingVideo(Video initialVideo, Video endVideo) {
    // TODO(jliarte): 13/09/18 addvideotoproject use case no longer saves video into repo
    mediaRepository.update(initialVideo);
    mediaRepository.add(endVideo); // TODO(jliarte): 13/09/18 save bay now, without trackId (??? not sure)
    updateComposition.updateComposition(project); // TODO(jliarte): 13/09/18 here finally above created media would be linked with composition
  }

  @Override
  public void showErrorSplittingVideo() {
    // (jliarte): 13/09/18 nothing done, as split view would have been closed
    Log.e(LOG_TAG, "Error splitting video");
//    splitView.showError(R.string.addMediaItemToTrackError);
  }
}
