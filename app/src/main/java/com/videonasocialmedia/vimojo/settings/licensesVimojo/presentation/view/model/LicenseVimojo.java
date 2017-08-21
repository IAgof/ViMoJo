package com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.model;


/**
 *
 */

public class LicenseVimojo {

  private String idLicenseVimojo;
  private String licenseContent;

  public LicenseVimojo(String idLicense, String licenseContent){
    this.idLicenseVimojo =idLicense;
    this.licenseContent=licenseContent;
  }

  public String getIdLicenseVimojo() {
    return idLicenseVimojo;
  }

  public String getLicenseContent() {
    return licenseContent;
  }

}
