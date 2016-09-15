package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;
import android.content.Intent;

import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideonaPlayerView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverView;
import com.videonasocialmedia.vimojo.split.domain.OnSplitVideoListener;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.utils.ExportIntentConstants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruth on 15/09/16.
 */
public class VoiceOverPresenter implements OnVideosRetrieved{

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private Video videoToEdit;

    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    private VoiceOverView voiceOverView;
    public UserEventTracker userEventTracker;
    public Project currentProject;
    private VideonaPlayerView playerView;

    public VoiceOverPresenter(VoiceOverView voiceOverView, VideonaPlayerView playerView) {
        this.playerView = playerView;
        this.voiceOverView = voiceOverView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.currentProject = loadCurrentProject();
    }

    public void onCreate() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null);
    }

    public void loadProjectVideo(int videoToTrimIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            onVideosRetrieved(v);
        }

    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        voiceOverView.showPreview(videoList);

        for (Video video:videoList) {
            if(video.isTextToVideoAdded())
                voiceOverView.showText(video.getTextToVideo(), video.getTextPositionToVideo());
            voiceOverView.initVoiceOverView(video.getStartTime(), video.getStopTime() - video.getStartTime());
        }
    }

    @Override
    public void onNoVideosRetrieved() {
        voiceOverView.showError("No videos");
    }


    public void addVoiceOver(Video video, int positionInAdapter, int timeMs) {

    }

    public void trackVoiceOverVideo() {

    }




}
