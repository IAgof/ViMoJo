package com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views;

import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;

import java.util.List;

/**
 *
 */

public interface LicenseListView {
  void showLicenseList(List<LicenseVimojo> licenseVimojoList);
}
