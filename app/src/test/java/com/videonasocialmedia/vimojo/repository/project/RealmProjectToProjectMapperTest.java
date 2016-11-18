package com.videonasocialmedia.vimojo.repository.project;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.video.RealmVideo;
import com.videonasocialmedia.vimojo.sources.MusicSource;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doReturn;

/**
 * Created by jliarte on 21/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RealmProjectToProjectMapperTest {
  @Mock MusicSource mockedMusicSource;

  @InjectMocks RealmProjectToProjectMapper mockedMapper;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
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
  public void testMapSetsMusicOnProject() {
    RealmProject realmProject = getARealmProject();
    realmProject.musicTitle = "Sorrow and sadness";
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();
    Music music = new Music("music/path");
    music.setMusicTitle(realmProject.musicTitle);
    doReturn(music).when(mockedMusicSource).getMusicByTitle(realmProject.musicTitle);

    Project project = mockedMapper.map(realmProject);

    assertThat(project.hasMusic(), is(true));
    assertThat(project.getMusic().getMusicTitle(), is("Sorrow and sadness"));
    assertThat(project.getMusic(), is(music));
  }

  @Test
  public void testMapSetsMusicVolume() {
    RealmProject realmProject = getARealmProject();
    realmProject.musicTitle = Constants.MUSIC_AUDIO_MIXED_TITLE;
    realmProject.musicVolume = 0.8f;
    Music music = new Music("music/path");
    doReturn(music).when(mockedMusicSource).getMusicByTitle(realmProject.musicTitle);

    Project project = mockedMapper.map(realmProject);

    assertThat(project.hasMusic(), is(true));
    assertThat(project.getMusic().getVolume(), is(0.8f));
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

  @NonNull
  private RealmProject getARealmProject() {
    RealmProject realmProject = new RealmProject();
    realmProject.quality = VideoQuality.Quality.HIGH.name();
    realmProject.resolution = VideoResolution.Resolution.HD720.name();
    realmProject.frameRate = VideoFrameRate.FrameRate.FPS25.name();
    return realmProject;
  }
}