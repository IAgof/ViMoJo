/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;

import java.util.List;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter implements OnAddMediaFinishedListener,
        OnRemoveMediaFinishedListener, OnExportFinishedListener {

    RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
    RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    AddVideoToProjectUseCase addVideoToProjectUseCase;
    GalleryPagerView galleryPagerView;
    ExportProjectUseCase exportProjectUseCase;
    private boolean exported = false;

    /**
     * Constructor.
     */
    public GalleryPagerPresenter(GalleryPagerView galleryPagerView) {
        this.galleryPagerView = galleryPagerView;
        removeVideoFromProjectUseCase = new RemoveVideoFromProjectUseCase();
        removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
    }

    /**
     * This method is used to add new videos to the actual track.
     *
     * @param video the path of the new video which user wants to add to the project
     */
    public void loadVideoToProject(Video video) {
       // resetProject();
        addVideoToProjectUseCase.addVideoToTrack(video, this);
    }

    public void loadVideoListToProject(List<Video> videoList) {
       // resetProject();
        //exported= false;
        addVideoToProjectUseCase.addVideoListToTrack(videoList, this);
    }


    @Override
    public void onRemoveMediaItemFromTrackError() {

    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {
    }

    @Override
    public void onAddMediaItemToTrackError() {

    }

    /*

    ÑAPA

     */

    @Override
    public void onAddMediaItemToTrackSuccess(Media video) {
        galleryPagerView.navigate();
        /*
        if (exported)
            galleryPagerView.navigate();
        else {
            exportProjectUseCase = new ExportProjectUseCase(this);
            exportProjectUseCase.export();
        }
        */
    }

    @Override
    public void onExportError(String error) {

    }

    @Override
    public void onExportSuccess(Video video) {
        //exported=true;
        loadVideoToProject(video);
    }
}
