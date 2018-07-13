package com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity;

/**
 *
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.GalleryProjectListPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectListView;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.GalleryProjectClickListener;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.adapter.GalleryProjectListAdapter;
import com.videonasocialmedia.vimojo.presentation.views.activity.GoToRecordOrGalleryActivity;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryProjectListActivity extends VimojoActivity implements GalleryProjectListView,
    GalleryProjectClickListener {

  @Inject GalleryProjectListPresenter presenter;

  @BindView(R.id.recycler_gallery_project)
  RecyclerView projectList;
  @Nullable
  @BindView(R.id.text_dialog)
  EditText editTextDialog;

  private GalleryProjectListAdapter projectAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery_project);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    initProjectListRecycler();
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

  @Override
  public void onResume() {
    super.onResume();
    presenter.updateProjectList();
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
    presenter.createNewProject(Constants.PATH_APP, Constants.PATH_APP_ANDROID, drawableFadeTransitionVideo);
    //presenter.updateProjectList();
    navigateTo(GoToRecordOrGalleryActivity.class);
  }

  @Override
  public void onClick(Project project) {
    // Go to detail project info activity
  }

  @Override
  public void onDuplicateProject(Project project) {
    try {
      Futures.addCallback(presenter.duplicateProject(project), new FutureCallback<Void>() {
        @Override
        public void onSuccess(@javax.annotation.Nullable Void result) {
          presenter.updateProjectList();
        }

        @Override
        public void onFailure(Throwable t) {
          t.printStackTrace();
          presenter.updateProjectList(); // TODO(jliarte): 13/07/18 needed? presenter.onErrorDuplicating -> show error msg!
        }
      });
    } catch (IllegalItemOnTrack illegalItemOnTrack) {
      illegalItemOnTrack.printStackTrace();
    }
  }

  @Override
  public void onDeleteProject(final Project project) {

    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            presenter.deleteProject(project);
            presenter.updateProjectList();
        }
      }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    builder.setMessage(R.string.dialog_project_remove_message)
        .setPositiveButton(R.string.dialog_project_remove_accept, dialogClickListener)
        .setNegativeButton(R.string.dialog_project_remove_cancel, dialogClickListener).show();

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





