package com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views;

import java.util.List;

/**
 * Created by alvaro on 19/12/16.
 */

public interface DetailProjectView {

    void showTitleProject(String title);

    void showDetailProjectInfo(int duration, double projectSizeMbVideoToExport, int width,
                               double videoBitRate, int frameRate);

    void showAcceptTitleButton();

    void hideAcceptTitleButton();

    void showAcceptDescriptionButton();

    void hideAcceptDescriptionButton();

    void expandDetailsInfo();

    void shrinkDetailsInfo();

    void showDescriptionProject(String description);

    void showProductTypeMultipleDialog(boolean[] checkedProductTypes);

  void showProductTypeSelected(List<String> productTypeList);
}
