/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.views.listener;

/**
 * Created by jca on 25/3/15.
 */
public interface VideoTimeLineRecyclerViewClickListener {

    void onClipClicked(int position);

    void onClipLongClicked(int adapterPosition);

    void onClipRemoveClicked(int position);

    void onClipMoved(int fromPosition, int toPosition);

    void onClipReordered(int newPosition);
}
