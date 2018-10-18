package com.videonasocialmedia.vimojo.composition.repository.datasource;

import android.content.Context;
import android.os.Environment;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.model.sources.ProductTypeProvider;

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
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

import static com.videonasocialmedia.vimojo.utils.Constants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 20/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Environment.class)
public class ProjectRealmDataSourceTest {
  private Realm mockedRealm;
  private File mockedStorageDir;
  @Mock private Context mockedContext;
  private Project currentProject;
  @Mock TrackRealmDataSource mockedTrackDataSource;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);

    PowerMockito.mockStatic(Environment.class);
    mockedStorageDir = PowerMockito.mock(File.class);
    when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).
        thenReturn(mockedStorageDir);
    when(Environment.getExternalStorageDirectory()).thenReturn(mockedStorageDir);
    getAProject();
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
    ProjectRealmDataSource repo = new ProjectRealmDataSource(mockedTrackDataSource);
    RealmQuery<RealmProject> mockedRealmQuery = PowerMockito.mock(RealmQuery.class);
    when(mockedRealm.where(RealmProject.class)).thenReturn(mockedRealmQuery);

    Project project = repo.getLastModifiedProject();

    verify(mockedRealmQuery).findFirst();
  }

  @Test
  public void testConstructorsSetsMappers() {
    ProjectRealmDataSource repo = new ProjectRealmDataSource(mockedTrackDataSource);

    assertThat(repo.toRealmProjectMapper, is(notNullValue()));
    assertThat(repo.toProjectMapper, is(notNullValue()));
  }

  // TODO(jliarte): 21/10/16 dont know how to test this yet
//  @Test
//  public void testAddCallsExecuteTransaction() {
//    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.EXCELLENT,
//            -1, Profile.ProfileType.pro);
//    Project project = new Project("Project title", "root/path", profile);
//    ProjectRealmDataSource repo = new ProjectRealmDataSource();
//    repo.realm = mockedRealm;
//
//    repo.add(project);
//
//    ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
//    verify(mockedRealm).executeTransaction(transactionCaptor.capture());
////    assertThat(transactionCaptor.getValue());
//  }

  @Test
  public void testSetWatermarkActivatedUpdateProject() {
    ProjectRealmDataSource repo = Mockito.spy(new ProjectRealmDataSource(mockedTrackDataSource));
    Mockito.doNothing().when(repo).update(any(Project.class));
    boolean watermarkActivated = true;
    assert(!currentProject.hasWatermark());

    currentProject.setWatermarkActivated(watermarkActivated);

    assertThat(currentProject.hasWatermark(), is(watermarkActivated));
  }


  public void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD1080,
        VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    ProjectInfo projectInfo = new ProjectInfo("Project title",
        "Project description", new ArrayList<>());
    currentProject = new Project(projectInfo, "/path","private/path",
        compositionProfile);
  }

}