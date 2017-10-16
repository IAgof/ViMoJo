package com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.vimojo.settings.licensesVimojo.domain.GetLicenseVimojoListUseCase;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseListView;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;

import java.util.List;

import javax.inject.Inject;

/**
 *
 */

public class LicenseListPresenter {
  private Context context;
  private List<LicenseVimojo> licenseList;
  private LicenseListView licenseListView;
  private GetLicenseVimojoListUseCase getLicenseListUseCase;

  @Inject public LicenseListPresenter(LicenseListView licenseListView, Context context, GetLicenseVimojoListUseCase getLicenseListUseCase) {
    this.context = context;
    licenseList = getLicenseListUseCase.getLicenceList();
    this.licenseListView=licenseListView;
  }

  public void getLicenseList() {
    licenseListView.showLicenseList(licenseList);
  }

}

