package com.videonasocialmedia.vimojo.model.entities.editor;

/**
 * Created by alvaro on 13/12/16.
 */

public class LastVideoExported {

  private String pathLastVideoExported;
  private String dateLastVideoExported;

  public LastVideoExported(String pathLastVideoExported, String dateLastVideoExported) {
    this.pathLastVideoExported = pathLastVideoExported;
    this.dateLastVideoExported = dateLastVideoExported;
  }

  public String getPathLastVideoExported() {
    return pathLastVideoExported;
  }

  public String getDateLastVideoExported() {
    return dateLastVideoExported;
  }
}
