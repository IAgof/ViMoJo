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
import android.util.TypedValue;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditActivityView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class EditPresenter extends VimojoPresenter implements OnAddMediaFinishedListener,
    OnRemoveMediaFinishedListener, ElementChangedListener {
    private final String TAG = getClass().getSimpleName();
    private final ProjectInstanceCache projectInstanceCache;
    protected Project currentProject;
    private Context context;
    // TODO(jliarte): 2/05/17 inject delegate?
    final VideoListErrorCheckerDelegate videoListErrorCheckerDelegate
            = new VideoListErrorCheckerDelegate();
    /**
     * UseCases
     */
    private final GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
    private ReorderMediaItemUseCase reorderMediaItemUseCase;
    /**
     * EditActivity View
     */
    private EditActivityView editActivityView;
    private final VideoTranscodingErrorNotifier videoTranscodingErrorNotifier;
    protected UserEventTracker userEventTracker;
    private CompositionApiClient compositionApiClient;

    @Inject
    public EditPresenter(EditActivityView editActivityView,
                         Context context,
                         VideoTranscodingErrorNotifier videoTranscodingErrorNotifier,
                         UserEventTracker userEventTracker,
                         GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                         RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase,
                         ReorderMediaItemUseCase reorderMediaItemUseCase,
                         ProjectInstanceCache projectInstanceCache,
                         CompositionApiClient compositionApiClient) {
        this.editActivityView = editActivityView;
        this.context = context;
        this.videoTranscodingErrorNotifier = videoTranscodingErrorNotifier;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.removeVideoFromProjectUseCase = removeVideoFromProjectUseCase;
        this.reorderMediaItemUseCase = reorderMediaItemUseCase;
        this.userEventTracker = userEventTracker;
        this.projectInstanceCache = projectInstanceCache;
        this.compositionApiClient = compositionApiClient;
    }

    public void addElementChangedListener() {
        currentProject.addListener(this);
    }

    public ListenableFuture<?> updatePresenter() {
        // TODO: 21/2/18 Study if is necessary repeat use case, running also in father,
        // TODO(jliarte): 9/07/18 implement this
//        editActivityView.showLoading();
        return this.executeUseCaseCall(() -> {
            setCurrentProject();
            getMediaListFromProjectUseCase.getMediaListFromProject(currentProject,
                    new OnVideosRetrieved() {
                        @Override
                        public void onVideosRetrieved(List<Video> videosRetrieved) {
                            int sizeOriginalVideoList = videosRetrieved.size();
                            List<Video> checkedVideoList = checkMediaPathVideosExistOnDevice(videosRetrieved);

                            List<Video> videoCopy = new ArrayList<>(checkedVideoList);

                            if (sizeOriginalVideoList > checkedVideoList.size()) {
                                editActivityView.showDialogMediasNotFound();
                            }
                            editActivityView.enableEditActions();
                            editActivityView.enableBottomBar();
                            editActivityView.enableFabText(true);
                            editActivityView.updateVideoList(videoCopy);
                            videoListErrorCheckerDelegate.checkWarningMessageVideosRetrieved(
                                    checkedVideoList, videoTranscodingErrorNotifier);
                        }

                        @Override
                        public void onNoVideosRetrieved() {
                            editActivityView.disableEditActions();
                            editActivityView.disableBottomBar();
                            editActivityView.enableFabText(false);
                            editActivityView.changeAlphaBottomBar(Constants.ALPHA_DISABLED_BOTTOM_BAR);
                        }
                    });
        });
    }

    private void setCurrentProject() {
        currentProject = projectInstanceCache.getCurrentProject();
        addElementChangedListener();
    }

    public String getResolution() {
        // TODO(jliarte): 19/12/16 inject sharedPreferences
        SharedPreferences sharedPreferences = VimojoApplication.getAppContext()
                .getSharedPreferences(
                        ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                        Context.MODE_PRIVATE);

        return sharedPreferences.getString(ConfigPreferences.RESOLUTION, "1280x720");
    }

    public void moveClip(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            editActivityView.seekToClip(toPosition);
            return;
        }
        reorderMediaItemUseCase.moveMediaItem(currentProject, fromPosition, toPosition, new OnReorderMediaListener() {
            @Override
            public void onSuccessMediaReordered() {
                // If everything was right the UI is already updated since the user did the
                // reordering over the "model view"
                userEventTracker.trackClipsReordered(currentProject);
                updateCompositionWithPlatform(currentProject);
                editActivityView.updatePlayerVideoListChanged();
            }

            @Override
            public void onErrorReorderingMedia() {
                //The reordering went wrong so we ask the project for the actual video list
                Log.d(TAG, "timeline:  error reordering!!");
                editActivityView.updatePlayerVideoListChanged();
            }
        });
    }

    private void updateCompositionWithPlatform(Project currentProject) {
        ListenableFuture<Project> compositionFuture = executeUseCaseCall(new Callable<Project>() {
            @Override
            public Project call() throws Exception {
                return compositionApiClient.updateComposition(currentProject);
            }
        });
        Futures.addCallback(compositionFuture, new FutureCallback<Project>() {
            @Override
            public void onSuccess(@Nullable Project result) {
                Log.d(TAG, "Success uploading composition to server ");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Error uploading composition to server " + t.getMessage());
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

    // TODO(jliarte): 23/04/18 move/remove?
    @Override
    public void onRemoveMediaItemFromTrackError() {
        // TODO modify error message
        editActivityView.showError(R.string.addMediaItemToTrackError);
    }

    // TODO(jliarte): 23/04/18 move/remove
    @Override
    public void onRemoveMediaItemFromTrackSuccess() {
        updateCompositionWithPlatform(currentProject);
        if (currentProject.getVMComposition().hasVideos()) {
            editActivityView.updatePlayerVideoListChanged();
            updatePresenter();
        } else {
            editActivityView.goToRecordOrGallery();
        }
    }

    private List<Video> checkMediaPathVideosExistOnDevice(List<Video> videoList) {
        List<Video> checkedVideoList = new ArrayList<>();
        for (int index = 0; index < videoList.size(); index++) {
            Video video = videoList.get(index);
            if (!new File(video.getMediaPath()).exists()) {
                // TODO(jliarte): 26/04/17 notify the user we are deleting items from project!!! FIXME
                ArrayList<Media> mediaToDeleteFromProject = new ArrayList<>();
                mediaToDeleteFromProject.add(video);
                removeVideoFromProjectUseCase.removeMediaItemsFromProject(
                        currentProject, mediaToDeleteFromProject, this);
                Log.e(TAG, video.getMediaPath() + " not found!! deleting from project");
            } else {
                checkedVideoList.add(video);
            }
        }
        return checkedVideoList;
    }

    public void removeVideoFromProject(int selectedVideoRemove) {
        removeVideoFromProjectUseCase.removeMediaItemFromProject(currentProject,
            selectedVideoRemove, new OnRemoveMediaFinishedListener() {
                    @Override
                    public void onRemoveMediaItemFromTrackSuccess() {
                        if (currentProject.getVMComposition().hasVideos()) {
                            editActivityView.updatePlayerVideoListChanged();
                            updatePresenter();
                        } else {
                            editActivityView.goToRecordOrGallery();
                        }
                    }

                    @Override
                    public void onRemoveMediaItemFromTrackError() {
                        //TODO modify error message
                        editActivityView.showError(R.string.addMediaItemToTrackError);
                    }
                });
    }

    @Override
    public void onObjectUpdated() {
        // TODO(jliarte): 26/07/17 save playback state and restore it when done updating project
        // in view
        editActivityView.updatePlayerVideoListChanged();
    }

    public String getCurrentTheme() {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.themeName, outValue, true);
        return (String) outValue.string;
    }

}
