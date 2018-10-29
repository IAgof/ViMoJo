/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 26/10/18.
 */

public class MusicListPresenterTest {

  @Mock private Context mockedContext;
  @Mock private MusicListView mockedMusicListView;
  @Mock private VideonaPlayer mockedVideonaPlayerView;
  @Mock private GetMusicListUseCase mockedGetMusicListUseCase;
  @Mock private ProjectInstanceCache mockedProjectInstanceCache;
  private boolean amIAVerticalApp;
  private Project currentProject;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    setAProject();
    when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
  }

  @Test
  public void updatePresenterShowMusicList() {
    MusicListPresenter musicListPresenter = getMusicListPresenter();

    musicListPresenter.updatePresenter();

    verify(mockedMusicListView).showMusicList(anyList());
  }

  @Test
  public void updatePresenterSetAspectRatioVerticalIfIsAVerticalApp() {
    MusicListPresenter spyMusicListPresenter = spy(getMusicListPresenter());
    spyMusicListPresenter.amIVerticalApp = true;

    spyMusicListPresenter.updatePresenter();

    verify(mockedVideonaPlayerView)
        .setAspectRatioVerticalVideos(Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
  }

  @Test
  public void updatePresenterInitPlayerFromComposition() {
    MusicListPresenter musicListPresenter = getMusicListPresenter();

    musicListPresenter.updatePresenter();

    verify(mockedVideonaPlayerView).init(any(VMComposition.class));
  }

  @Test
  public void updatePresenterAttachPlayerView() {
    MusicListPresenter musicListPresenter = getMusicListPresenter();

    musicListPresenter.updatePresenter();

    verify(mockedVideonaPlayerView).attachView(mockedContext);
  }

  @Test
  public void removePresenterDetachPlayerView() {
    MusicListPresenter musicListPresenter = getMusicListPresenter();

    musicListPresenter.removePresenter();

    verify(mockedVideonaPlayerView).detachView();
  }

  @Test
  public void setMusicNavigateToDetail() {
    MusicListPresenter musicListPresenter = getMusicListPresenter();
    Music someMusic = new Music("some/path", Music.DEFAULT_VOLUME, 59);

    musicListPresenter.selectMusic(someMusic);

    verify(mockedMusicListView).navigateToDetailMusic(someMusic.getMediaPath());
  }

  private MusicListPresenter getMusicListPresenter() {
    return new MusicListPresenter(mockedContext, mockedMusicListView,
        mockedVideonaPlayerView, mockedGetMusicListUseCase,
        mockedProjectInstanceCache, amIAVerticalApp);
  }

  private void setAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }
}
