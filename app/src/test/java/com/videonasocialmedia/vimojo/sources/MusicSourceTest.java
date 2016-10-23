package com.videonasocialmedia.vimojo.sources;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class MusicSourceTest {

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

}