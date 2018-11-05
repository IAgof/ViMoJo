package com.videonasocialmedia.vimojo.text.domain;

/**
 * Created by jliarte on 13/09/18.
 */

import com.google.common.util.concurrent.FutureCallback;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.domain.usecase.UpdateMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;

import javax.annotation.Nullable;

/**
 * Callback class for handling {@link ModifyVideoTextAndPositionUseCase#addTextToVideo(Project, Video, String, String)} results.
 */
public class ClipTextResultCallback implements FutureCallback<Video> {
  private Project currentProject;
  private UpdateMedia updateMedia;
  private UpdateComposition updateComposition;

  public ClipTextResultCallback(Project currentProject, UpdateMedia updateMedia,
                                UpdateComposition updateComposition) {
    this.currentProject = currentProject;
    this.updateMedia = updateMedia;
    this.updateComposition = updateComposition;
  }

  @Override
  public void onSuccess(@Nullable Video result) {
    updateMedia.update(result);
    updateComposition.updateComposition(currentProject);
  }

  @Override
  public void onFailure(Throwable t) {

  }
}
