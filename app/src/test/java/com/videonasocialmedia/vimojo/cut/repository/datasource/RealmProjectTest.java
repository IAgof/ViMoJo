package com.videonasocialmedia.vimojo.cut.repository.datasource;


import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import io.realm.RealmObject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by jliarte on 20/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RealmProjectTest {
  @Test
  public void testProjectExtendsRealmObject() {
    RealmProject realmProject = new RealmProject();

    assertThat(realmProject, IsInstanceOf.instanceOf(RealmObject.class));
  }

  @Test
  public void testProjectFields() {
    RealmProject realmProject = new RealmProject();
    realmProject.title = "Project title";
    realmProject.projectPath = "root/path";
    realmProject.resolution = VideoResolution.Resolution.HD720.name();
    realmProject.quality = VideoQuality.Quality.GOOD.name();
    realmProject.uuid = "123456";
    realmProject.lastModification = "Date";
    realmProject.duration = 10;

    assertThat(realmProject.title, is("Project title"));
    assertThat(realmProject.projectPath, is("root/path"));
    assertThat(realmProject.resolution, is(VideoResolution.Resolution.HD720.name()));
    assertThat(realmProject.quality, is(VideoQuality.Quality.GOOD.name()));
    assertThat(realmProject.uuid, is("123456"));
    assertThat(realmProject.lastModification, is("Date"));
    assertThat(realmProject.duration, is(10));
  }

}