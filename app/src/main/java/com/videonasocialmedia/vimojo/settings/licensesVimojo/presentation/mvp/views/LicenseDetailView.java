package com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views;

import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;

/**
 * Created by ruth on 20/08/17.
 */

public interface LicenseDetailView {

  void setContentLicense(LicenseVimojo license);

  void setTitleToolbar(LicenseVimojo licenseSelected);
}
