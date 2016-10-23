package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class VideoToRealmVideoMapperTest {
  @Test
  public void testMapReturnsARealmVideoInstance() {
    Video video = new Video("media/path");
    VideoToRealmVideoMapper mapper = new VideoToRealmVideoMapper();

    RealmVideo realmVideo = mapper.map(video);

    assertThat(realmVideo, instanceOf(RealmVideo.class));
  }

  @Test
  public void testMapReturnsVideoObjectWithMappedFields() {
    Video video = new Video("media/path");
//    video.fileDuration = 90;
//    video.tempPath = "tmp/path";
    video.setClipText("text");
    video.setClipTextPosition(VideoEditTextActivity.TextPosition.CENTER.name());
    video.setTextToVideoAdded(true);
    video.setTrimmedVideo(true);
    video.setStartTime(10);
    video.setStopTime(80);
//    public RealmProject project;
    VideoToRealmVideoMapper mapper = new VideoToRealmVideoMapper();

    RealmVideo realmVideo = mapper.map(video);

    assertThat(realmVideo.mediaPath, is("media/path"));
    assertThat(realmVideo.uuid, is(video.getUuid()));
//    assertThat(realmVideo.fileDuration, is(90));
//    assertThat(realmVideo.tempPath, is("tmp/path"));
    assertThat(realmVideo.clipText, is("text"));
    assertThat(realmVideo.clipTextPosition, is(VideoEditTextActivity.TextPosition.CENTER.name()));
    assertThat(realmVideo.isTextToVideoAdded, is(true));
    assertThat(realmVideo.isTrimmedVideo, is(true));
    assertThat(realmVideo.startTime, is(10));
    assertThat(realmVideo.stopTime, is(80));
  }
}