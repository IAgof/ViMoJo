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

  private void populateLicenseList() {
    licenseList.add(new LicenseVimojo("Licencias de código abierto", context.getString(R.string.licenseContent)));
    licenseList.add(new LicenseVimojo("Licencias de diseño", context.getString(R.string.licenseDesign)));
    licenseList.add(new LicenseVimojo("Licencias de música", context.getString(R.string.licenseMusic)));

  }
}
