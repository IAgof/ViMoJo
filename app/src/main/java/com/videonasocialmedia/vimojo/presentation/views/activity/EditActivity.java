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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.roughike.bottombar.BottomBar;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.EditPresenter;

import com.videonasocialmedia.vimojo.presentation.mvp.views.EditActivityView;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.VideoTimeLineAdapter;
import com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.helper.VideoTimeLineTouchHelperCallback;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideoTimeLineRecyclerViewClickListener;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;
import com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.split.presentation.views.activity.VideoSplitActivity;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.trim.presentation.views.activity.VideoTrimActivity;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FabUtils;


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
        VideoTimeLineRecyclerViewClickListener {
    private static String LOG_TAG = EditActivity.class.getCanonicalName();
  private final int NUM_COLUMNS_GRID_TIMELINE_HORIZONTAL = 3;
    private final int NUM_COLUMNS_GRID_TIMELINE_VERTICAL = 4;
    private final String THEME_DARK = "dark";

    @Inject EditPresenter editPresenter;

  private final int ID_BUTTON_FAB_TOP=1;
  private final int ID_BUTTON_FAB_CENTER=2;

    @Nullable @BindView(R.id.button_edit_duplicate)
    ImageButton editDuplicateButton;
    @Nullable @BindView(R.id.button_edit_trim)
    ImageButton editTrimButton;
    @Nullable @BindView(R.id.button_edit_split)
    ImageButton editSplitButton;
    @Nullable @BindView(R.id.button_edit_add_text)
    ImageButton editTextButton;
    @Nullable @BindView(R.id.recyclerview_editor_timeline)
    RecyclerView videoListRecyclerView;
    @Nullable @BindView(R.id.fab_edit_room)
    FloatingActionsMenu fabMenu;
    @Nullable @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @Nullable @BindView(R.id.relative_layout_activity_edit)
    RelativeLayout relativeLayoutActivityEdit;
    @Nullable @BindView(R.id.button_edit_warning_transcoding_file)
    ImageButton warningTranscodingFilesButton;

    private List<Video> videoList;
    private int currentVideoIndex = 0;
    private VideoTimeLineAdapter timeLineAdapter;
    private int selectedVideoRemovePosition;
    private FloatingActionButton newFab;

  private String warningTranscodingFilesMessage;
  private ProgressDialog initProgressDialog;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateLinearLayout(R.id.container_layout,R.layout.activity_edit);
        inflateLinearLayout(R.id.container_navigator,R.layout.edit_activity_layout_button_navigator);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);
        if (savedInstanceState != null) {
            this.currentVideoIndex = savedInstanceState.getInt(Constants.CURRENT_VIDEO_INDEX);
          }
        setupBottomBar(bottomBar);
        setupFabMenu();
        setupActivityButtons();
        initBarProgressDialog();
      }

  private void initBarProgressDialog() {
    initProgressDialog = new ProgressDialog(EditActivity.this, R.style.VideonaDialog);
    initProgressDialog.setTitle(R.string.dialog_title_export_project);
    initProgressDialog.setMessage(getString(R.string.dialog_message_export_project));
    initProgressDialog.setProgressStyle(initProgressDialog.STYLE_HORIZONTAL);
    initProgressDialog.setIndeterminate(true);
    initProgressDialog.setProgressNumberFormat(null);
    initProgressDialog.setProgressPercentFormat(null);
    initProgressDialog.setCanceledOnTouchOutside(false);
    initProgressDialog.setCancelable(false);
  }

  private void setupActivityButtons() {
    String currentTheme= editPresenter.getCurrentTheme();
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
        initVideoListRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constants.CURRENT_VIDEO_INDEX)) {
                this.currentVideoIndex = getIntent().getIntExtra(
                        Constants.CURRENT_VIDEO_INDEX, 0);
            }
        }
        bottomBar.selectTabWithId(R.id.tab_editactivity);
        editPresenter.init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initVideoListRecycler() {
        int orientation = LinearLayoutManager.VERTICAL;
        int num_grid_columns = NUM_COLUMNS_GRID_TIMELINE_VERTICAL;
        if (isLandscapeOriented()) {
            num_grid_columns = NUM_COLUMNS_GRID_TIMELINE_HORIZONTAL;
        }
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(
                this, num_grid_columns, orientation, false);
        videoListRecyclerView.setLayoutManager(layoutManager);
        timeLineAdapter = new VideoTimeLineAdapter(this);
        videoListRecyclerView.setAdapter(timeLineAdapter);

      VideoTimeLineTouchHelperCallback callback =
              new VideoTimeLineTouchHelperCallback(timeLineAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(videoListRecyclerView);
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

    ////// RECYCLER VIDEO TIME LINE
    @Override
    public void onClipClicked(int position) {
        setSelectedClip(position);
    }

    public void setSelectedClip(int position) {
      Log.d(LOG_TAG, "setSelectedClip");
        currentVideoIndex = position;
        super.seekToClip(position);
        timeLineAdapter.updateSelection(position);
    }

    @Override
    public void onClipLongClicked(int adapterPosition) {
        super.pausePreview();
        currentVideoIndex = adapterPosition;
    }

    @Override
    public void onClipRemoveClicked(int position) {
        selectedVideoRemovePosition = position;
        final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    setSelectedClipIndex(Math.max(selectedVideoRemovePosition - 1, 0));
                    editPresenter.removeVideoFromProject(selectedVideoRemovePosition);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
        builder.setMessage(R.string.dialog_edit_remove_message).setPositiveButton(R.string.dialog_edit_remove_accept, dialogClickListener)
                .setNegativeButton(R.string.dialog_edit_remove_cancel, dialogClickListener).show();
    }

    private void setSelectedClipIndex(int selectedIndex) {
      Log.d(LOG_TAG, "setSelectedClipIndex");
        this.currentVideoIndex = selectedIndex;
        timeLineAdapter.updateSelection(selectedIndex);
    }

    @Override
    public void onClipMoved(int fromPosition, int toPosition) {
        currentVideoIndex = toPosition;
        editPresenter.moveItem(fromPosition, toPosition);
    }

    @Override
    public void onClipReordered(int newPosition) {
        super.updatePreviewTimeLists();
        super.seekToClip(currentVideoIndex);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

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
        videoList = retrievedVideoList;
        timeLineAdapter.updateVideoList(retrievedVideoList);
      });
    }

    @Override
    public void enableEditActions() {
        editTrimButton.setEnabled(true);
        editSplitButton.setEnabled(true);
        editTextButton.setEnabled(true);
        editDuplicateButton.setEnabled(true);
    }

    @Override
    public void disableEditActions() {
        editTrimButton.setEnabled(false);
        editSplitButton.setEnabled(false);
        editTextButton.setEnabled(false);
        editDuplicateButton.setEnabled(false);
  }

  @Override
  public void enableBottomBar() {
    bottomBar.getTabWithId(R.id.tab_sound).setEnabled(true);
    bottomBar.getTabWithId(R.id.tab_share).setEnabled(true);
  }

  @Override
  public void disableBottomBar() {
    bottomBar.getTabWithId(R.id.tab_sound).setEnabled(false);
    bottomBar.getTabWithId(R.id.tab_share).setEnabled(false);
  }

  @Override
  public void changeAlphaBottomBar(float alpha) {
    bottomBar.getTabWithId(R.id.tab_sound).setAlpha(alpha);
    bottomBar.getTabWithId(R.id.tab_share).setAlpha(alpha);
  }


    @Override
    public void showDialogMediasNotFound() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.VideonaDialog);
        dialog.setTitle(R.string.titleVideosNotFound);
        dialog.setMessage(getString(R.string.messageVideosNotFound));
        dialog.setNeutralButton(getString(R.string.ok), (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }

  @Override
  public void enableFabText(boolean enableFabText) {
  }

  @Override
  public void goToRecordOrGallery() {
    navigateTo(GoToRecordOrGalleryActivity.class);
  }

  @Override
  public void updatePlayerAndTimelineVideoListChanged() {
    runOnUiThread(() -> {
      editPresenter.init();
      obtainVideos();
    });
  }

  @Override
  public void updateTimeLineClipSelected() {
    runOnUiThread(() -> {
        timeLineAdapter.updateSelection(currentVideoIndex);
        videoListRecyclerView.scrollToPosition(currentVideoIndex);
    });
  }

  @Override
  public void showWarningTempFile(ArrayList<Video> failedVideos) {
    timeLineAdapter.setFailedVideos(failedVideos);
    for (Video failedVideo : failedVideos) {
      Log.e(LOG_TAG, "failed video " + videoList.indexOf(failedVideo));
    }
    warningTranscodingFilesButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void setWarningMessageTempFile(String messageTempFile) {
    warningTranscodingFilesMessage = messageTempFile;
    Log.d(LOG_TAG, "Error detected in videos intermediate files: " + messageTempFile);
  }

  @Override
    public void newClipPlayed(int currentClipIndex) {
        Log.d(LOG_TAG, "newClipPlayed");
        super.newClipPlayed(currentClipIndex);
        currentVideoIndex = currentClipIndex;
        timeLineAdapter.updateSelection(currentClipIndex);
        videoListRecyclerView.scrollToPosition(currentClipIndex);
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
