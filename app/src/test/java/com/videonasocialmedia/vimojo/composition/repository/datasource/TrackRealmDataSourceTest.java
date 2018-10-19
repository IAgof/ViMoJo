package com.videonasocialmedia.vimojo.composition.repository.datasource;

import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoRealmDataSource;
import com.videonasocialmedia.vimojo.repository.music.datasource.MusicRealmDataSource;

import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by alvaro on 11/04/17.
 */

public class TrackRealmDataSourceTest {
  @Mock VideoRealmDataSource mockedVideoDataSource;
  @Mock MusicRealmDataSource mockedMusicDataSource;

  @Test
  public void testTrackRealmRepositoryConstructorSetsMappers() {
    TrackRealmDataSource repo = new TrackRealmDataSource(mockedVideoDataSource,
        mockedMusicDataSource);

    assertThat(repo.toTrackMapper, notNullValue());
    assertThat(repo.toRealmTrackMapper, notNullValue());
  }
}
