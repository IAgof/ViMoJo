package com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity;

/**
 *
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
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

public class GalleryProjectListActivity extends VimojoActivity implements GalleryProjectListView,
    GalleryProjectClickListener {

  @Inject GalleryProjectListPresenter presenter;

  @BindView(R.id.recycler_gallery_project)
  RecyclerView projectList;

  private GalleryProjectListAdapter projectAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery_project);
    ButterKnife.bind(this);
    setupToolbar();
    getActivityPresentersComponent().inject(this);
    initProjectListRecycler();
  }

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
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
  public void onResume(){
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
    projectAdapter.setProjectList(projectList);
    projectAdapter.notifyDataSetChanged();
    this.projectList.setAdapter(projectAdapter);
  }

  @Override
  public void createDefaultProject() {
    presenter.createNewDefaultProject(Constants.PATH_APP, Constants.PATH_APP_ANDROID);
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
      presenter.duplicateProject(project);
      presenter.updateProjectList();
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
    presenter.updateCurrentProject(project);
    navigateTo(EditActivity.class);
  }

  @Override
  public void goToShareActivity(Project project) {
    presenter.checkNavigationToShare(project);

  }

  @Override
  public void goToDetailActivity(Project project) {
    presenter.updateCurrentProject(project);
    navigateTo(DetailProjectActivity.class);
  }

}





