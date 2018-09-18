/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.init.presentation.mvp.views;

public interface InitAppView {

    public void navigate(Class<?> cls);

    void screenOrientationPortrait();

    void screenOrientationLandscape();

  void initializeAdMob();

  void showDialogOutOfDate();

  void appContinueWorkflow();
}
