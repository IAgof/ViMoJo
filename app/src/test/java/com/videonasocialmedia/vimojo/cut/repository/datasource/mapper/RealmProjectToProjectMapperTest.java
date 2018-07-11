package com.videonasocialmedia.vimojo.cut.repository.datasource.mapper;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.sources.ProductTypeProvider;
import com.videonasocialmedia.vimojo.repository.music.datasource.RealmMusic;
import com.videonasocialmedia.vimojo.cut.repository.datasource.RealmProject;
import com.videonasocialmedia.vimojo.repository.track.datasource.RealmTrack;
import com.videonasocialmedia.vimojo.repository.video.datasource.RealmVideo;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by jliarte on 21/10/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(Environment.class)
public class RealmProjectToProjectMapperTest {
  @Mock MusicSource mockedMusicSource;
  @Mock Project mockedProject;
  @InjectMocks
  RealmProjectToProjectMapper mockedMapper;
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
    PowerMockito.when(Environment.getExternalStorageDirectory()).thenReturn(mockedStorageDir);
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

    assertThat(project.getProjectInfo().getTitle(), is(title));
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
  public void testMapsSetsProjectInfo() {
    RealmProject realmProject = getARealmProject();
    realmProject.title = "title";
    realmProject.description = "description";
    realmProject.productTypeList.add(ProductTypeProvider.Types.LIVE_ON_TAPE.name());
    realmProject.productTypeList.add(ProductTypeProvider.Types.B_ROLL.name());
    realmProject.productTypeList.add(ProductTypeProvider.Types.NAT_VO.name());
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();


    Project project = mapper.map(realmProject);

    assertThat(project.getProjectInfo().getTitle(), is("title"));
    assertThat(project.getProjectInfo().getDescription(), is("description"));
    List<String> productTypeList = new ArrayList<>();
    productTypeList.add(ProductTypeProvider.Types.LIVE_ON_TAPE.name());
    productTypeList.add(ProductTypeProvider.Types.B_ROLL.name());
    productTypeList.add(ProductTypeProvider.Types.NAT_VO.name());
    assertThat(project.getProjectInfo().getProductTypeList(), is(productTypeList));
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
    RealmTrack realmTrack = new RealmTrack();
    realmTrack.id = Constants.INDEX_AUDIO_TRACK_MUSIC;
    realmTrack.volume = 0.6f;
    realmTrack.position = 1;
    realmTrack.mute = false;
    realmProject.tracks.add(realmTrack);
    RealmMusic realmMusic = new RealmMusic();
    realmMusic.musicPath = "media/path";
    realmMusic.title = "titleSong";
    realmMusic.volume = 0.55f;
    realmMusic.duration = 70;
    realmProject.musics.add(realmMusic);

    Project project = mockedMapper.map(realmProject);

    Music music = (Music) project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC)
        .getItems().get(0);
    assertThat(music.getMediaPath(), is("media/path"));
    assertThat(music.getVolume(), is(0.55f));
    assertThat(music.getDuration(), is(70));
  }

  @Test
  public void testMapSetsProjectVoiceOver() {
    RealmProject realmProject = getARealmProject();
    //Add musicTrack
    RealmTrack realmTrackMusic = new RealmTrack();
    realmTrackMusic.id = Constants.INDEX_AUDIO_TRACK_MUSIC;
    realmTrackMusic.volume = 0.6f;
    realmTrackMusic.position = 1;
    realmTrackMusic.mute = false;
    realmProject.tracks.add(realmTrackMusic);
    //Add voiceOverTrack
    RealmTrack realmTrackVoiceOver = new RealmTrack();
    realmTrackVoiceOver.id = Constants.INDEX_AUDIO_TRACK_VOICE_OVER;
    realmTrackVoiceOver.volume = 0.9f;
    realmTrackVoiceOver.position = 2;
    realmTrackVoiceOver.mute = true;
    realmProject.tracks.add(realmTrackVoiceOver);

    RealmMusic realmVoiceOver = new RealmMusic();
    realmVoiceOver.musicPath = "media/path";
    realmVoiceOver.title = com.videonasocialmedia.vimojo.utils.Constants.MUSIC_AUDIO_VOICEOVER_TITLE;
    realmVoiceOver.volume = 0.55f;
    realmVoiceOver.duration = 70;
    realmProject.musics.add(realmVoiceOver);

    Project project = mockedMapper.map(realmProject);

    Music music = (Music) project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER)
        .getItems().get(0);
    assertThat(music.getMediaPath(), is("media/path"));
    assertThat(music.getVolume(), is(0.55f));
    assertThat(music.getDuration(), is(70));
  }

  @Test
  public void testMapReturnsProjectWithMediaTrack() {
    RealmProject realmProject = getARealmProject();
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();
    RealmTrack realmTrack = new RealmTrack();
    realmTrack.id = Constants.INDEX_MEDIA_TRACK;
    realmTrack.volume = 0.3f;
    realmTrack.position = 0;
    realmTrack.mute = false;
    realmProject.tracks.add(realmTrack);

    Project project = mapper.map(realmProject);

    MediaTrack mediaTrack = project.getMediaTrack();
    assertThat(mediaTrack.getVolume(), is(0.3f));
    assertThat(mediaTrack.isMuted(), is(false));
    assertThat(mediaTrack.getPosition(), is(0));
  }

  @Test
  public void testMapReturnsProjectWithAudioTrackMusic() {
    RealmProject realmProject = getARealmProject();
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();
    RealmTrack realmTrack = new RealmTrack();
    realmTrack.id = Constants.INDEX_AUDIO_TRACK_MUSIC;
    realmTrack.volume = 0.6f;
    realmTrack.position = 1;
    realmTrack.mute = false;
    realmProject.tracks.add(realmTrack);

    Project project = mapper.map(realmProject);

    AudioTrack musicTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
    assertThat(musicTrack.getVolume(), is(0.6f));
    assertThat(musicTrack.isMuted(), is(false));
    assertThat(musicTrack.getPosition(), is(1));
  }

  @Test
  public void testMapReturnsProjectWithAudioTrackVoiceOver() {
    RealmProject realmProject = getARealmProject();
    RealmProjectToProjectMapper mapper = new RealmProjectToProjectMapper();
    // First we need to have a MusicTrack in Project.
    RealmTrack realmTrackMusic = new RealmTrack();
    realmTrackMusic.id = Constants.INDEX_AUDIO_TRACK_MUSIC;
    realmTrackMusic.volume = 0.6f;
    realmTrackMusic.position = 1;
    realmTrackMusic.mute = false;
    realmProject.tracks.add(realmTrackMusic);

    RealmTrack realmTrack = new RealmTrack();
    realmTrack.id = Constants.INDEX_AUDIO_TRACK_VOICE_OVER;
    realmTrack.volume = 0.9f;
    realmTrack.position = 2;
    realmTrack.mute = true;
    realmProject.tracks.add(realmTrack);

    Project project = mapper.map(realmProject);

    AudioTrack voiceOverTrack = project.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
    assertThat(voiceOverTrack.getVolume(), is(0.9f));
    assertThat(voiceOverTrack.isMuted(), is(true));
    assertThat(voiceOverTrack.getPosition(), is(2));
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