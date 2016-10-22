package com.videonasocialmedia.vimojo.repository.project;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.vimojo.sound.domain.AddMusicToProjectUseCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 21/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectToRealmProjectMapperTest {
  @Test
  public void testMapReturnsRealmProjectInstance() {
    Project project = new Project(null, null, Profile.getInstance(Profile.ProfileType.free));
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject, notNullValue());
  }

  @Test
  public void testMapSetsRealmProjectFieldsFromProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.EXCELLENT,
            -1, Profile.ProfileType.pro);
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
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.EXCELLENT,
            -1, Profile.ProfileType.pro);
    Project project = new Project("Project title", "root/path", profile);
    Music music = new Music("music/path");
    music.setMusicTitle("Music title");
    project.getAudioTracks().get(0).insertItemAt(0, music);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.musicTitle, is(music.getMusicTitle()));
  }
}