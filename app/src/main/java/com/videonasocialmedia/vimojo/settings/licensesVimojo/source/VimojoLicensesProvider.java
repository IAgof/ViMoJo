package com.videonasocialmedia.vimojo.settings.licensesVimojo.source;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class VimojoLicensesProvider {

  private final Context context;
  private List<LicenseVimojo> licenseList = new ArrayList();

  public VimojoLicensesProvider(Context context) {
    this.context = context;
    populateLicenseList();
  }

  public List<LicenseVimojo> getAll() {
    return licenseList;
  }

  public void populateLicenseList() {
    licenseList.add(new LicenseVimojo("License 1", context.getString(R.string.licenseContent)));
    licenseList.add(new LicenseVimojo("License 2", context.getString(R.string.licenseContent)));
    }
}
