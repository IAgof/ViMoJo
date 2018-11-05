/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.text.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.asset.domain.usecase.UpdateMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 23/10/18.
 */

public class EditTextPreviewPresenterTest {

  @Mock private ProjectInstanceCache mockedProjectInstanceCache;
  @Mock private Context mockedContext;
  @Mock private EditTextView mockedEditTextView;
  @Mock private UserEventTracker mockedUserEvenTracker;
  @Mock private ModifyVideoTextAndPositionUseCase mockedModifyVideoTextAndPositionUseCase;
  @Mock private UpdateMedia mockedUpdateMedia;
  @Mock private UpdateComposition mockedUpdateComposition;
  private boolean amIAVerticalApp;
  @Mock private BackgroundExecutor mockedBackgroundExecutor;
  private Project currentProject;

  @Before
  public void injectMocks() throws IllegalItemOnTrack {
    MockitoAnnotations.initMocks(this);
    setAProjectWithSomeVideoWithText();
    when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
  }

  @Test
  public void constructorSetsUserTracker() {
    UserEventTracker userEventTracker = UserEventTracker.getInstance();
    EditTextPreviewPresenter editTextPreviewPresenter = new EditTextPreviewPresenter(mockedContext,
        mockedEditTextView, userEventTracker, mockedModifyVideoTextAndPositionUseCase,
        mockedProjectInstanceCache, mockedUpdateMedia, mockedUpdateComposition, amIAVerticalApp,
        mockedBackgroundExecutor);

    assertThat(editTextPreviewPresenter.userEventTracker, is(userEventTracker));
  }

  @Test
  public void updatePresenterSetsCurrentProject() {
    EditTextPreviewPresenter editTextPreviewPresenter = getEditTextPreviewPresenter();
    int videoIndexOnTrack = 0;

    editTextPreviewPresenter.updatePresenter(videoIndexOnTrack);

    assertThat(editTextPreviewPresenter.currentProject, is(currentProject));
  }

  @Test
  public void updatePresenterAttachView() {
    EditTextPreviewPresenter editTextPreviewPresenter = getEditTextPreviewPresenter();
    int videoIndexOnTrack = 0;

    editTextPreviewPresenter.updatePresenter(videoIndexOnTrack);

    verify(mockedEditTextView).attachView(mockedContext);
  }

  @Test
  public void updatePresenterSetAspectRatioVerticalIfIsAVerticalApp() {
    EditTextPreviewPresenter spyEditTextPreviewPresenter = spy(getEditTextPreviewPresenter());
    int videoIndexOnTrack = 0;
    spyEditTextPreviewPresenter.amIAVerticalApp = true;

    spyEditTextPreviewPresenter.updatePresenter(videoIndexOnTrack);

    verify(mockedEditTextView)
        .setAspectRatioVerticalVideos(Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
  }

  @Test
  public void updatePresenterInitSingleComposition() {
    EditTextPreviewPresenter editTextPreviewPresenter = getEditTextPreviewPresenter();
    int videoIndexOnTrack = 0;

    editTextPreviewPresenter.updatePresenter(videoIndexOnTrack);

    verify(mockedEditTextView).initSingleClip(any(VMComposition.class), eq(videoIndexOnTrack));
  }

  @Test
  public void pausePresenterDetachPlayerView() {
    EditTextPreviewPresenter editTextPreviewPresenter = getEditTextPreviewPresenter();

    editTextPreviewPresenter.pausePresenter();

    verify(mockedEditTextView).detachView();
  }

  @Test
  public void updatePresenterHideSeekBarPlayerView() {
    EditTextPreviewPresenter editTextPreviewPresenter = getEditTextPreviewPresenter();
    int videoIndexOnTrack = 0;

    editTextPreviewPresenter.updatePresenter(videoIndexOnTrack);

    verify(mockedEditTextView).setSeekBarLayoutEnabled(false);
  }

  @Test
  public void updatePresenterIfVideoHasTextSetVideoTextParamsView() {
    EditTextPreviewPresenter editTextPreviewPresenter = getEditTextPreviewPresenter();
    int videoIndexOnTrack = 0;
    Video video = (Video) currentProject.getMediaTrack().getItems().get(0);
    assertThat("Video has text", video.hasText(), is(true));

    editTextPreviewPresenter.updatePresenter(videoIndexOnTrack);

    verify(mockedEditTextView).setEditText(video.getClipText());
    verify(mockedEditTextView).setPositionEditText(video.getClipTextPosition());
    verify(mockedEditTextView).setCheckboxShadow(video.hasClipTextShadow());
  }

  @Test
  public void setTextToVideoCallsUseCase() {
    EditTextPreviewPresenter spyEditTextPreviewPresenter = spy(getEditTextPreviewPresenter());
    int videoIndexOnTrack = 0;
    spyEditTextPreviewPresenter.updatePresenter(videoIndexOnTrack);
    Video video = (Video) currentProject.getMediaTrack().getItems().get(0);
    String text = video.getClipText();
    String textPosition = video.getClipTextPosition();
    boolean isShadowSelected = video.hasClipTextShadow();

    spyEditTextPreviewPresenter.setTextToVideo();

    verify(mockedModifyVideoTextAndPositionUseCase).addTextToVideo(currentProject, video, text,
        textPosition, isShadowSelected);
  }

  @Test
  public void setCheckboxShadowUpdatePlayerIfItIsReady() {
    EditTextPreviewPresenter spyEditTextPreviewPresenter = spy(getEditTextPreviewPresenter());
    spyEditTextPreviewPresenter.isPlayerReady = true;
    int videoIndexOnTrack = 0;
    spyEditTextPreviewPresenter.updatePresenter(videoIndexOnTrack);
    Video video = (Video) currentProject.getMediaTrack().getItems().get(0);
    String text = video.getClipText();
    String textPosition = video.getClipTextPosition();
    boolean isShadowSelected = false;

    spyEditTextPreviewPresenter.setCheckboxShadow(isShadowSelected);

    verify(mockedEditTextView).setImageText(text, textPosition, isShadowSelected,
        Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);
  }

  @Test
  public void onClickPositionTopCallsEditTextAndPlayerViews() {
    EditTextPreviewPresenter spyEditTextPreviewPresenter = spy(getEditTextPreviewPresenter());
    int videoIndexOnTrack = 0;
    spyEditTextPreviewPresenter.updatePresenter(videoIndexOnTrack);
    Video video = (Video) currentProject.getMediaTrack().getItems().get(0);
    String text = video.getClipText();
    String textPosition = TextEffect.TextPosition.TOP.name();
    boolean isShadowSelected = video.hasClipTextShadow();

    spyEditTextPreviewPresenter.onClickPositionTop();

    verify(mockedEditTextView).setPositionEditText(textPosition);
    verify(mockedEditTextView).hideKeyboard();
    verify(mockedEditTextView).setImageText(text, textPosition, isShadowSelected,
        Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);
  }

  @Test
  public void onClickPositionCenterCallsEditTextAndPlayerViews() {
    EditTextPreviewPresenter spyEditTextPreviewPresenter = spy(getEditTextPreviewPresenter());
    int videoIndexOnTrack = 0;
    spyEditTextPreviewPresenter.updatePresenter(videoIndexOnTrack);
    Video video = (Video) currentProject.getMediaTrack().getItems().get(0);
    String text = video.getClipText();
    String textPosition = TextEffect.TextPosition.CENTER.name();
    boolean isShadowSelected = video.hasClipTextShadow();

    spyEditTextPreviewPresenter.onClickPositionCenter();

    verify(mockedEditTextView, atLeast(2)).setPositionEditText(textPosition);
    verify(mockedEditTextView).hideKeyboard();
    verify(mockedEditTextView).setImageText(text, textPosition, isShadowSelected,
        Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);
  }

  @Test
  public void onClickPositionBottomCallsEditTextAndPlayerViews() {
    EditTextPreviewPresenter spyEditTextPreviewPresenter = spy(getEditTextPreviewPresenter());
    int videoIndexOnTrack = 0;
    spyEditTextPreviewPresenter.updatePresenter(videoIndexOnTrack);
    Video video = (Video) currentProject.getMediaTrack().getItems().get(0);
    String text = video.getClipText();
    String textPosition = TextEffect.TextPosition.BOTTOM.name();
    boolean isShadowSelected = video.hasClipTextShadow();

    spyEditTextPreviewPresenter.onClickPositionBottom();

    verify(mockedEditTextView).setPositionEditText(textPosition);
    verify(mockedEditTextView).hideKeyboard();
    verify(mockedEditTextView).setImageText(text, textPosition, isShadowSelected,
        Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);
  }

  @Test
  public void onTextChangedUpdatePlayerImageText() {
    EditTextPreviewPresenter spyEditTextPreviewPresenter = spy(getEditTextPreviewPresenter());
    int videoIndexOnTrack = 0;
    spyEditTextPreviewPresenter.updatePresenter(videoIndexOnTrack);
    Video video = (Video) currentProject.getMediaTrack().getItems().get(0);
    String text = "some text changed";
    String textPosition = video.getClipTextPosition();
    boolean isShadowSelected = video.hasClipTextShadow();

    spyEditTextPreviewPresenter.onTextChanged(text);

    verify(mockedEditTextView).setImageText(text, textPosition, isShadowSelected,
        Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);
  }

  @Test
  public void editTextCancelNavigateToEditScreen() {
    EditTextPreviewPresenter spyEditTextPreviewPresenter = spy(getEditTextPreviewPresenter());
    int videoIndexOnTrack = 0;
    spyEditTextPreviewPresenter.updatePresenter(videoIndexOnTrack);

    spyEditTextPreviewPresenter.editTextCancel();

    verify(mockedEditTextView).navigateTo(EditActivity.class, videoIndexOnTrack);
  }

  @Test
  public void playerReadyUpdatePlayerImageText() {
    EditTextPreviewPresenter spyEditTextPreviewPresenter = spy(getEditTextPreviewPresenter());
    int videoIndexOnTrack = 0;
    spyEditTextPreviewPresenter.updatePresenter(videoIndexOnTrack);
    Video video = (Video) currentProject.getMediaTrack().getItems().get(0);
    String text = video.getClipText();
    String textPosition = video.getClipTextPosition();
    boolean isShadowSelected = video.hasClipTextShadow();

    spyEditTextPreviewPresenter.playerReady();

    verify(mockedEditTextView).setImageText(text, textPosition, isShadowSelected,
        Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);

  }

  @NonNull
  private EditTextPreviewPresenter getEditTextPreviewPresenter(){
    return new EditTextPreviewPresenter(mockedContext, mockedEditTextView,
        mockedUserEvenTracker, mockedModifyVideoTextAndPositionUseCase, mockedProjectInstanceCache,
        mockedUpdateMedia, mockedUpdateComposition, amIAVerticalApp, mockedBackgroundExecutor);
  }
  private void setAProjectWithSomeVideoWithText() throws IllegalItemOnTrack {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
    Video video = new Video("some/path", Video.DEFAULT_VOLUME);
    video.setClipText("some text");
    video.setClipTextPosition(TextEffect.TextPosition.CENTER.name());
    video.setClipTextShadow(true);
    currentProject.getMediaTrack().insertItem(video);
  }
}
