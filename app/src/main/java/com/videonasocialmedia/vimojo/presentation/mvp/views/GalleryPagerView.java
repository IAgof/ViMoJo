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

import java.util.ArrayList;

/**
 * This interface is used to update the track view in the editor activity.
 */
public interface GalleryPagerView {

    /**
     * Navigates to other activity.
     */
    void navigate();

    void showDialogVideosNotAddedFromGallery(ArrayList<Integer> listVideoId);
}
