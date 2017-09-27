package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.videonamediaframework.model.media.Video;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class VideoRealmRepositoryTest {
  @Test
  public void testVideoRealmRepositoryConstructorSetsMappers() {
    VideoRealmRepository repo = new VideoRealmRepository();

    assertThat(repo.toVideoMapper, notNullValue());
    assertThat(repo.toRealmVideoMapper, notNullValue());
  }

  // TODO(jliarte): 28/07/17 update these tests to test code moved to video repository
  @Test
  public void succesTranscodingVideoResetNumTriesVideoToExport(){
    Video video= new Video("media/path", Video.DEFAULT_VOLUME);
    video.increaseNumTriesToExportVideo();
    Assert.assertThat(video.getNumTriesToExportVideo(), is(1));
    VideoRealmRepository repo = Mockito.spy(new VideoRealmRepository());
    Mockito.doNothing().when(repo).update(any(Video.class));

    repo.setSuccessTranscodingVideo(video);

    Assert.assertThat(video.getNumTriesToExportVideo(), is(0));
  }

  @Test
  public void succesTranscodingVideoSetTrueIsTranscodingTempfileFinished(){
    Video video= new Video("media/path", Video.DEFAULT_VOLUME);
    video.setTranscodingTempFileFinished(false);
    Assert.assertThat(video.isTranscodingTempFileFinished(), is(false));
    VideoRealmRepository repo = Mockito.spy(new VideoRealmRepository());
    Mockito.doNothing().when(repo).update(any(Video.class));

    repo.setSuccessTranscodingVideo(video);

    Assert.assertThat(video.isTranscodingTempFileFinished(), is(true));
  }

  @Test
  public void errorTranscodingVideoSetTrueIsTranscodingTempfileFinished(){
    Video video= new Video("media/path", Video.DEFAULT_VOLUME);
    video.setTranscodingTempFileFinished(false);
    Assert.assertThat(video.isTranscodingTempFileFinished(), is(false));
    VideoRealmRepository repo = Mockito.spy(new VideoRealmRepository());
    Mockito.doNothing().when(repo).update(any(Video.class));

    repo.setErrorTranscodingVideo(video, "error");

    Assert.assertThat(video.isTranscodingTempFileFinished(), is(true));
  }
}