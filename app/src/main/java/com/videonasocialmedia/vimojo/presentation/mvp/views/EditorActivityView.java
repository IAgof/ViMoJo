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

    void watermarkFeatureAvailable();

    void hideWatermarkSwitch();

    void setIconsFeatures();

    void setIconsPurchaseInApp();

    void hideVimojoStoreViews();

    void deactivateDarkTheme();

    void activateWatermark();

    void setHeaderViewCurrentProject(String pathThumbProject, String projectName, String projectDate);

    void goToRecordOrGalleryScreen();
}
