/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas Abascal
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditActivityView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class EditPresenter implements OnAddMediaFinishedListener, OnRemoveMediaFinishedListener {
    public static final float VOLUME_MUTE = 0f;
    private final String TAG = getClass().getSimpleName();
    private final Project currentProject;
    // TODO(jliarte): 2/05/17 inject delegate?
    final VideoListErrorCheckerDelegate videoListErrorCheckerDelegate
            = new VideoListErrorCheckerDelegate();
    /**
     * UseCases
     */
    private RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
    private ReorderMediaItemUseCase reorderMediaItemUseCase;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetAudioFromProjectUseCase getAudioFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    /**
     * EditActivity View
     */
    private EditActivityView editActivityView;
    private final VideoTranscodingErrorNotifier videoTranscodingErrorNotifier;
    protected List<Video> videoList;
    protected UserEventTracker userEventTracker;

    @Inject
    public EditPresenter(EditActivityView editActivityView,
                         VideoTranscodingErrorNotifier videoTranscodingErrorNotifier,
                         UserEventTracker userEventTracker,
                         RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase,
                         ReorderMediaItemUseCase reorderMediaItemUseCase,
                         GetAudioFromProjectUseCase getAudioFromProjectUseCase,
                         GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                         GetPreferencesTransitionFromProjectUseCase
                                 getPreferencesTransitionFromProjectUseCase) {
        this.editActivityView = editActivityView;
        this.videoTranscodingErrorNotifier = videoTranscodingErrorNotifier;
        this.removeVideoFromProjectUseCase = removeVideoFromProjectUseCase;
        this.reorderMediaItemUseCase = reorderMediaItemUseCase;
        this.getAudioFromProjectUseCase = getAudioFromProjectUseCase;

        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase = getPreferencesTransitionFromProjectUseCase;
        this.userEventTracker = userEventTracker;

        this.currentProject = loadCurrentProject();
    }

    public Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null, null);
    }

    public String getResolution() {
        // TODO(jliarte): 19/12/16 inject sharedPreferences
        SharedPreferences sharedPreferences = VimojoApplication.getAppContext()
                .getSharedPreferences(
                        ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                        Context.MODE_PRIVATE);

        return sharedPreferences.getString(ConfigPreferences.RESOLUTION, "1280x720");
    }

    public void moveItem(int fromPosition, int toPositon) {
//        Video mediaToMove = videoList.get(fromPosition);
        Video mediaToMove = (Video) currentProject.getMediaTrack().getItems().get(fromPosition);
        reorderMediaItemUseCase.moveMediaItem(mediaToMove, toPositon,
                new OnReorderMediaListener() {
            @Override
            public void onMediaReordered(Media media, int newPosition) {
                // If everything was right the UI is already updated since the user did the
                // reordering over the "model view"
                userEventTracker.trackClipsReordered(currentProject);
            }

            @Override
            public void onErrorReorderingMedia() {
                //The reordering went wrong so we ask the project for the actual video list
                Log.d(TAG, "timeline:  error reordering!!");
                obtainVideos();
            }
        });
    }

    @Override
    public void onAddMediaItemToTrackError() {
        //TODO modify error message
        editActivityView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media media) {
    }

    @Override
    public void onRemoveMediaItemFromTrackError() {
        //TODO modify error message
        editActivityView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {
        editActivityView.updateProject();
    }


    private List<Video> checkMediaPathVideosExistOnDevice(List<Video> videoList) {
        List<Video> checkedVideoList = new ArrayList<>();
        for (int index = 0; index < videoList.size(); index++) {
            Video video = videoList.get(index);
            if (!new File(video.getMediaPath()).exists()) {
                // TODO(jliarte): 26/04/17 notify the user we are deleting items from project!!! FIXME
                ArrayList<Media> mediaToDeleteFromProject = new ArrayList<>();
                mediaToDeleteFromProject.add(video);
                removeVideoFromProjectUseCase.removeMediaItemsFromProject(mediaToDeleteFromProject, this);
                Log.d(TAG, video.getMediaPath() + "not found!! deleting from project");
            } else {
                checkedVideoList.add(video);
            }
        }
        return checkedVideoList;
    }

    public void obtainVideos() {
        editActivityView.showProgressDialog();
        getMediaListFromProjectUseCase.getMediaListFromProject(new OnVideosRetrieved() {
            @Override
            public void onVideosRetrieved(List<Video> videosRetrieved) {
                int sizeOriginalVideoList = videosRetrieved.size();
                List<Video> checkedVideoList = checkMediaPathVideosExistOnDevice(videosRetrieved);
                videoList = checkedVideoList;

                List<Video> videoCopy = new ArrayList<>(checkedVideoList);

                if (sizeOriginalVideoList > checkedVideoList.size()) {
                    editActivityView.showDialogMediasNotFound();
                }
                editActivityView.enableEditActions();
                editActivityView.enableBottomBar();
                editActivityView.enableFabText(true);
                editActivityView.hideProgressDialog();
                editActivityView.bindVideoList(videoCopy);
                videoListErrorCheckerDelegate.checkWarningMessageVideosRetrieved(videoList, videoTranscodingErrorNotifier);
            }

            @Override
            public void onNoVideosRetrieved() {
                editActivityView.disableEditActions();
                editActivityView.disableBottomBar();
                editActivityView.enableFabText(false);
                editActivityView.changeAlphaBottomBar(Constants.ALPHA_DISABLED_BOTTOM_BAR);
                editActivityView.hideProgressDialog();
                editActivityView.showMessage(R.string.add_videos_to_project);
                editActivityView.expandFabMenu();
                editActivityView.resetPreview();
                editActivityView.bindVideoList(Collections.<Video>emptyList());
            }
        });
    }

    public void removeVideoFromProject(int selectedVideoRemove) {
        Video videoToRemove = this.videoList.get(selectedVideoRemove);
        ArrayList<Media> mediaToDeleteFromProject = new ArrayList<>();
        mediaToDeleteFromProject.add(videoToRemove);
        removeVideoFromProjectUseCase.removeMediaItemsFromProject(mediaToDeleteFromProject, this);
    }

    public void init() {
        obtainVideos();
        retrieveMusic();
        retrieveTransitions();
        checkMuteOnTracks();
    }

    private void checkMuteOnTracks() {
        if (currentProject.getVMComposition().hasMusic()) {
            Track musicTrack = currentProject.getAudioTracks()
                .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_MUSIC);
            if (musicTrack.isMuted()) {
                editActivityView.setMusicVolume(VOLUME_MUTE);
            }
        }

        if (currentProject.getVMComposition().hasVoiceOver()) {
            Track voiceOverTrack = currentProject.getAudioTracks()
                .get(com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
            if (voiceOverTrack.isMuted()) {
                editActivityView.setVoiceOverVolume(VOLUME_MUTE);
            }
        }

        if (currentProject.getVMComposition().hasVideos()) {
            Track mediaTrack = currentProject.getMediaTrack();
            if (mediaTrack.isMuted()) {
                editActivityView.setVideoMute();
            }
        }
    }

    private void retrieveTransitions() {
        if(getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated()){
            editActivityView.setVideoFadeTransitionAmongVideos();
        }
        if(getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated() &&
            !currentProject.getVMComposition().hasMusic()){
            editActivityView.setAudioFadeTransitionAmongVideos();
        }
    }

    private void retrieveMusic() {
        if (currentProject.getVMComposition().hasMusic()) {
            getAudioFromProjectUseCase.getMusicFromProject(new GetMusicFromProjectCallback() {
                @Override
                public void onMusicRetrieved(Music music) {
                    editActivityView.setMusic(music);
                }
            });
        }
        if (currentProject.getVMComposition().hasVoiceOver()) {
            getAudioFromProjectUseCase.getVoiceOverFromProject(new GetMusicFromProjectCallback() {
                @Override
                public void onMusicRetrieved(Music voiceOver) {
                    editActivityView.setVoiceOver(voiceOver);
                }
            });
        }
    }
}
