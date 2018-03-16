/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.domain.editor;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * This class is used to add a new videos to the project.
 */
// TODO(jliarte): 22/10/16 refactor this class to have a unique insert point. Get rid of event bus
public class AddVideoToProjectUseCase {
    protected ProjectRepository projectRepository;
    private ApplyAVTransitionsUseCase applyAVTransitionsUseCase;

    /**
     * Default Constructor with project repository argument.
     *  @param projectRepository the project repository
     * @param applyAVTransitionsUseCase
     */
    @Inject public AddVideoToProjectUseCase(
            ProjectRepository projectRepository,
            ApplyAVTransitionsUseCase applyAVTransitionsUseCase) {
        this.projectRepository = projectRepository;
        this.applyAVTransitionsUseCase = applyAVTransitionsUseCase;
    }

    public void addVideoToTrack(Project currentProject, String videoPath) {
        Video videoToAdd = new Video(videoPath, Video.DEFAULT_VOLUME);
        addVideoToTrack(currentProject, videoToAdd);
        checkIfVideoNeedAVTransitionTempFile(videoToAdd, currentProject);
    }

    private void addVideoToTrack(Project currentProject, Video video) {
        try {
            Track mediaTrack = currentProject.getMediaTrack();
            mediaTrack.insertItem(video);
            projectRepository.update(currentProject);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            //TODO manejar error
        }
    }

    public void addVideoToProjectAtPosition(Project currentProject, Video video, int position,
                                            OnAddMediaFinishedListener listener) {
        try {
            Track mediaTrack = currentProject.getMediaTrack();
            mediaTrack.insertItemAt(position, video);
            video.addListener(currentProject);
            projectRepository.update(currentProject);
            listener.onAddMediaItemToTrackSuccess(video);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }

    public void addVideoListToTrack(Project currentProject, List<Video> videoList,
                                    OnAddMediaFinishedListener listener) {
        try {

            Track mediaTrack = currentProject.getMediaTrack();
            for (Video video : videoList) {
                mediaTrack.insertItem(video);
                checkIfVideoNeedAVTransitionTempFile(video, currentProject);
            }
            projectRepository.update(currentProject);
            listener.onAddMediaItemToTrackSuccess(null);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }

    private void checkIfVideoNeedAVTransitionTempFile(Video videoToAdd, Project currentProject) {
        if (currentProject.getVMComposition().isAudioFadeTransitionActivated()
                || currentProject.getVMComposition().isVideoFadeTransitionActivated()) {
//            listener.videoToLaunchAVTransitionTempFile(videoToAdd,
//                    currentProject.getProjectPathIntermediateFileAudioFade());
            applyAVTransitions(videoToAdd, currentProject);
        }
    }

    private void applyAVTransitions(Video video, Project currentProject) {
        video.setTempPath(currentProject.getProjectPathIntermediateFiles());

        VideonaFormat videoFormat = currentProject.getVMComposition().getVideoFormat();
        Drawable drawableFadeTransitionVideo = currentProject.getVMComposition()
                .getDrawableFadeTransitionVideo();

        applyAVTransitionsUseCase.applyAVTransitions(drawableFadeTransitionVideo, video,
                videoFormat, currentProject.getProjectPathIntermediateFileAudioFade(),
                new ApplyAVTransitionsUseCase.AVTransitionsApplierListener() {
                    @Override
                    public void onSuccessApplyAVTransitions(Video video) {
                        // TODO(jliarte): 31/08/17 implement this method
                    }

                    @Override
                    public void onErrorApplyAVTransitions(Video video, String message) {
                        // TODO(jliarte): 31/08/17 implement this method
                    }
                });
    }
}
