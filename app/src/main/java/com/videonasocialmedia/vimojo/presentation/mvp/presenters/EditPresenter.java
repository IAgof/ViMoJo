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

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoTranscodingErrorNotifier;
import com.videonasocialmedia.vimojo.presentation.mvp.views.EditActivityView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class EditPresenter implements OnAddMediaFinishedListener, OnRemoveMediaFinishedListener,
        ElementChangedListener {
    private final String TAG = getClass().getSimpleName();
    private Project currentProject;
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
    protected List<Video> videoList = new ArrayList<>();
    protected UserEventTracker userEventTracker;

    @Inject
    public EditPresenter(EditActivityView editActivityView,
                         Context context,
                         VideoTranscodingErrorNotifier videoTranscodingErrorNotifier,
                         UserEventTracker userEventTracker,
                         ProjectRepository projectRepository,
                         GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                         RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase,
                         ReorderMediaItemUseCase reorderMediaItemUseCase) {
        this.editActivityView = editActivityView;
        this.context =context;
        this.videoTranscodingErrorNotifier = videoTranscodingErrorNotifier;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.removeVideoFromProjectUseCase = removeVideoFromProjectUseCase;
        this.reorderMediaItemUseCase = reorderMediaItemUseCase;
        this.userEventTracker = userEventTracker;
        this.currentProject = projectRepository.getCurrentProject();
    }

    public void addElementChangedListener(){
        currentProject.addListener(this);
    }

    public String getResolution() {
        // TODO(jliarte): 19/12/16 inject sharedPreferences
        SharedPreferences sharedPreferences = VimojoApplication.getAppContext()
                .getSharedPreferences(
                        ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                        Context.MODE_PRIVATE);

        return sharedPreferences.getString(ConfigPreferences.RESOLUTION, "1280x720");
    }

    public void finishedMoveItem(int fromPosition, int toPosition) {
        if( fromPosition == toPosition ) {
            return;
        }
        reorderMediaItemUseCase.moveMediaItem(currentProject, fromPosition, toPosition, new OnReorderMediaListener() {
            @Override
            public void onSuccessMediaReordered() {
                // If everything was right the UI is already updated since the user did the
                // reordering over the "model view"
                userEventTracker.trackClipsReordered(currentProject);
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
        if (currentProject.getVMComposition().hasVideos()) {
            editActivityView.updatePlayerAndTimeLineVideoListChanged();
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
        ArrayList<Media> mediaToDeleteFromProject = new ArrayList<>();
        mediaToDeleteFromProject.add(videoList.get(selectedVideoRemove));
        removeVideoFromProjectUseCase.removeMediaItemsFromProject(currentProject,
            mediaToDeleteFromProject, this);
    }

    public void init() {
        // TODO: 21/2/18 Study if is necessary repeat use case, running also in father, EditorActivity. Tried ListenableFuture and make synchronus call to wait until finish and after this method get result of get medialist, problems with UI thread.
        getMediaListFromProjectUseCase.getMediaListFromProject(currentProject, new OnVideosRetrieved() {
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
                editActivityView.updateVideoList(videoCopy);
                videoListErrorCheckerDelegate.checkWarningMessageVideosRetrieved(
                    videoList, videoTranscodingErrorNotifier);
            }

            @Override
            public void onNoVideosRetrieved() {
                editActivityView.disableEditActions();
                editActivityView.disableBottomBar();
                editActivityView.enableFabText(false);
                editActivityView.changeAlphaBottomBar(Constants.ALPHA_DISABLED_BOTTOM_BAR);
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
