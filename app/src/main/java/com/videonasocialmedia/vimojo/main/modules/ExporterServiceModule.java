package com.videonasocialmedia.vimojo.main.modules;

import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jliarte on 13/12/16.
 */
@Module
public class ExporterServiceModule {
  @Provides
  ModifyVideoDurationUseCase provideVideoTrimmer(VideoRepository videoRepository) {
    return new ModifyVideoDurationUseCase(videoRepository);
  }

  @Provides
  ModifyVideoTextAndPositionUseCase provideVideoHeadliner(VideoRepository videoRepository) {
    return new ModifyVideoTextAndPositionUseCase(videoRepository);
  }
}
