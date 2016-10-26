package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.sound.domain.MixAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.internal.log.RealmLog;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
//@PrepareForTest(Environment.class)
@PrepareForTest({Realm.class, RealmLog.class})
public class SoundVolumePresenterTest {
//  @Mock SoundVolumeView soundVolumeView;
  @Mock MixAudioUseCase mockedMixAudioUseCase;
  @Mock RemoveMusicFromProjectUseCase mockedRemoveMusicFromProjectUseCase;
  @InjectMocks SoundVolumePresenter injectedPresenter;
//  private File mockedStorageDir;
  private Realm mockedRealm;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }
//
//  @Before
//  public void setupTestEnvironment() {
//    PowerMockito.mockStatic(Environment.class);
//    mockedStorageDir = PowerMockito.mock(File.class);
//    PowerMockito.when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).
//            thenReturn(mockedStorageDir);
//  }
//
  @Before
  public void setupTestRealm() {
    PowerMockito.mockStatic(RealmLog.class);
    PowerMockito.mockStatic(Realm.class);
    mockedRealm = PowerMockito.mock(Realm.class);
    PowerMockito.when(Realm.getDefaultInstance()).thenReturn(mockedRealm);
  }

  // TODO(jliarte): 23/10/16 I'm unable to get this working as cannot inject doubles in both
  //                constructor and fields of the presenter class
  @Test
  @Ignore
  public void removeMusicFromProjectCallsRemoveMusicFromProject() throws IllegalItemOnTrack {
    Project currentProject = Project.getInstance(null, null, null);
    Music music = new Music("media/path");
    currentProject.getAudioTracks().get(0).insertItemAt(0, music);

    injectedPresenter.removeMusicFromProject();

    Mockito.verify(mockedRemoveMusicFromProjectUseCase).removeMusicFromProject(music, 0);
  }

  @NonNull
  private SoundVolumeView getSoundVolumeView() {
    return new SoundVolumeView() {
      @Override
      public void bindVideoList(List<Video> movieList) {

      }

      @Override
      public void resetPreview() {

      }

      @Override
      public void goToEditActivity() {

      }
    };
  }
}