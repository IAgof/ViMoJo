package com.videonasocialmedia.vimojo.repository.video;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class VideoRealmRepositoryTest {
  @Test
  public void testVideoRealmRepositoryConstructorSetsMappers() {
    VideoRealmRepository repo = new VideoRealmRepository();

    assertThat(repo.toVideoMapper, notNullValue());
    assertThat(repo.toRealmVideoMapper, notNullValue());
  }
}