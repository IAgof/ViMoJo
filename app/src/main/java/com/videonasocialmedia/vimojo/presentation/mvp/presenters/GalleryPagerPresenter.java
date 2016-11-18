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

import android.media.MediaMetadataRetriever;

import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter implements OnAddMediaFinishedListener,
        OnRemoveMediaFinishedListener, OnExportFinishedListener {
    
    RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
    AddVideoToProjectUseCase addVideoToProjectUseCase;
    GalleryPagerView galleryPagerView;
    protected Project currentProject;
    ArrayList<Integer> listErrorVideoIds = new ArrayList<>();
    private boolean differentVideoFormat;

    /**
     * Constructor.
     */
    public GalleryPagerPresenter(GalleryPagerView galleryPagerView) {
        this.galleryPagerView = galleryPagerView;
        removeVideoFromProjectUseCase = new RemoveVideoFromProjectUseCase();
        addVideoToProjectUseCase = new AddVideoToProjectUseCase();
        this.currentProject = loadCurrentProject();
    }

    public Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    /**
     * This method is used to add new videos to the actual track.
     *
     * @param video the path of the new video which user wants to add to the project
     */
    public void loadVideoToProject(Video video) {

        addVideoToProjectUseCase.addVideoToTrack(video, this);
    }

    public void loadVideoListToProject(List<Video> videoList) {

        List<Video> checkedVideoList = checkFormatVideoSelected(videoList);

        if(listErrorVideoIds.size() > 0){
            galleryPagerView.showDialogVideosNotAddedFromGallery(listErrorVideoIds);
            differentVideoFormat = true;
        }
        addVideoToProjectUseCase.addVideoListToTrack(checkedVideoList, this);
    }

    public List<Video> checkFormatVideoSelected(List<Video> videoList){

        List<Video> checkedFortmatVideoList = new ArrayList<>();

        VideoResolution videoResolution = currentProject.getProfile().getVideoResolution();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        for (int index = 0; index < videoList.size(); index++) {
            Video video = videoList.get(index);
            try {
                retriever.setDataSource(video.getMediaPath());
                String duration = retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_DURATION);

                int durationInt = Integer.parseInt(duration);
                video.setDuration(durationInt);
                video.setStopTime(durationInt);

                String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                if(width.compareTo(String.valueOf(videoResolution.getWidth())) != 0){
                    listErrorVideoIds.add(index + 1);
                } else {
                    checkedFortmatVideoList.add(video);
                }

            } catch (Exception e) {
                video.setDuration(0);
                e.printStackTrace();
            }
        }

        return checkedFortmatVideoList;
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
        if(!differentVideoFormat)
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
