package com.videonasocialmedia.vimojo.sources;

import android.content.Context;
import android.os.Environment;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Environment.class)
public class MusicSourceTest {

  private File mockedStorageDir;

  @Mock
  Project mockedProject;

  @Mock
  Context mockedContext;

  @Before
  public void setupTestEnvironment() {
    PowerMockito.mockStatic(Environment.class);
    mockedStorageDir = PowerMockito.mock(File.class);
    PowerMockito.when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).
            thenReturn(mockedStorageDir);
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testPopulateLocalMusicSetsMusicList() {
    MusicSource musicSource = new MusicSource();

    musicSource.populateLocalMusic();

    assertThat(musicSource.localMusic.size(), is(greaterThan(0)));
  }

  @Test
  public void testGetMusicByTitleReturnsFoundMusic() {
    MusicSource musicSource = new MusicSource();
    musicSource = Mockito.spy(musicSource);
    musicSource.populateLocalMusic();
    assert musicSource.localMusic.size() > 0;
    Music musicToFind = musicSource.localMusic.get(0);
    // TODO(jliarte): 23/10/16 Utils.getMusicFileByName returns NPE
    Mockito.doNothing().when(musicSource).addPathToMusic(musicSource.localMusic);

    Music musicFound = musicSource.getMusicByTitle("somePath", musicToFind.getMusicTitle());

    assertThat(musicFound, is(musicToFind));
  }

  @Test
  public void getMusicByTitleReturnsAudioMixedIfTitleIsAudioMixedTitle() {
    MusicSource musicSource = new MusicSource();
    musicSource = Mockito.spy(musicSource);
    musicSource.populateLocalMusic();
    String mixedMusicPath = "somePath" + File.separator +
        Constants.AUDIO_TEMP_RECORD_VOICE_OVER_FILENAME;
    Music musicToFind = new Music(mixedMusicPath);
    musicToFind.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    Mockito.doNothing().when(musicSource).addPathToMusic(musicSource.localMusic);

    Music musicFound = musicSource.getMusicByTitle("somePath", musicToFind.getMusicTitle());

    assertThat(musicFound, notNullValue());
    assertThat(musicFound.getMusicTitle(), is(Constants.MUSIC_AUDIO_VOICEOVER_TITLE));
    assertThat(musicFound.getMediaPath(), is(mixedMusicPath));
  }

}