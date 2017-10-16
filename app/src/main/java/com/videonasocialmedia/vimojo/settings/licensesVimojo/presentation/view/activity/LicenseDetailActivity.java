package com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters.LicenseDetailPresenter;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseDetailView;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */

public class LicenseDetailActivity extends VimojoActivity implements LicenseDetailView {

  @Bind(R.id.toolbar_license_title)
  TextView toolbarTitle;
  @Bind (R.id.license_content)
  TextView licenseContent;
  private Toolbar toolbar;

  private String idLicense;

  @Inject LicenseDetailPresenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_detail_licenses);
    ButterKnife.bind(this);
    initToolbar();
    getActivityPresentersComponent().inject(this);
  }

  private void initToolbar() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
  }

  @Override
  protected void onResume() {
    super.onResume();
    try {
      Bundle extras = this.getIntent().getExtras();
      idLicense = extras.getString(IntentConstants.LICENSE_SELECTED);
    } catch (Exception e) {
    }
    presenter.init(idLicense);
  }

  @Override
  public void setContentLicense(LicenseVimojo license) {
    licenseContent.setText(license.getLicenseContent());
  }

  @Override
  public void setTitleToolbar(LicenseVimojo license) {
    toolbarTitle.setText(license.getIdLicenseVimojo());
  }

  public void navigateTo(Class cls) {
    Intent intent = new Intent(VimojoApplication.getAppContext(), cls);
    startActivity(intent);
  }
}