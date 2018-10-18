package com.videonasocialmedia.vimojo.asset.domain.usecase;

/**
 * Created by jliarte on 12/09/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;

import javax.inject.Inject;

/**
 * Use Case for updating a {@link Media} into repository.
 */
public class UpdateMedia {
  private MediaRepository mediaRepository;

  @Inject
  public UpdateMedia(MediaRepository mediaRepository) {
    this.mediaRepository = mediaRepository;
  }

  public void update(Media media) {
    mediaRepository.update(media);
  }
}
