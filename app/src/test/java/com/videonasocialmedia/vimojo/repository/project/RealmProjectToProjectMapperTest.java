package com.videonasocialmedia.vimojo.repository.project;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.music.RealmMusic;
import com.videonasocialmedia.vimojo.repository.track.RealmTrack;
import com.videonasocialmedia.vimojo.repository.video.RealmVideo;
import com.videonasocialmedia.vimojo.sources.MusicSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by jliarte on 21/10/16.
 */
//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(Environment.class)
public class RealmProjectToProjectMapperTest {
  @Mock MusicSource mockedMusicSource;
  @Mock Project mockedProject;
  @InjectMocks RealmProjectToProjectMapper mockedMapper;
  private File mockedStorageDir;
  @Mock private Context mockedContext;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setupTestEnvironment() {
    PowerMockito.mockStatic(Environment.class);
    mockedStorageDir = PowerMockito.mock(File.class);
    PowerMockito.when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).
        thenReturn(mockedStorageDir);
  }

  @Test
  public void testMapReturnsProjectInstance() {
    RealmProject realmProject = getARealmProject();
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();

    Project project = mapper.map(realmProject);

    assertThat(project, notNullValue());
  }

  @Test
  public void testMapReturnsProjectWithPrimitiveFields() {
    RealmProject realmProject = new RealmProject();
    String title = realmProject.title = "Project title";
    realmProject.projectPath = "project/path";
    realmProject.quality = VideoQuality.Quality.HIGH.name();
    realmProject.resolution = VideoResolution.Resolution.HD720.name();
    realmProject.frameRate = VideoFrameRate.FrameRate.FPS25.name();
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();

    Project project = mapper.map(realmProject);

    assertThat(project.getTitle(), is(title));
    assertThat(project.getProjectPath(), is("project/path"));
  }

  @Test
  public void testMapReturnsProjectWithProfile() {
    RealmProject realmProject = getARealmProject();
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();

    Project project = mapper.map(realmProject);
    Profile profile = project.getProfile();

    assertThat(profile.getQuality(), is(VideoQuality.Quality.HIGH));
    assertThat(profile.getResolution(), is(VideoResolution.Resolution.HD720));
    assertThat(profile.getFrameRate(), is(VideoFrameRate.FrameRate.FPS25));
  }

  @Test
  public void testMapReturnsNullIfEmptyQuality() {
    RealmProject realmProject = new RealmProject();
    realmProject.quality = VideoQuality.Quality.HIGH.name();
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();

    Project project = mapper.map(realmProject);

    assertThat(project, is(nullValue()));
  }

  @Test
  public void testMapReturnsNullIfEmptyResolution() {
    RealmProject realmProject = new RealmProject();
    realmProject.resolution = VideoResolution.Resolution.HD720.name();
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();

    Project project = mapper.map(realmProject);

    assertThat(project, is(nullValue()));
  }

  @Test
  public void testMapReturnsNullIfEmptyFrameRate() {
    RealmProject realmProject = new RealmProject();
    realmProject.frameRate = VideoFrameRate.FrameRate.FPS25.name();
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();

    Project project = mapper.map(realmProject);

    assertThat(project, is(nullValue()));
  }

  @Test
  public void testMapReturnsNullIfInvalidQuality() {
    RealmProject realmProject = new RealmProject();
    realmProject.quality = "esto no vale";
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();

    Project project = mapper.map(realmProject);

    assertThat(project, is(nullValue()));
  }

  @Test
  public void testMapReturnsNullIfInvalidResolution() {
    RealmProject realmProject = new RealmProject();
    realmProject.resolution = "esto no vale";
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();

    Project project = mapper.map(realmProject);

    assertThat(project, is(nullValue()));
  }

  @Test
  public void testMapReturnsNullIfInvalidFrameRate() {
    RealmProject realmProject = new RealmProject();
    realmProject.frameRate = "esto no vale";
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();

    Project project = mapper.map(realmProject);

    assertThat(project, is(nullValue()));
  }


  @Test
  public void testMapSetsProjectVideos() {
    RealmProject realmProject = getARealmProject();
    RealmVideo realmVideo = new RealmVideo();
    realmVideo.mediaPath = "media/path";
    realmProject.videos.add(realmVideo);

    Project project = mockedMapper.map(realmProject);

    assertThat(project.getMediaTrack().getItems().size(), is(1));
    Video video = (Video) project.getMediaTrack().getItems().get(0);
    assertThat(video.getMediaPath(), is("media/path"));
  }

  @Test
  public void testMapSetsProjectMusics() {
    RealmProject realmProject = getARealmProject();
    RealmMusic realmMusic = new RealmMusic();
    realmMusic.musicPath = "media/path";
    realmMusic.title = "titleSong";
    realmMusic.volume = 0.55f;
    realmMusic.duration = 70;

    realmProject.musics.add(realmMusic);

    Project project = mockedMapper.map(realmProject);

    Music music = (Music) project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_MUSIC)
        .getItems().get(0);
    assertThat(music.getMediaPath(), is("media/path"));
    assertThat(music.getVolume(), is(0.55f));
    assertThat(music.getDuration(), is(70));
  }

  @Test
  public void testMapSetsProjectTracks() {
    RealmProject realmProject = getARealmProject();
    RealmTrack realmTrack = new RealmTrack();
    realmTrack.id = Constants.INDEX_AUDIO_TRACKS_MUSIC;
    realmTrack.volume = 0.5f;
    realmTrack.mute = true;
    realmTrack.solo = false;

    realmProject.tracks.add(realmTrack);

    Project project = mockedMapper.map(realmProject);

    Track track = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACKS_MUSIC);
    assertThat(track.getVolume(), is(0.5f));
    assertThat(track.isMute(), is(true));
    assertThat(track.isSolo(), is(false));

  }

  @NonNull
  private RealmProject getARealmProject() {
    RealmProject realmProject = new RealmProject();
    realmProject.quality = VideoQuality.Quality.HIGH.name();
    realmProject.resolution = VideoResolution.Resolution.HD720.name();
    realmProject.frameRate = VideoFrameRate.FrameRate.FPS25.name();
    realmProject.projectPath = "/projects";
    return realmProject;
  }
}