package com.videonasocialmedia.vimojo.repository.project;

import android.content.Context;
import android.os.Environment;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
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
public class ProjectRealmRepositoryTest {
  private Realm mockedRealm;
  private File mockedStorageDir;
  @Mock private Context mockedContext;
  private Project currentProject;

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
    ProjectRealmRepository repo = new ProjectRealmRepository();
    RealmQuery<RealmProject> mockedRealmQuery = PowerMockito.mock(RealmQuery.class);
    when(mockedRealm.where(RealmProject.class)).thenReturn(mockedRealmQuery);

    Project project = repo.getLastModifiedProject();

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
//    Profile profile = new Profile(VideoResolution.Resolution.H_720P, VideoQuality.Quality.EXCELLENT,
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
    Mockito.doNothing().when(repo).update(any(Project.class));

    repo.updateResolution(currentProject, videoResolution);

    assertThat(currentProject.getProfile().getResolution(), is(videoResolution));
  }

  @Test
  public void testUpdateFrameRateUpdateProject() {
    ProjectRealmRepository repo = Mockito.spy(new ProjectRealmRepository());
    VideoFrameRate.FrameRate videoFrameRate = DEFAULT_CAMERA_SETTING_VIDEO_FRAME_RATE;
    Mockito.doNothing().when(repo).update(any(Project.class));

    repo.updateFrameRate(currentProject, videoFrameRate);

    assertThat(currentProject.getProfile().getFrameRate(), is(videoFrameRate));
  }

  @Test
  public void testUpdateQualityUpdateProject() {
    ProjectRealmRepository repo = Mockito.spy(new ProjectRealmRepository());
    VideoQuality.Quality videoQuality = DEFAULT_CAMERA_SETTING_VIDEO_QUALITY;
    Mockito.doNothing().when(repo).update(any(Project.class));

    repo.updateQuality(currentProject, videoQuality);

    assertThat(currentProject.getProfile().getQuality(), is(videoQuality));
  }

  @Test
  public void testSetWatermarkActivatedUpdateProject() {
    ProjectRealmRepository repo = Mockito.spy(new ProjectRealmRepository());
    Mockito.doNothing().when(repo).update(any(Project.class));
    boolean watermarkActivated = true;
    assert(!currentProject.hasWatermark());

    currentProject.setWatermarkActivated(watermarkActivated);

    assertThat(currentProject.hasWatermark(), is(watermarkActivated));
  }

  @Test
  public void testSetProjectInfoUpdateProject() {
    ProjectRealmRepository repo = Mockito.spy(new ProjectRealmRepository());
    Mockito.doNothing().when(repo).update(any(Project.class));
    String title = "title";
    String description = "description";
    List<String> productTypeList = new ArrayList<>();
    productTypeList.add(ProductTypeProvider.Types.LIVE_ON_TAPE.name());
    productTypeList.add(ProductTypeProvider.Types.B_ROLL.name());
    productTypeList.add(ProductTypeProvider.Types.NAT_VO.name());

    repo.setProjectInfo(currentProject, title, description, productTypeList);

    assertThat(currentProject.getProjectInfo().getTitle(), is(title));
    assertThat(currentProject.getProjectInfo().getDescription(), is(description));
    assertThat(currentProject.getProjectInfo().getProductTypeList(), is(productTypeList));
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