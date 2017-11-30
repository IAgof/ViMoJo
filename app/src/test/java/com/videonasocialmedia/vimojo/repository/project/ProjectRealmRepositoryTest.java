package com.videonasocialmedia.vimojo.repository.project;

import android.content.Context;
import android.os.Environment;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.internal.log.RealmLog;

import static com.videonasocialmedia.vimojo.utils.Constants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 20/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Realm.class, RealmLog.class, RealmQuery.class, RealmResults.class, Environment.class})
public class ProjectRealmRepositoryTest {
  private Realm mockedRealm;
  private File mockedStorageDir;
  @Mock private Context mockedContext;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);

    PowerMockito.mockStatic(Environment.class);
    mockedStorageDir = PowerMockito.mock(File.class);
    when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).
        thenReturn(mockedStorageDir);
    when(Environment.getExternalStorageDirectory()).thenReturn(mockedStorageDir);
  }

//  @Before
//  public void setup() {
//    mockStatic(RealmLog.class);
//    mockStatic(Realm.class);
//
//    Realm mockRealm = PowerMockito.mock(Realm.class);
//
//    when(Realm.getDefaultInstance()).thenReturn(mockRealm);
//
//    this.mockedRealm = mockRealm;
//  }

//  @Test
//  public void shouldBeAbleToGetDefaultInstance() {
//    assertThat(Realm.getDefaultInstance(), is(mockedRealm));
//  }

  // TODO:(alvaro.martinez) 22/12/16 Study how to test getCurrentProject, now Query depends of lastModification
  @Ignore
  @Test
  public void testGetCurrentProjectReturnsLastSavedProject() {
    ProjectRealmRepository repo = new ProjectRealmRepository();
    RealmQuery<RealmProject> mockedRealmQuery = PowerMockito.mock(RealmQuery.class);
    when(mockedRealm.where(RealmProject.class)).thenReturn(mockedRealmQuery);

    Project project = repo.getCurrentProject();

    verify(mockedRealmQuery).findFirst();
  }

  @Test
  public void testConstructorsSetsMappers() {
    ProjectRealmRepository repo = new ProjectRealmRepository();

    assertThat(repo.toRealmProjectMapper, is(notNullValue()));
    assertThat(repo.toProjectMapper, is(notNullValue()));
  }

  // TODO(jliarte): 21/10/16 dont know how to test this yet
//  @Test
//  public void testAddCallsExecuteTransaction() {
//    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.EXCELLENT,
//            -1, Profile.ProfileType.pro);
//    Project project = new Project("Project title", "root/path", profile);
//    ProjectRealmRepository repo = new ProjectRealmRepository();
//    repo.realm = mockedRealm;
//
//    repo.add(project);
//
//    ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
//    verify(mockedRealm).executeTransaction(transactionCaptor.capture());
////    assertThat(transactionCaptor.getValue());
//  }

  @Test
  public void testUpdateResolutionUpdateProject() {
    ProjectRealmRepository repo = Mockito.spy(new ProjectRealmRepository());
    VideoResolution.Resolution videoResolution = DEFAULT_CAMERA_SETTING_VIDEO_RESOLUTION;
    Project project = getAProject();
    Mockito.doNothing().when(repo).update(any(Project.class));

    repo.updateResolution(project, videoResolution);

    assertThat(project.getProfile().getResolution(), is(videoResolution));
  }

  @Test
  public void testUpdateFrameRateUpdateProject() {
    ProjectRealmRepository repo = Mockito.spy(new ProjectRealmRepository());
    VideoFrameRate.FrameRate videoFrameRate = DEFAULT_CAMERA_SETTING_VIDEO_FRAME_RATE;
    Project project = getAProject();
    Mockito.doNothing().when(repo).update(any(Project.class));

    repo.updateFrameRate(project, videoFrameRate);

    assertThat(project.getProfile().getFrameRate(), is(videoFrameRate));
  }

  @Test
  public void testUpdateQualityUpdateProject() {
    ProjectRealmRepository repo = Mockito.spy(new ProjectRealmRepository());
    VideoQuality.Quality videoQuality = DEFAULT_CAMERA_SETTING_VIDEO_QUALITY;
    Project project = getAProject();
    Mockito.doNothing().when(repo).update(any(Project.class));

    repo.updateQuality(project, videoQuality);

    assertThat(project.getProfile().getQuality(), is(videoQuality));
  }

  @Test
  public void testSetWatermarkActivatedUpdateProject() {
    ProjectRealmRepository repo = Mockito.spy(new ProjectRealmRepository());
    Project project = getAProject();
    Mockito.doNothing().when(repo).update(any(Project.class));
    boolean watermarkActivated = true;
    assert(!project.hasWatermark());

    project.setWatermarkActivated(watermarkActivated);

    assertThat(project.hasWatermark(), is(watermarkActivated));
  }

  public Project getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD1080, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    return Project.getInstance("title", "/path", "private/path", compositionProfile);
  }

}