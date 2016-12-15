package com.videonasocialmedia.vimojo.repository.project;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.internal.log.RealmLog;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 20/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Realm.class, RealmLog.class, RealmQuery.class})
public class RealmProjectRepositoryTest {

  private Realm mockedRealm;
  private RealmResults<RealmProject> mockedAllRealmProjects;

  @Mock
  private Context mockedContext;
  private RealmConfiguration defaultConfig;


  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setup() {

    mockStatic(RealmLog.class);
    mockStatic(Realm.class);

    Realm mockRealm = PowerMockito.mock(Realm.class);
    when(Realm.getDefaultInstance()).thenReturn(mockRealm);
    this.mockedRealm = mockRealm;
  }

  @Test
  public void shouldBeAbleToGetDefaultInstance() {
    assertThat(Realm.getDefaultInstance(), is(mockedRealm));
  }


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


  private Project createProject(){
      Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
          VideoFrameRate.FrameRate.FPS25);
    Project project = new Project("Project title", "root/path", profile);
    return project;
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
}