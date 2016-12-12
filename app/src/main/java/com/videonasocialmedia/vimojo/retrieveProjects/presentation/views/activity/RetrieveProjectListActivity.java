package com.videonasocialmedia.vimojo.retrieveProjects.presentation.views.activity;

/**
 *
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;

import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.presenters.RetrieveProjectListPresenter;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views.RetrieveProjectListView;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views.RetrieveProjectClickListener;
import com.videonasocialmedia.vimojo.retrieveProjects.presentation.views.adapter.RetrieveProjectListAdapter;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RetrieveProjectListActivity extends VimojoActivity implements RetrieveProjectListView,
    RetrieveProjectClickListener, VideonaPlayer.VideonaPlayerListener {
    private static final String MUSIC_LIST_PROJECT_POSITION = "music_list_project_position";

    @Bind(R.id.recycler_retrieve_project)
    RecyclerView projectList;

    private RetrieveProjectListPresenter presenter;
    private RetrieveProjectListAdapter projectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_project);
        ButterKnife.bind(this);
        setupToolbar();
        presenter = new RetrieveProjectListPresenter(this);
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
        presenter.getAvailableMusic();
        LinearLayoutManager layoutManager =
            new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        projectList.setLayoutManager(layoutManager);
        projectList.setAdapter(projectAdapter);
    }


    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
    }

    @Override
    public void newClipPlayed(int i) {

    }

    @Override
    public void showProjectList(List<Project> projectList) {
        projectAdapter.setProjectList(projectList);

    }

    @Override
    public void onClick(Project project) {

    }

}





