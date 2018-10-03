package com.videonasocialmedia.vimojo.presentation.views.activity;
/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Álvaro Martínez Marco
 *
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.roughike.bottombar.BottomBar;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditPresenter;

import com.videonasocialmedia.vimojo.presentation.mvp.views.EditActivityView;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.split.presentation.views.activity.VideoSplitActivity;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.trim.presentation.views.activity.VideoTrimActivity;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FabUtils;
import com.videonasocialmedia.vimojo.videonaTimeLine.view.customview.VideonaTimeLine;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

public class EditActivity extends EditorActivity implements EditActivityView,
        VideoTranscodingErrorNotifier, VideonaPlayer.VideonaPlayerListener,
        VideonaTimeLine.Listener {
  private static String LOG_TAG = EditActivity.class.getCanonicalName();

    private final String THEME_DARK = "dark";

    @Inject EditPresenter editPresenter;

  private final int ID_BUTTON_FAB_TOP = 1;
  private final int ID_BUTTON_FAB_CENTER = 2;

  // UI elements
    @Nullable @BindView(R.id.button_edit_duplicate)
    ImageButton editDuplicateButton;
    @Nullable @BindView(R.id.button_edit_trim)
    ImageButton editTrimButton;
    @Nullable @BindView(R.id.button_edit_split)
    ImageButton editSplitButton;
    @Nullable @BindView(R.id.button_edit_add_text)
    ImageButton editTextButton;

  @Nullable @BindView(R.id.videona_time_line)
  VideonaTimeLine videonaTimeLine;

    @Nullable @BindView(R.id.fab_edit_room)
    FloatingActionsMenu fabMenu;
    @Nullable @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @Nullable @BindView(R.id.relative_layout_activity_edit)
    RelativeLayout relativeLayoutActivityEdit;
    @Nullable @BindView(R.id.button_edit_warning_transcoding_file)
    ImageButton warningTranscodingFilesButton;

    private int currentVideoIndex = 0;
    private FloatingActionButton newFab;

  private String warningTranscodingFilesMessage;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        inflateLinearLayout(R.id.container_layout, R.layout.activity_edit);
        inflateLinearLayout(R.id.container_navigator,
                R.layout.edit_activity_layout_button_navigator);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);
        if (savedInstanceState != null) {
            this.currentVideoIndex = savedInstanceState.getInt(Constants.CURRENT_VIDEO_INDEX);
          }
        setupBottomBar(bottomBar);
        setupFabMenu();
        setupActivityButtons();
      }

  private void setupActivityButtons() {
    String currentTheme = editPresenter.getCurrentTheme();
    if (currentTheme.compareTo(THEME_DARK) == 0 ){
      tintEditButtons(R.color.button_color_theme_dark);
    } else {
      tintEditButtons(R.color.button_color_theme_light);
    }
  }

  private void tintEditButtons(int tintList) {
    tintButton(editTrimButton,tintList);
    tintButton(editSplitButton,tintList);
    tintButton(editTextButton,tintList);
    tintButton(editDuplicateButton,tintList);
  }

  private void setupBottomBar(BottomBar bottomBar) {
    bottomBar.setOnTabSelectListener(tabId -> {
      switch (tabId){
        case(R.id.tab_sound):
          navigateTo(SoundActivity.class);
          break;
        case (R.id.tab_share):
          navigateTo(ShareActivity.class);
          break;
      }
    });
  }

   private void setupFabMenu() {
     addAndConfigurateFabButton(ID_BUTTON_FAB_TOP,
             R.drawable.common_navigate_record, R.color.colorWhite);
     addAndConfigurateFabButton(ID_BUTTON_FAB_CENTER,
             R.drawable.common_navigate_gallery, R.color.colorWhite);
  }

  private void addAndConfigurateFabButton(int id, int icon, int color) {
    newFab = FabUtils.createNewFabMini(id, icon, color);
    onClickFabButton(newFab);
    fabMenu.addButton(newFab);
  }

  private void onClickFabButton(final FloatingActionButton fab) {
    fab.setOnClickListener(v -> {
      switch (fab.getId()){
        case ID_BUTTON_FAB_TOP:
          fabMenu.collapse();
            navigateTo(RecordCamera2Activity.class);
            break;
        case ID_BUTTON_FAB_CENTER:
          fabMenu.collapse();
          navigateTo(GalleryActivity.class);
          break;
      }
    });
  }

  @Override
  protected void onStart() {
      super.onStart();
      videonaTimeLine.initVideoListRecycler(isLandscapeOriented());
  }

  @Override
  protected void onResume() {
    super.onResume();
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      if (bundle.containsKey(Constants.CURRENT_VIDEO_INDEX)) {
        this.currentVideoIndex = getIntent()
                .getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
      }
    }
    bottomBar.selectTabWithId(R.id.tab_editactivity);
    videonaTimeLine.setListener(this);
    Futures.addCallback(editPresenter.updatePresenter(), new FutureCallback<Object>() {
      @Override
      public void onSuccess(@javax.annotation.Nullable Object result) {
        // TODO(jliarte): 9/07/18 handle this async
        seekToClip(currentVideoIndex);
      }

      @Override
      public void onFailure(Throwable t) {}
    });
  }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

  // TODO(jliarte): 9/07/18 check if this is still needed
//  @Override
//  protected void onSaveInstanceState(Bundle outState) {
//    Log.d(LOG_TAG, "onSaveInstanceState");
//    outState.putInt(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
//    videoListState = videoListRecyclerView.getLayoutManager().onSaveInstanceState();
//    outState.putParcelable(SAVED_LAYOUT_MANAGER, videoListState);
//    // Reset touchHelper. Created new one onRestore, key line to restore activity without problems.
//    if (touchHelper != null) {
//      touchHelper.attachToRecyclerView(null);
//    }
//    super.onSaveInstanceState(outState);
//  }
//

  @Override
  protected void onRestoreInstanceState(Bundle state) {
    Log.d(LOG_TAG, "onRestoreInstanceState" + state);
    super.onRestoreInstanceState(state);
    if (state != null) {
      // TODO(jliarte): 8/07/18 Check if this is still needed
//      layoutManager.onRestoreInstanceState(videoListState);
//      editPresenter.updatePresenter(); // (jliarte): 9/07/18 already done in onResume!
    }
  }

  public void navigateTo(Class cls) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        startActivity(intent);
    }

    @Optional @OnClick(R.id.button_edit_duplicate)
    public void onClickEditDuplicate() {
        if (!editDuplicateButton.isEnabled())
            return;
        navigateTo(VideoDuplicateActivity.class, currentVideoIndex);
    }

    public void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
        finish();
    }

   @Optional @OnClick(R.id.button_edit_trim)
    public void onClickEditTrim() {
        if (!editTrimButton.isEnabled())
            return;
        navigateTo(VideoTrimActivity.class, currentVideoIndex);
    }

    @Optional @OnClick(R.id.button_edit_split)
    public void onClickEditSplit() {
        if (!editSplitButton.isEnabled())
            return;
        navigateTo(VideoSplitActivity.class, currentVideoIndex);
    }

    @Optional @OnClick (R.id.button_edit_add_text)
    public void onClickEditText() {
      if (!editTextButton.isEnabled()) {
        return;
      }
      navigateTo( VideoEditTextActivity.class, currentVideoIndex);
    }

    @Optional @OnClick(R.id.button_edit_warning_transcoding_file)
    public void onClickWarningTranscodingFile(){
      AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.VideonaDialog);
      dialog.setTitle(getString(R.string.dialog_title_warning_error_transcoding_file));
      dialog.setMessage(getString(R.string.dialog_message_warning_error_transcoding_file));
      dialog.setNeutralButton(getString(R.string.ok), (dialog1, which) -> dialog1.dismiss());
      dialog.show();
    }

    public void navigateTo(Class cls, String videoToSharePath) {
        Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
        intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoToSharePath);
        startActivity(intent);
        finish();
    }

  // ------------- VideonaTimeLine.Listener ----------------

  @Override
  public void onTimeLineClipSelected(int position) {
    Log.d(LOG_TAG, "onTimeLineClipSelected position " + position);
    this.currentVideoIndex = position;
    pausePreview();
    seekToClip(position);
  }

  @Override
  public void onTimeLineClipRemoved(int position) {
    Log.d(LOG_TAG, "onTimeLineClipRemoved position " + position);
    currentVideoIndex = Math.max(position - 1, 0);
    editPresenter.removeVideoFromProject(position);
  }

  @Override
  public void onTimeLineReorderStarted() {
    // TODO(jliarte): 9/07/18 seems that pausing preview prevents init movement
    pausePreview();
  }

  @Override
  public void onTimeLineReorderFinished(int fromPosition, int toPosition) {
    Log.d(LOG_TAG, "onTimeLineReorderFinished " + fromPosition + " to  " + toPosition);
    currentVideoIndex = toPosition;
    editPresenter.moveClip(fromPosition, toPosition);
  }

  // ------------- end of VideonaTimeLine.Listener ----------------

    @Override
    public void showError(final int stringToast) {
        Snackbar snackbar = Snackbar.make(relativeLayoutActivityEdit, stringToast,
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void showMessage(final int stringToast) {
        Snackbar snackbar = Snackbar.make(relativeLayoutActivityEdit, stringToast,
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void updateVideoList(final List<Video> retrievedVideoList) {
      runOnUiThread(() -> {
        Log.d(LOG_TAG, "updateVideoList");
        videonaTimeLine.setVideoList(retrievedVideoList);
        videonaTimeLine.setSelectedClip(currentVideoIndex);
      });
    }

  @Override
    public void enableEditActions() {
      runOnUiThread(() -> {
        editTrimButton.setEnabled(true);
        editSplitButton.setEnabled(true);
        editTextButton.setEnabled(true);
        editDuplicateButton.setEnabled(true);
      });
    }

    @Override
    public void disableEditActions() {
      runOnUiThread(() -> {
        editTrimButton.setEnabled(false);
        editSplitButton.setEnabled(false);
        editTextButton.setEnabled(false);
        editDuplicateButton.setEnabled(false);
      });
  }

  @Override
  public void enableBottomBar() {
      runOnUiThread(() -> {
        bottomBar.getTabWithId(R.id.tab_sound).setEnabled(true);
        bottomBar.getTabWithId(R.id.tab_share).setEnabled(true);
      });
  }

  @Override
  public void disableBottomBar() {
      runOnUiThread(() -> {
        bottomBar.getTabWithId(R.id.tab_sound).setEnabled(false);
        bottomBar.getTabWithId(R.id.tab_share).setEnabled(false);
      });
  }

  @Override
  public void changeAlphaBottomBar(float alpha) {
      runOnUiThread(() -> {
        bottomBar.getTabWithId(R.id.tab_sound).setAlpha(alpha);
        bottomBar.getTabWithId(R.id.tab_share).setAlpha(alpha);
      });
  }

    @Override
    public void showDialogMediasNotFound() {
      runOnUiThread(() -> {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.VideonaDialog);
        dialog.setTitle(R.string.titleVideosNotFound);
        dialog.setMessage(getString(R.string.messageVideosNotFound));
        dialog.setNeutralButton(getString(R.string.ok), (dialog1, which) -> dialog1.dismiss());
        dialog.show();
      });
    }

  @Override
  public void enableFabText(boolean enableFabText) {
    // TODO(jliarte): 9/07/18 whai is this for?!?!?
  }

  @Override
  public void goToRecordOrGallery() {
    navigateTo(GoToRecordOrGalleryActivity.class);
  }

  @Override
  public void updatePlayerVideoListChanged() {
    // Every time video list changed for a movement, timeline is updated by itself in adapter. Only it is needed update player.
    runOnUiThread(() -> {
      updatePlayer();
    });
  }

  @Override
  public void disableEditTextAction() {
    editTextButton.setVisibility(View.GONE);
  }

  private void updatePlayer() {
    Futures.addCallback(updatePlayerVideos(), new FutureCallback<Object>() {
      @Override
      public void onSuccess(@javax.annotation.Nullable Object result) {
        Log.d(LOG_TAG, "player updated, seeking to pos " + currentVideoIndex);
        seekToClip(currentVideoIndex);
      }

      @Override
      public void onFailure(Throwable t) {
        Log.d(LOG_TAG, "player updated, seeking to pos " + currentVideoIndex);
      }
    });
  }

  @Override
  public void showWarningTempFile(ArrayList<Video> failedVideos) {
      runOnUiThread(() -> {
        videonaTimeLine.setFailedVideos(failedVideos);
        warningTranscodingFilesButton.setVisibility(View.VISIBLE);
      });
  }

  @Override
  public void setWarningMessageTempFile(String messageTempFile) {
      runOnUiThread(() -> {
        warningTranscodingFilesMessage = messageTempFile;
        Log.d(LOG_TAG, "Error detected in videos intermediate files: " + messageTempFile);
      });
  }

  @Override
    public void newClipPlayed(int currentClipIndex) {
        Log.d(LOG_TAG, "newClipPlayed");
        //super needed, avoid recursion
        super.newClipPlayed(currentClipIndex);
        currentVideoIndex = currentClipIndex;
        videonaTimeLine.setSelectedClip(currentClipIndex);
        videonaTimeLine.scrollToPosition(currentClipIndex);
    }

  @Override
    public void onBackPressed() {
        navigateTo(RecordCamera2Activity.class);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.onBackPressed();
                return true;
            default:
                return false;
        }
    }

}
