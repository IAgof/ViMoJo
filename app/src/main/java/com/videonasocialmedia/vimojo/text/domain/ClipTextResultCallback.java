package com.videonasocialmedia.vimojo.text.domain;

/**
 * Created by jliarte on 13/09/18.
 */

import com.google.common.util.concurrent.FutureCallback;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.domain.usecase.UpdateMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Callback class for handling {@link ModifyVideoTextAndPositionUseCase#addTextToVideo(Project, Video, String, String)} results.
 */
public class ClipTextResultCallback implements FutureCallback<Video> {
  @Inject UpdateMedia mediaRepository;

  @Override
  public void onSuccess(@Nullable Video result) {
    mediaRepository.update(result);
  }

  @Override
  public void onFailure(Throwable t) {

  }
}
