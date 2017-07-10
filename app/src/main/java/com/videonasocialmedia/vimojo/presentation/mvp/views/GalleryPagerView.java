/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;

import java.util.ArrayList;

/**
 * This interface is used to update the track view in the editor activity.
 */
public interface GalleryPagerView extends NewClipImporter.View {

    /**
     * Navigates to other activity.
     */
    void navigate();

    void showDialogVideosNotAddedFromGallery(ArrayList<Integer> listVideoId);

  @Override
  void hideProgressAdaptingVideo();

  void showError(String string);
}
