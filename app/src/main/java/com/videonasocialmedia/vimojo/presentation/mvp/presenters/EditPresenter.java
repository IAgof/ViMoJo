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

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveVideoFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ReorderMediaItemUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.views.EditorView;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ToolbarNavigator;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProfileSharedPreferencesRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditPresenter implements OnAddMediaFinishedListener, OnRemoveMediaFinishedListener,
        OnVideosRetrieved, OnReorderMediaListener, GetMusicFromProjectCallback {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    /**
     * UseCases
     */
    private RemoveVideoFromProjectUseCase remoVideoFromProjectUseCase;
    private ReorderMediaItemUseCase reorderMediaItemUseCase;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private ToolbarNavigator.ProjectModifiedCallBack projectModifiedCallBack;
    private GetMusicFromProjectUseCase getMusicFromProjectUseCase;
    /**
     * Editor View
     */
    private EditorView editorView;
    private List<Video> videoList;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private ProfileRepository profileRepository;
    protected ProjectRepository projectRepository;

    public EditPresenter(EditorView editorView,
                         ToolbarNavigator.ProjectModifiedCallBack projectModifiedCallBack,
                         UserEventTracker userEventTracker, SharedPreferences sharedPreferences) {
        this.editorView = editorView;
        this.projectModifiedCallBack = projectModifiedCallBack;

        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        remoVideoFromProjectUseCase = new RemoveVideoFromProjectUseCase();
        reorderMediaItemUseCase = new ReorderMediaItemUseCase();
        getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
        profileRepository = new ProfileSharedPreferencesRepository(sharedPreferences,
            VimojoApplication.getAppContext());
        projectRepository = new ProjectRealmRepository();
        this.userEventTracker = userEventTracker;
        this.currentProject = loadCurrentProject();
    }

    public Project loadCurrentProject() {
       /* Project project = projectRepository.getCurrentProject();
        if(project == null){
            Project newProject = Project.getInstance(DateUtils.getDateRightNow(), Constants.PATH_APP,
                profileRepository.getCurrentProfile());
            projectRepository.update(newProject);
            return newProject;
        }
        return projectRepository.getCurrentProject();*/
        return Project.getInstance(null, null, null);
    }


    public String getResolution() {
        SharedPreferences sharedPreferences = VimojoApplication.getAppContext().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);

        return sharedPreferences.getString(ConfigPreferences.RESOLUTION, "1280x720");
    }

    public void moveItem(int fromPosition, int toPositon) {
        reorderMediaItemUseCase.moveMediaItem(videoList.get(fromPosition), toPositon, this);
    }

    @Override
    public void onAddMediaItemToTrackError() {
        //TODO modify error message
        editorView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media media) {

    }

    @Override
    public void onRemoveMediaItemFromTrackError() {
        //TODO modify error message
        editorView.showError(R.string.addMediaItemToTrackError);
    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {
        editorView.updateProject();
        projectModifiedCallBack.onProjectModified();

    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        int sizeVideoList = videoList.size();
        List<Video> checkedVideoList = checkMediaPathVideosExist(videoList);
        this.videoList = checkedVideoList;

        List<Video> videoCopy = new ArrayList<>(checkedVideoList);

        if(sizeVideoList > checkedVideoList.size()){
            editorView.showDialogMediasNotFound();
        }

        editorView.enableEditActions();
        //videonaPlayerView.bindVideoList(videoList);
        editorView.bindVideoList(videoCopy);
        projectModifiedCallBack.onProjectModified();
    }

    private List<Video> checkMediaPathVideosExist(List<Video> videoCopy) {

        List<Video> checkedVideoList = new ArrayList<>();

        for (int index = 0; index < videoCopy.size(); index++) {
            Video video = videoCopy.get(index);
            if(!new File(video.getMediaPath()).exists()){
                ArrayList<Media> mediaToDeleteFromProject = new ArrayList<>();
                mediaToDeleteFromProject.add(video);
                remoVideoFromProjectUseCase.removeMediaItemsFromProject(mediaToDeleteFromProject, this);
            } else {
                checkedVideoList.add(video);
            }
        }

        return checkedVideoList;
    }

    @Override
    public void onNoVideosRetrieved() {
        editorView.disableEditActions();
        editorView.hideProgressDialog();
        editorView.showMessage(R.string.add_videos_to_project);
        editorView.expandFabMenu();
        editorView.resetPreview();
        projectModifiedCallBack.onProjectModified();
    }

    public void removeVideoFromProject(int selectedVideoRemove) {
        Video videoToRemove = this.videoList.get(selectedVideoRemove);
        ArrayList<Media> mediaToDeleteFromProject = new ArrayList<>();
        mediaToDeleteFromProject.add(videoToRemove);
        remoVideoFromProjectUseCase.removeMediaItemsFromProject(mediaToDeleteFromProject, this);
    }

    @Override
    public void onMediaReordered(Media media, int newPosition) {
        //If everything was right the UI is already updated since the user did the reordering
        userEventTracker.trackClipsReordered(currentProject);
        // (jliarte): 24/08/16 probando fix del reorder. Si actualizamos el proyecto al
        //          reordenar, como se reordena en cada cambio de celda, no sólo al final,
        //          generamos overhead innecesario en la actividad y además de esto, se para el
        //          preview y se corta el movimiento que estemos haciendo de reordenado
//        editorView.updateProject();
    }

    @Override
    public void onErrorReorderingMedia() {
        //The reordering went wrong so we ask the project for the actual video list
        obtainVideos();
    }

    public void obtainVideos() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    public void loadProject() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
        if(currentProject.hasMusic())
            getMusicFromProjectUseCase.getMusicFromProject(this);
    }

    @Override
    public void onMusicRetrieved(Music music) {
        editorView.setMusic(music);
    }

}
