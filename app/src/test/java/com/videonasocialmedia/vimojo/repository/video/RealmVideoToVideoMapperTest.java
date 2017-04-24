package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RealmVideoToVideoMapperTest {
  @Test
  public void testMapReturnsVideoObject() {
    RealmVideo realmVideo = new RealmVideo();
    RealmVideoToVideoMapper mapper = new RealmVideoToVideoMapper();

    Video video = mapper.map(realmVideo);

    assertThat(video, notNullValue());
  }

  @Test
  public void testMapReturnsVideoWithFieldsMapped() {
    RealmVideo realmVideo = new RealmVideo("randomUUID", 0, "media/path", "temp/path", true, "text",
            TextEffect.TextPosition.CENTER.name(), true, true, 2, 10);
    RealmVideoToVideoMapper mapper = new RealmVideoToVideoMapper();

    Video video = mapper.map(realmVideo);

    assertThat(video.getUuid(), is("randomUUID"));
    assertThat(video.getPosition(), is(0));
    assertThat(video.getMediaPath(), is("media/path"));
    assertThat(video.getTempPath(), is("temp/path"));
    assertThat(video.outputVideoIsFinished(), is(true));
    assertThat(video.getClipText(), is("text"));
    assertThat(video.getClipTextPosition(), is(TextEffect.TextPosition.CENTER.name()));
    assertThat(video.hasText(), is(true));
    assertThat(video.isTrimmedVideo(), is(true));
    assertThat(video.getStartTime(), is(2));
    assertThat(video.getStopTime(), is(10));
  }
}