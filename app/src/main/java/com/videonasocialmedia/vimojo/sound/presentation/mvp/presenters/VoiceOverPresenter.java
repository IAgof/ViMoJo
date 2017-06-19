package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.avrecorder.AudioRecorder;
import com.videonasocialmedia.avrecorder.SessionConfig;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.MergeVoiceOverAudiosUseCase;
import com.videonasocialmedia.vimojo.sound.domain.OnMergeVoiceOverAudiosListener;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverView;
import com.videonasocialmedia.vimojo.utils.FileUtils;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 15/09/16.
 */
public class VoiceOverPresenter implements OnVideosRetrieved, OnMergeVoiceOverAudiosListener{

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private SessionConfig sessionConfig;
    private AudioRecorder audioRecorder;

    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private MergeVoiceOverAudiosUseCase mergeVoiceOverAudiosUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;

    private VoiceOverView voiceOverView;
    public UserEventTracker userEventTracker;
    public Project currentProject;
    private boolean firstTimeRecording;

    @Inject
    public VoiceOverPresenter(VoiceOverView voiceOverView, GetMediaListFromProjectUseCase
                              getMediaListFromProjectUseCase,
                              GetPreferencesTransitionFromProjectUseCase
                                  getPreferencesTransitionFromProjectUseCase,
                              MergeVoiceOverAudiosUseCase mergeVoiceOverAudiosUseCase) {
        this.voiceOverView = voiceOverView;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
            getPreferencesTransitionFromProjectUseCase;
        this.mergeVoiceOverAudiosUseCase = mergeVoiceOverAudiosUseCase;
        this.currentProject = loadCurrentProject();

        initAudioRecorder();
    }

    public void initAudioRecorder() {
        try {
            sessionConfig = new SessionConfig(
                    currentProject.getProjectPathIntermediateAudioFilesVoiceOverRecord(), 1);
            audioRecorder = new AudioRecorder(sessionConfig);
            firstTimeRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onResume(){
        audioRecorder.onHostActivityResumed();
        init();
    }

    private void init() {
        obtainVideos();
        if (getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated()) {
            voiceOverView.setVideoFadeTransitionAmongVideos();
        }
        if (getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated() &&
                !currentProject.getVMComposition().hasMusic()) {
            voiceOverView.setAudioFadeTransitionAmongVideos();
        }
    }

    private void obtainVideos() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    public void onPause(){
        stopRecording();
        audioRecorder.onHostActivityPaused();
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null,null, null);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        voiceOverView.bindVideoList(videoList);
        voiceOverView.initVoiceOverView(0, currentProject.getDuration());
    }

    @Override
    public void onNoVideosRetrieved() {
        voiceOverView.resetPreview();
    }

    public void addVoiceOver(String finalNamePathAudioMerge) {
        mergeAudio(finalNamePathAudioMerge);
        // (jliarte): 1/12/16 merge audio finally makes an async call, so files are deleted before
        //            it completes
//        cleanDirectory();
    }

    public void cleanDirectory() {
       FileUtils.cleanDirectory(new
           File(currentProject.getProjectPathIntermediateAudioFilesVoiceOverRecord()));
    }

    private void mergeAudio(String finalNamePathAudioMerge) {
        String path = currentProject.getProjectPathIntermediateFiles() + File.separator
            + finalNamePathAudioMerge;
        mergeVoiceOverAudiosUseCase.mergeAudio(path, this);
    }

    public void trackVoiceOverVideo() {
        // TODO(jliarte): 29/11/16 user event tracker implementation
    }

    public void requestRecord() {
        if (!audioRecorder.isRecording()) {
            if (!firstTimeRecording) {
                try {
                    resetAudioRecorder();
                } catch (IOException ioe) {

                }
            } else {
                startRecording();
            }
        }
    }

    private void resetAudioRecorder() throws IOException {
        sessionConfig = new
            SessionConfig(currentProject.getProjectPathIntermediateAudioFilesVoiceOverRecord(), 1);
        audioRecorder.reset(sessionConfig);

        startRecording();
    }

    private void startRecording() {
        audioRecorder.startRecording();
        firstTimeRecording = false;
        voiceOverView.playVideo();
    }

    public void stopRecording() {
        if (audioRecorder.isRecording()) {
            audioRecorder.stopRecording();
            voiceOverView.pauseVideo();
        }
    }

    @Override
    public void onMergeVoiceOverAudioSuccess(String voiceOverRecordedPath) {
        cleanDirectory();
        voiceOverView.navigateToSoundVolumeActivity(voiceOverRecordedPath);
    }

    @Override
    public void onMergeVoiceOverAudioError(String message) {
        voiceOverView.showError("Cannot apply voice over in your video project");
    }
}
