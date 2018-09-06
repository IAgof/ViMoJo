package com.videonasocialmedia.vimojo.presentation.mvp.views;

/**
 * Created by ruth on 23/11/16.
 */

public interface EditorActivityView {

    void showError(int causeTextResource);

    void showMessage(int stringToast);

    void restartActivity(Class clas);

    void itemDarkThemePurchased();

    void itemWatermarkPurchased();

    void showWatermarkSwitch(boolean watermarkIsSelected);

    void hideWatermarkSwitch();

    void setDefaultIconsForStoreItems();

    void setLockIconsForStoreItems();

    void hideVimojoStoreViews();

    void deactivateDarkTheme();

    void activateWatermark();

    void setHeaderViewCurrentProject(String pathThumbProject, String projectName, String projectDate);

    void goToRecordOrGalleryScreen();

    void hideLinkToVimojoPlatform();

    void hideTutorialViews();

  void setAspectRatioVerticalVideos();
}
