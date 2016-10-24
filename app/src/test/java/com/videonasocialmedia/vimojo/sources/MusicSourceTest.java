package com.videonasocialmedia.vimojo.sources;

import android.os.Environment;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
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

  @Before
  public void setupTestEnvironment() {
    PowerMockito.mockStatic(Environment.class);
    mockedStorageDir = PowerMockito.mock(File.class);
    PowerMockito.when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).
            thenReturn(mockedStorageDir);
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

    Music musicFound = musicSource.getMusicByTitle(musicToFind.getMusicTitle());

    assertThat(musicFound, is(musicToFind));
  }

  @Test
  public void getMusicByTitleReturnsAudioMixedIfTitleIsAudioMixedTitle() {
    MusicSource musicSource = new MusicSource();
    musicSource = Mockito.spy(musicSource);
    musicSource.populateLocalMusic();
    Music musicToFind = new Music(Constants.OUTPUT_FILE_MIXED_AUDIO);
    musicToFind.setMusicTitle(Constants.MUSIC_AUDIO_MIXED_TITLE);
    Mockito.doNothing().when(musicSource).addPathToMusic(musicSource.localMusic);

    Music musicFound = musicSource.getMusicByTitle(musicToFind.getMusicTitle());

    assertThat(musicFound, notNullValue());
    assertThat(musicFound.getMusicTitle(), is(Constants.MUSIC_AUDIO_MIXED_TITLE));
    assertThat(musicFound.getMediaPath(), is(Constants.OUTPUT_FILE_MIXED_AUDIO));
  }

}