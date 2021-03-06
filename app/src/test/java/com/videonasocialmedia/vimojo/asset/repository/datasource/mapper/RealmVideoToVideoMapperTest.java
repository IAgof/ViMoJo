package com.videonasocialmedia.vimojo.asset.repository.datasource.mapper;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.vimojo.asset.repository.datasource.RealmVideo;
import com.videonasocialmedia.vimojo.utils.Constants;

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
    RealmVideo realmVideo = new RealmVideo("randomUUID", 0, "media/path", 1f, "temp/path", "text",
            TextEffect.TextPosition.CENTER.name(), false, true, true, 2, 10,
        Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name(), true);
    RealmVideoToVideoMapper mapper = new RealmVideoToVideoMapper();

    Video video = mapper.map(realmVideo);

    assertThat(video.getUuid(), is("randomUUID"));
    assertThat(video.getPosition(), is(0));
    assertThat(video.getMediaPath(), is("media/path"));
    assertThat(video.getVolume(), is(1f));
    assertThat(video.getTempPath(), is("temp/path"));
    assertThat(video.getClipText(), is("text"));
    assertThat(video.hasClipTextShadow(), is(false));
    assertThat(video.getClipTextPosition(), is(TextEffect.TextPosition.CENTER.name()));
    assertThat(video.hasText(), is(true));
    assertThat(video.isTrimmedVideo(), is(true));
    assertThat(video.getStartTime(), is(2));
    assertThat(video.getStopTime(), is(10));
    assertThat(video.getVideoError(), is(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name()));
  }
}