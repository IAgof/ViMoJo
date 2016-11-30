package com.videonasocialmedia.vimojo.repository.project;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 21/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectToRealmProjectMapperTest {
  @Test
  public void testMapReturnsRealmProjectInstance() {
    Project project = new Project(null, null, Profile.getInstance(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject, notNullValue());
  }

  @Test
  public void testMapSetsRealmProjectFieldsFromProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    Project project = new Project("Project title", "root/path", profile);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.title, is(project.getTitle()));
    assertThat(realmProject.quality, is(profile.getQuality().name()));
    assertThat(realmProject.resolution, is(profile.getResolution().name()));
    assertThat(realmProject.projectPath, is(project.getProjectPath()));
  }

  @Test
  public void testMapSetsRealmProjectMusicTitle() throws IllegalItemOnTrack {
    Project project = getAProject();
    Music music = new Music("music/path");
    music.setMusicTitle("Music title");
    project.getAudioTracks().get(0).insertItemAt(0, music);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.musicTitle, is(music.getMusicTitle()));
  }

  @Test
  public void testMapSetsRealmProjectMusicVolume() throws IllegalItemOnTrack {
    Project project = getAProject();
    Music music = new Music("music/path", 0.8f);
    music.setMusicTitle("Music title");
    project.getAudioTracks().get(0).insertItemAt(0, music);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.musicVolume, is(0.8f));
  }

  @Test
  public void testMapSetsRealmProjectVideos() throws IllegalItemOnTrack {
    Project project = getAProject();
    Video video = new Video("media/path");
    project.getMediaTrack().insertItem(video);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.videos.size(), is(1));
  }

  // TODO(jliarte): 23/10/16 what to do in this case? exception in realm
  //                   java.lang.IllegalArgumentException: Null objects cannot be copied into Realm.
  @Test
  public void testMapReturnsNullRealmProjectIfNullProjectProfile() {
    Project project = new Project("title", "root/path", null);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject, nullValue());
  }

  @NonNull
  private Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    return new Project("Project title", "root/path", profile);
  }
}