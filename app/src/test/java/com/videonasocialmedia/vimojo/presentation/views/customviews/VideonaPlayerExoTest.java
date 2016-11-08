package com.videonasocialmedia.vimojo.presentation.views.customviews;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.main.VimojoTestApplication;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.main.modules.MockedDataRepositoriesModule;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 25/10/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = VimojoTestApplication.class, constants = BuildConfig.class, sdk = 21,
        shadows = {ShadowMultiDex.class})
public class VideonaPlayerExoTest {

  @Before
  public void setup() {
    VimojoTestApplication app = (VimojoTestApplication) RuntimeEnvironment.application;
    app.setDataRepositoryModule(new MockedDataRepositoriesModule());
  }

  @Test
  public void testBugGetClipPositionFromTimeLineTimeThrowsNPEIfVideoListSizeIsZero() {
    EditActivity editActivity = Robolectric.buildActivity(EditActivity.class).create().get();
    VideonaPlayerExo videonaPlayerExo = new VideonaPlayerExo(editActivity);

    int result = videonaPlayerExo.getClipPositionFromTimeLineTime();

    assertThat(result, is(0));
  }

}