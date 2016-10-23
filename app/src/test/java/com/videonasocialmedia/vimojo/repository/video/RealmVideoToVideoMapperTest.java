package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;

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
    RealmVideo realmVideo = new RealmVideo("randomUUID", 0, "media/path", "text",
            VideoEditTextActivity.TextPosition.CENTER.name(), true, true, 2, 10);
    RealmVideoToVideoMapper mapper = new RealmVideoToVideoMapper();

    Video video = mapper.map(realmVideo);

    assertThat(video.getUuid(), is("randomUUID"));
    assertThat(video.getPosition(), is(0));
    assertThat(video.getMediaPath(), is("media/path"));
    assertThat(video.getClipText(), is("text"));
    assertThat(video.getClipTextPosition(), is(VideoEditTextActivity.TextPosition.CENTER.name()));
    assertThat(video.isTextToVideoAdded(), is(true));
    assertThat(video.isTrimmedVideo(), is(true));
    assertThat(video.getStartTime(), is(2));
    assertThat(video.getStopTime(), is(10));
  }
}