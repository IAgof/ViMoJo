/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.views;

public interface InitAppView {

    public void navigate(Class<?> cls);

    void screenOrientationPortrait();

    void screenOrientationLandscape();

  void initializeAdMob();

  void showDialogOutOfDate();

  void appContinueWorkflow();
}
