package com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity;

/**
 *
 */

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.victor.loading.book.BookLoading;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.GalleryProjectListPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectClickListener;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.adapter.GalleryProjectListAdapter;
import com.videonasocialmedia.vimojo.presentation.views.activity.GoToRecordOrGalleryActivity;
import com.videonasocialmedia.vimojo.repository.DataPersistanceType;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryProjectListActivity extends VimojoActivity implements GalleryProjectListView,
    GalleryProjectClickListener {
  @Inject GalleryProjectListPresenter presenter;

  @BindView(R.id.bookloading_view)
  BookLoading loadingView;
  @BindView(R.id.recycler_gallery_project)
  RecyclerView projectList;
  @Nullable
  @BindView(R.id.text_dialog)
  EditText editTextDialog;

  private GalleryProjectListAdapter projectAdapter;
  private BroadcastReceiver completionReceiver;
  private ProgressDialog updateAssetsProgressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery_project);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    initProjectListRecycler();
    initUpdateAssetsProgressDialog();
  }

  @Override
  public void onResume() {
    super.onResume();
    presenter.updateProjectList();
  }

  private void initProjectListRecycler() {
    projectAdapter = new GalleryProjectListAdapter();
    projectAdapter.setRetrieveProjectClickListener(this);
    presenter.init();
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    projectList.setLayoutManager(layoutManager);
    projectList.setAdapter(projectAdapter);
  }

  private void initUpdateAssetsProgressDialog() {
    updateAssetsProgressDialog = new ProgressDialog(GalleryProjectListActivity.this,
            R.style.VideonaDialog);
    updateAssetsProgressDialog.setTitle(R.string.dialog_title_update_assets_progress_dialog);
    updateAssetsProgressDialog.setMessage(getString(R.string.dialog_message_update_assets_progress_dialog));
    updateAssetsProgressDialog.setProgressStyle(updateAssetsProgressDialog.STYLE_HORIZONTAL);
    updateAssetsProgressDialog.setIndeterminate(true);
    updateAssetsProgressDialog.setProgressNumberFormat(null);
    updateAssetsProgressDialog.setProgressPercentFormat(null);
    updateAssetsProgressDialog.setCanceledOnTouchOutside(false);
    updateAssetsProgressDialog.setCancelable(false);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterFileUploadReceiver();
  }

  @Override
  public void navigateTo(Class cls) {
    Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
    startActivity(intent);
  }

  @Override
  public void navigateTo(Class cls, String videoToSharePath) {
    Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
    intent.putExtra(Constants.VIDEO_TO_SHARE_PATH, videoToSharePath);
    startActivity(intent);
  }

  @Override
  public void showLoading() {
    runOnUiThread(() -> {
      projectList.setVisibility(View.GONE);
      loadingView.start();
      loadingView.setVisibility(View.VISIBLE);
    });
  }

  @Override
  public void hideLoading() {
    runOnUiThread(() -> {
      loadingView.stop();
      loadingView.setVisibility(View.GONE);
      projectList.setVisibility(View.VISIBLE);
    });
  }

  @Override
  public void registerFileUploadReceiver(BroadcastReceiver completionReceiver) {
    this.completionReceiver = completionReceiver;
    registerReceiver(completionReceiver,
            new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
  }

  @Override
  public void unregisterFileUploadReceiver() {
    if (this.completionReceiver != null) {
      unregisterReceiver(this.completionReceiver);
    }
  }

  @Override
  public void showUpdateAssetsProgressDialog() {
    updateAssetsProgressDialog.show();
  }

  @Override
  public void hideUpdateAssetsProgressDialog() {
    updateAssetsProgressDialog.hide();
  }

  @Override
  public void updateUpdateAssetsProgressDialog(int remaining) {
    updateAssetsProgressDialog.setMessage(
            getString(R.string.dialog_message_update_assets_progress_dialog) + "\n " + remaining + " "
                    + getString(R.string.update_assets_remaining));
  }

  @Override
  public void showProjectList(List<Project> projectList) {
    runOnUiThread(() -> {
      projectAdapter.setProjectList(projectList);
      projectAdapter.notifyDataSetChanged();
      this.projectList.setAdapter(projectAdapter);
    });
  }

  @Override
  public void createDefaultProject() {
    // TODO(jliarte): 20/04/18 review this workflow
    // TODO(jliarte): 20/04/18 generic transition drawable to allow change in build phase?
    Drawable drawableFadeTransitionVideo = getDrawable(R.drawable.alpha_transition_white);
    presenter.createNewProject(Constants.PATH_APP, Constants.PATH_APP_ANDROID,
            drawableFadeTransitionVideo);
    navigateTo(GoToRecordOrGalleryActivity.class);
  }

  @Override
  public void onClick(Project project) {
    // Go to detail project info activity
  }

  @Override
  public void onDuplicateProject(Project project) {
    presenter.duplicateProject(project);
  }

  @Override
  public void onDeleteProject(final Project project) {
    showDeleteConfirmDialog(project);
  }

  private void showDeleteConfirmDialog(Project project) {
    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
      if (which == DialogInterface.BUTTON_POSITIVE) {
        presenter.deleteProject(project);
      } else if (which == DialogInterface.BUTTON_NEUTRAL) {
        presenter.deleteLocalProject(project);
      }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog)
            .setMessage(R.string.dialog_project_remove_message)
            .setPositiveButton(R.string.dialog_project_remove_accept, dialogClickListener)
            .setNegativeButton(R.string.dialog_project_remove_cancel, dialogClickListener);
    if (project.getDataPersistanceType() != DataPersistanceType.API) {
      builder.setNeutralButton(R.string.dialog_project_remove_local_only, dialogClickListener);
    }
    builder.show();
  }

  @Override
  public void goToEditActivity(Project project) {
    presenter.goToEdit(project);
  }

  @Override
  public void goToShareActivity(Project project) {
    presenter.goToShare(project);
  }

  @Override
  public void goToDetailActivity(Project project) {
    presenter.goToDetailProject(project);
  }

  @OnClick(R.id.backButton)
  public void onClickBackButton() {
    super.onBackPressed();
  }

}





