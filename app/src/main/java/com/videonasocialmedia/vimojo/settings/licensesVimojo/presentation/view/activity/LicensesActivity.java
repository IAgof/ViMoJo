/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters.LicenseListPresenter;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseListView;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicensesVimojoClickListener;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.adapter.LicensesVimojoAdapter;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.model.LicenseVimojo;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is used to show the about information.
 *
 * @author vlf
 * @since 04/05/2015
 */
public class LicensesActivity extends VimojoActivity implements LicenseListView, LicensesVimojoClickListener {

  @Inject LicenseListPresenter presenter;

  @Bind(R.id.license_list)
  RecyclerView licenseList;

  private LicensesVimojoAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_licenses);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    initToolbar();
    initLicenseListRecycler();
  }

  private void initToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
  }

  private void initLicenseListRecycler() {
    adapter = new LicensesVimojoAdapter();
    adapter.setLicensesClickListener(this);
    presenter.getLicenseList();
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    licenseList.setLayoutManager(layoutManager);
    licenseList.setAdapter(adapter);
    licenseList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.getLicenseList();
  }

  @Override
  public void onClick(LicenseVimojo licenses) {
    navigateToLicenseDetailActivity(licenses.getIdLicenseVimojo());
  }

  private void navigateToLicenseDetailActivity(String idLicense) {
    Intent intent = new Intent (this, LicenseDetailActivity.class);
    intent.putExtra(IntentConstants.LICENSE_SELECTED,idLicense);
    startActivity(intent);
    finish();
  }

  @Override
    public void showLicenseList(List<LicenseVimojo> licenseVimojoList) {
      adapter.setLicenseList(licenseVimojoList);
    }
}
