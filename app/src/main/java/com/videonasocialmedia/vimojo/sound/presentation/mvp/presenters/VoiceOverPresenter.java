package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.avrecorder.AudioRecorder;
import com.videonasocialmedia.avrecorder.SessionConfig;
import com.videonasocialmedia.avrecorder.event.MuxerFinishedEvent;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.MergeVoiceOverAudiosUseCase;
import com.videonasocialmedia.vimojo.sound.domain.OnMergeVoiceOverAudiosListener;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;


import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by ruth on 15/09/16.
 */
@Deprecated // New implemetation of voice over. Not needed avrecorder package to save several .mp4
// audio voice over files and merged. Instead of, it use om-recorder library to save one file .pcm
public class VoiceOverPresenter implements OnVideosRetrieved, OnMergeVoiceOverAudiosListener {

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
    private boolean firstTimeRecording = true;

  public int getNumVoiceOverRecorded() {
    return numVoiceOverRecorded;
  }

  private int numVoiceOverRecorded = 0;

    @Inject
    public VoiceOverPresenter(VoiceOverView voiceOverView, GetMediaListFromProjectUseCase
                              getMediaListFromProjectUseCase,
                              GetPreferencesTransitionFromProjectUseCase
                                  getPreferencesTransitionFromProjectUseCase,
                              MergeVoiceOverAudiosUseCase mergeVoiceOverAudiosUseCase, SessionConfig
                              sessionConfig, AudioRecorder audioRecorder) {
        this.voiceOverView = voiceOverView;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
            getPreferencesTransitionFromProjectUseCase;
        this.mergeVoiceOverAudiosUseCase = mergeVoiceOverAudiosUseCase;
        this.currentProject = loadCurrentProject();
        this.sessionConfig = sessionConfig;
        this.audioRecorder = audioRecorder;
    }

    public void onResume(){
      EventBus.getDefault().register(this);
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

    public void onPause() throws IOException {
        stopRecording();
        audioRecorder.onHostActivityPaused();
      EventBus.getDefault().unregister(this);
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null,null,null,null);
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
      if(getNumVoiceOverRecorded() > 0) {
        mergeAudio(finalNamePathAudioMerge);
      } else {
        voiceOverView.showError("Please, record some voice over");
      }
    }

    protected void mergeAudio(String finalNamePathAudioMerge) {
        String path = currentProject.getProjectPathIntermediateFiles() + File.separator
            + finalNamePathAudioMerge;
        mergeVoiceOverAudiosUseCase.mergeAudio(path, this);
    }

    public void trackVoiceOverVideo() {
        // TODO(jliarte): 29/11/16 user event tracker implementation
    }

    public void requestRecord() {
        if (!audioRecorder.isRecording()) {
          startRecording();
        }
    }

    private void resetAudioRecorder() throws IOException {
      renameFileRecorded();
      sessionConfig = new
            SessionConfig(currentProject.getProjectPathIntermediateAudioFilesVoiceOverRecord(), 1);
      audioRecorder.reset(sessionConfig);
    }

    private void startRecording() {
      if(firstTimeRecording){
        firstTimeRecording = false;
        voiceOverView.cleanTempDirectoryPathVoiceOverRecorded(currentProject
            .getProjectPathIntermediateAudioFilesVoiceOverRecord());
      }
        audioRecorder.startRecording();
        voiceOverView.playVideo();
    }

    public void stopRecording() throws IOException {
        if (audioRecorder.isRecording()) {
            audioRecorder.stopRecording();
            voiceOverView.pauseVideo();
        }
    }

  public void onEventMainThread(MuxerFinishedEvent e) throws IOException {
    resetAudioRecorder();
  }

    private void renameFileRecorded() {
        File voiceOverRecorded = new File(sessionConfig.getOutputPath());
        String directory = voiceOverRecorded.getParentFile().getAbsolutePath();
        File renamedVoiceOver = new File(directory, incrementAudioRecorded());
        voiceOverRecorded.renameTo(renamedVoiceOver);
    }

    protected String incrementAudioRecorded() {
        return "AUD_VO_" + numVoiceOverRecorded++ + ".mp4" ;
    }

    @Override
    public void onMergeVoiceOverAudioSuccess(String voiceOverRecordedPath) {
        voiceOverView.cleanTempDirectoryPathVoiceOverRecorded(currentProject
            .getProjectPathIntermediateAudioFilesVoiceOverRecord());
        voiceOverView.navigateToSoundVolumeActivity(voiceOverRecordedPath);
    }

    @Override
    public void onMergeVoiceOverAudioError(String message) {
        voiceOverView.showError("Cannot apply voice over in your video project");
    }

    public void cancelVoiceOverRecorded() {
        voiceOverView.resetVoiceOverRecorded();
        voiceOverView.cleanTempDirectoryPathVoiceOverRecorded(currentProject
            .getProjectPathIntermediateAudioFilesVoiceOverRecord());
      numVoiceOverRecorded = 0;
    }
}
