package com.videonasocialmedia.videonamediaframework.model.media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class VideoTest {
  @Test
  public void testVideoHasUUID() {
    Video video = new Video("media/path");

    assertThat(video.getUuid(), notNullValue());
  }
}