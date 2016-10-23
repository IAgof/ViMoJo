package com.videonasocialmedia.vimojo.repository.project;


import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;

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
    realmProject.musicTitle = "My cool music";
    realmProject.musicVolume = 0.5f;

    assertThat(realmProject.title, is("Project title"));
    assertThat(realmProject.projectPath, is("root/path"));
    assertThat(realmProject.resolution, is(VideoResolution.Resolution.HD720.name()));
    assertThat(realmProject.quality, is(VideoQuality.Quality.GOOD.name()));
    assertThat(realmProject.musicTitle, is("My cool music"));
    assertThat(realmProject.musicVolume, is(0.5f));
  }

}