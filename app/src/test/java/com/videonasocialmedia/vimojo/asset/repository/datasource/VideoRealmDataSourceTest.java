/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.asset.repository.datasource;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoRealmDataSource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class VideoRealmDataSourceTest {
  @Test
  public void testVideoRealmRepositoryConstructorSetsMappers() {
    VideoRealmDataSource repo = new VideoRealmDataSource();

    assertThat(repo.toVideoMapper, notNullValue());
    assertThat(repo.toRealmVideoMapper, notNullValue());
  }

  // TODO(jliarte): 28/07/17 update these tests to test code moved to video repository
//  @Test @Ignore // TODO(jliarte): 10/09/18 method removed
//  public void succesTranscodingVideoResetNumTriesVideoToExport(){
//    Video video= new Video("media/path", Video.DEFAULT_VOLUME);
//    video.increaseNumTriesToExportVideo();
//    Assert.assertThat(video.getNumTriesToExportVideo(), is(1));
//    VideoRealmDataSource repo = Mockito.spy(new VideoRealmDataSource());
//    Mockito.doNothing().when(repo).update(any(Video.class));
//
//    repo.setSuccessTranscodingVideo(video);
//
//    Assert.assertThat(video.getNumTriesToExportVideo(), is(0));
//  }

//  @Test @Ignore // TODO(jliarte): 10/09/18 method removed
//  public void succesTranscodingVideoSetTrueIsTranscodingTempfileFinished(){
//    Video video= new Video("media/path", Video.DEFAULT_VOLUME);
//    video.setTranscodingTempFileFinished(false);
//    Assert.assertThat(video.isTranscodingTempFileFinished(), is(false));
//    VideoRealmDataSource repo = Mockito.spy(new VideoRealmDataSource());
//    Mockito.doNothing().when(repo).update(any(Video.class));
//
//    repo.setSuccessTranscodingVideo(video);
//
//    Assert.assertThat(video.isTranscodingTempFileFinished(), is(true));
//  }

//  @Test @Ignore // TODO(jliarte): 10/09/18 method removed!
//  public void errorTranscodingVideoSetTrueIsTranscodingTempfileFinished(){
//    Video video= new Video("media/path", Video.DEFAULT_VOLUME);
//    video.setTranscodingTempFileFinished(false);
//    Assert.assertThat(video.isTranscodingTempFileFinished(), is(false));
//    VideoRealmDataSource repo = Mockito.spy(new VideoRealmDataSource());
//    Mockito.doNothing().when(repo).update(any(Video.class));
//
//    repo.setErrorTranscodingVideo(video, "error");
//
//    Assert.assertThat(video.isTranscodingTempFileFinished(), is(true));
//  }
}