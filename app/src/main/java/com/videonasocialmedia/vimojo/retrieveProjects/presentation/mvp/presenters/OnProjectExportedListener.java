package com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.presenters;

/**
 * Created by alvaro on 20/12/16.
 */
public interface OnProjectExportedListener {
  void videoExported(String videoPath);
  void exportNewVideo();
}
