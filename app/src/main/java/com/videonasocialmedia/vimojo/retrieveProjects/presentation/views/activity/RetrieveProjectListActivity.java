package com.videonasocialmedia.vimojo.retrieveProjects.presentation.views.activity;

/**
 *
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.presenters.RetrieveProjectListPresenter;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views.RetrieveProjectListView;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views.RetrieveProjectClickListener;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.views.adapter.RetrieveProjectListAdapter;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RetrieveProjectListActivity extends VimojoActivity implements RetrieveProjectListView,
    RetrieveProjectClickListener {

  @Bind(R.id.recycler_retrieve_project)
  RecyclerView projectList;

  private RetrieveProjectListPresenter presenter;
  private RetrieveProjectListAdapter projectAdapter;
  private SharedPreferences sharedPreferences;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_retrieve_project);
    ButterKnife.bind(this);
    setupToolbar();
    sharedPreferences = getSharedPreferences(ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
        Context.MODE_PRIVATE);
    presenter = new RetrieveProjectListPresenter(this, sharedPreferences);
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
    projectAdapter = new RetrieveProjectListAdapter();
    projectAdapter.setRetrieveProjectClickListener(this);
    presenter.getAvailableProjects();
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    projectList.setLayoutManager(layoutManager);
    projectList.setAdapter(projectAdapter);
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
  public void onClick(Project project) {
    // Go to detail project info activity
  }

  @Override
  public void onDuplicateProject(Project project) {
    presenter.duplicateProject(project);
    presenter.updateProjectList();
  }

  @Override
  public void onDeleteProject(Project project) {
    presenter.deleteProject(project);
    presenter.updateProjectList();
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

}





