package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import io.realm.RealmObject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RealmVideoTest {
  @Test
  public void testRealmVideoExtendsRealmObject() {
    RealmVideo realmVideo = new RealmVideo();

    assertThat(realmVideo, CoreMatchers.instanceOf(RealmObject.class));
  }

  @Test
  public void testRealmVideoFields() {
    RealmVideo realmVideo = new RealmVideo();
//    realmVideo.VIDEO_FOLDER_PATH = "video/forder";
//    realmVideo.fileDuration = 10;
    realmVideo.tempPath = "tmp/path";
    realmVideo.isTempPathFinished = true;
    realmVideo.clipText = "text";
    realmVideo.clipTextPosition = TextEffect.TextPosition.CENTER.name();
    realmVideo.isTextToVideoAdded = false;
    realmVideo.isTrimmedVideo = false;
    realmVideo.startTime = 1;
    realmVideo.stopTime = 9;

//    assertThat(realmVideo.VIDEO_FOLDER_PATH, is("video/forder"));
//    assertThat(realmVideo.fileDuration, is(10));
    assertThat(realmVideo.tempPath, is("tmp/path"));
    assertThat(realmVideo.isTempPathFinished, is(true));
    assertThat(realmVideo.clipText, is("text"));
    assertThat(realmVideo.clipTextPosition, is(TextEffect.TextPosition.CENTER.name()));
    assertThat(realmVideo.isTextToVideoAdded, is(false));
    assertThat(realmVideo.isTrimmedVideo, is(false));
    assertThat(realmVideo.startTime, is(1));
    assertThat(realmVideo.stopTime, is(9));
  }

  @Test
  public void testRealmVideoHasIdentifierPrimaryKey() {
    RealmVideo realmVideo = new RealmVideo();
    realmVideo.uuid = "randomUUID";

    assertThat(realmVideo.uuid, is("randomUUID"));
  }

  @Test
  public void testRealmVideoHasPosition() {
    RealmVideo realmVideo = new RealmVideo();
    realmVideo.position = 0;

    assertThat(realmVideo.position, is(0));
  }
}