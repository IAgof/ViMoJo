package com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.vimojo.settings.licensesVimojo.domain.GetLicenseVimojoListUseCase;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseDetailView;

import java.util.List;

import javax.inject.Inject;

/**
 *
 */

public class LicenseDetailPresenter {

  private final Context context;
  private final LicenseDetailView licenseDetailView;
  private LicenseVimojo licenseSelected;
  private final List<LicenseVimojo> licenseList;

  @Inject public LicenseDetailPresenter (LicenseDetailView licenseDetailView, Context context, GetLicenseVimojoListUseCase getLicenseUseCase){
    this.context=context;
    licenseList = getLicenseUseCase.getLicenceList();
    this.licenseDetailView=licenseDetailView;
  }

  public void init(String idLicense) {
    licenseSelected = retrieveLicense(idLicense);
    if (licenseSelected!=null){
      setContentAndTitleLicense(licenseSelected);
    }
  }

  public LicenseVimojo retrieveLicense(String idLicense) {
    LicenseVimojo result = null;
    for (LicenseVimojo license : licenseList) {
      if (idLicense.equals(license.getIdLicenseVimojo())) {
        result = license;
      }
    }
    return result;
  }

  public void setContentAndTitleLicense(LicenseVimojo licenseSelected) {
    licenseDetailView.setContentLicense(licenseSelected);
    licenseDetailView.setTitleToolbar (licenseSelected);
  }

}
