package com.videonasocialmedia.vimojo.retrieveProjects.presentation.mvp.views;

/**
 * Created by alvaro on 19/12/16.
 */

public interface DetailProjectView {

  void showDetailProjectThumb(String path);
  void showTitleProject(String title);
  void showDetailProjectInfo(int duration, double projectSizeMbVideoToExport, int width,
                             double videoBitRate, int frameRate);
}
