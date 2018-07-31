package com.videonasocialmedia.vimojo.repository.project;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

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
    Profile compositionProfile = new Profile(VideoResolution.Resolution.H_720P,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    Project project = new Project(projectInfo, null, null, compositionProfile);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject, notNullValue());
  }

  @Test
  public void testMapSetsRealmProjectFieldsFromProject() {
    Profile profile = new Profile(VideoResolution.Resolution.H_720P, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    ProjectInfo projectInfo = new ProjectInfo("Project title",
        "Project description", new ArrayList<>());

    Project project = new Project(projectInfo, "root/path",
        "private/path", profile);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.title, is(project.getProjectInfo().getTitle()));
    assertThat(realmProject.description, is(project.getProjectInfo().getDescription()));
    assertThat(realmProject.quality, is(profile.getQuality().name()));
    assertThat(realmProject.resolution, is(profile.getResolution().name()));
    assertThat(realmProject.projectPath, is(project.getProjectPath()));
  }

  @Test
  public void testMapSetsRealmProjectVideos() throws IllegalItemOnTrack {
    Project project = getAProject();
    Video video = new Video("media/path", 1f);
    project.getMediaTrack().insertItem(video);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.videos.size(), is(1));
  }

  @Test
  public void testMapSetsRealmProjectMusics() throws IllegalItemOnTrack {
    Project project = getAProject();
    Music music = new Music("media/path", 1f, 60);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).insertItem(music);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).insertItem(music);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.musics.size(), is(2));
  }

  @Test
  public void testMapSetsRealmProjectVoiceOver() throws IllegalItemOnTrack {
    Project project = getAProject();
    project.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    Music voiceOver = new Music(com.videonasocialmedia.vimojo.utils
        .Constants.MUSIC_AUDIO_VOICEOVER_TITLE, 0.5f, 60);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).insertItem(voiceOver);
    assertThat("Project has voice over", project.hasVoiceOver(), is(true));
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.musics.size(), is(1));
  }

  @Test
  public void testMapSetsRealmProjectMusicAndVoiceOver() throws IllegalItemOnTrack {
    Project project = getAProject();
    project.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    Music voiceOver = new Music(com.videonasocialmedia.vimojo.utils
        .Constants.MUSIC_AUDIO_VOICEOVER_TITLE, 0.5f, 60);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).insertItem(voiceOver);
    Music music = new Music("media/path", 0.5f, 60);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).insertItem(music);
    assertThat("Project has voice over", project.hasVoiceOver(), is(true));
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.musics.size(), is(2));
  }

  @Test
  public void testMapSetsRealmProjectTracks() throws IllegalItemOnTrack {
    Project project = getAProject();
    project.getAudioTracks().add(new AudioTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER));
    Music voiceOver = new Music(com.videonasocialmedia.vimojo.utils
        .Constants.MUSIC_AUDIO_VOICEOVER_TITLE, 0.5f, 60);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER).insertItem(voiceOver);
    Music music = new Music("media/path", 0.5f, 60);
    project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).insertItem(music);
    assertThat("Project has MediaTrack", project.getMediaTrack(), notNullValue());
    assertThat("Project has two AudioTrack", project.getAudioTracks().size(), is(2));
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject.tracks.size(), is(3));
  }

  // TODO(jliarte): 23/10/16 what to do in this case? exception in realm
  //                   java.lang.IllegalArgumentException: Null objects cannot be copied into Realm.
  @Test
  public void testMapReturnsNullRealmProjectIfNullProjectProfile() {

    ProjectInfo projectInfo = new ProjectInfo("Project title",
        "Project description", new ArrayList<>());
    Project project = new Project(projectInfo, "root/path",
        "private/path", null);
    ProjectToRealmProjectMapper mapper = new ProjectToRealmProjectMapper();

    RealmProject realmProject = mapper.map(project);

    assertThat(realmProject, nullValue());
  }

  @NonNull
  private Project getAProject() {
    Profile profile = new Profile(VideoResolution.Resolution.H_720P, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    ProjectInfo projectInfo = new ProjectInfo("Project title",
        "Project description", new ArrayList<>());
    return new Project(projectInfo, "root/path", "private/path", profile);
  }

}