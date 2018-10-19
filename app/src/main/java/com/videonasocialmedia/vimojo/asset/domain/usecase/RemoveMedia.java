package com.videonasocialmedia.vimojo.asset.domain.usecase;

/**
 * Created by jliarte on 18/07/18.
 */

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Use Case for removing {@link Media} from repository.
 */
public class RemoveMedia {
  private MediaRepository mediaRepository;

  @Inject
  public RemoveMedia(MediaRepository mediaRepository) {
    this.mediaRepository = mediaRepository;
  }

  // TODO(jliarte): 18/07/18 return medias removed?
  public void removeMedias(List<Media> mediaList) {
    for (Media media : mediaList) {
      mediaRepository.remove(media);
    }
  }
}
