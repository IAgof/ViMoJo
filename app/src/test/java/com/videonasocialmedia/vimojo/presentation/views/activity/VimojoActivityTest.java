package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.PersistableBundle;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.VimojoTestApplication;
import com.videonasocialmedia.vimojo.domain.editor.LoadCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.test.shadows.ShadowMultiDex;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.realm.Realm;
import io.realm.internal.log.RealmLog;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
@Config(application = VimojoTestApplication.class, shadows = {ShadowMultiDex.class})
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
//@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*", "org.json.*" })
@PrepareForTest({MixpanelAPI.class, Activity.class, RealmLog.class, Realm.class})
//@Config(constants = BuildConfig.class, sdk = 19)
public class VimojoActivityTest {
  @Mock LoadCurrentProjectUseCase mockedUseCase;
  @Mock private android.os.Bundle mockedBundle;
  @Mock private PersistableBundle mockedState;
  private MixpanelAPI mockedMixpanel;
//  @Mock private Application context;
  @Mock private android.view.Window mockedWindow;
  private Realm mockedRealm;
//  @InjectMocks VimojoActivity injectedVimojoActivity;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void clearProjectInstance() {
    if (Project.INSTANCE != null) {
      Project.INSTANCE.clear();
    }
  }

  @Before
  public void setupTestRealm() {
    PowerMockito.mockStatic(RealmLog.class);
    PowerMockito.mockStatic(Realm.class);
    mockedRealm = PowerMockito.mock(Realm.class);
    PowerMockito.when(Realm.getDefaultInstance()).thenReturn(mockedRealm);
  }


//  @Before
//  public void setupMixpanelAPIForTest() {
//    mockedMixpanel = PowerMockito.mock(MixpanelAPI.class);
//    PowerMockito.mockStatic(MixpanelAPI.class);
////    PowerMockito.when(MixpanelAPI.getInstance(RuntimeEnvironment.application, BuildConfig.MIXPANEL_TOKEN)).thenReturn(mockedMixpanel);
//    PowerMockito.when(MixpanelAPI.getInstance(any(Context.class), any(String.class))).thenReturn(mockedMixpanel);
////    PowerMockito.doReturn(mockedMixpanel).
////            when(MixpanelAPI.getInstance(RuntimeEnvironment.application, BuildConfig.MIXPANEL_TOKEN));
////    PowerMockito.mockStatic(Activity.class);
////    PowerMockito.when(Activity.get)
//  }

  // TODO(jliarte): 23/10/16 cannot make this work!
  @Test
  @Ignore
  public void onCreateInjectsCurrentProject() {
    assert Project.INSTANCE == null;
//    ChildActivity activity = new ChildActivity();
//    ChildActivity activity = Mockito.spy(new ChildActivity());
//    Mockito.when(activity.getWindow()).thenReturn(mockedWindow);
    ChildActivity act = Robolectric.buildActivity(ChildActivity.class).create().get();
//    activity.mixpanel = mockedMixpanel;

//    act.onCreate(mockedBundle);

    assertThat(Project.INSTANCE, is(notNullValue()));
  }
}