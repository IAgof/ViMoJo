package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.MergeVoiceOverAudiosUseCase;
import com.videonasocialmedia.vimojo.sound.domain.OnMergeVoiceOverAudiosListener;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;


/**
 * Created by ruth on 15/09/16.
 */
public class NewVoiceOverPresenter implements OnVideosRetrieved, OnMergeVoiceOverAudiosListener {

  /**
   * LOG_TAG
   */
  private final String LOG_TAG = getClass().getSimpleName();


  /**
   * Get media list from project use case
   */
  private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
  private MergeVoiceOverAudiosUseCase mergeVoiceOverAudiosUseCase;
  private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;

  private VoiceOverView voiceOverView;
  public UserEventTracker userEventTracker;
  public Project currentProject;
  private boolean isRecording = false;
  private Recorder audioRecorder;

  public int getNumVoiceOverRecorded() {
    return numVoiceOverRecorded;
  }

  private int numVoiceOverRecorded = 0;

  @Inject
  public NewVoiceOverPresenter(VoiceOverView voiceOverView, GetMediaListFromProjectUseCase
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
    setupAudioRecorder();
  }

  private void setupAudioRecorder() {
    //cleanPreviousVoiceOverFiles();
    audioRecorder = OmRecorder.pcm(
        new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
          @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
          }
        }), fileRecordedPcm());
  }

  private void cleanPreviousVoiceOverFiles() {
    if(fileRecordedPcm().exists()){
      fileRecordedPcm().delete();
    }
    if(fileRecordedWav().exists()){
      fileRecordedWav().delete();
    }
  }

  public void init() {
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

  private Project loadCurrentProject() {
    return Project.getInstance(null, null, null, null);
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
    /*if (getNumVoiceOverRecorded() > 0) {
      mergeAudio(finalNamePathAudioMerge);
    } else {
      voiceOverView.showError("Please, record some voice over");
    }*/
 /*   AudioEncoder audioEncoder = new AudioEncoder();
    try {
      audioEncoder.encodeToMp4(fileRecordedPcm().getAbsolutePath(), finalNamePathAudioMerge);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TranscodingException e) {
      e.printStackTrace();
    } */
    voiceOverView.navigateToSoundVolumeActivity(fileRecordedWav().getAbsolutePath());
  }

  protected void mergeAudio(String finalNamePathAudioMerge) {
    String path = currentProject.getProjectPathIntermediateFiles() + File.separator
        + finalNamePathAudioMerge;
    mergeVoiceOverAudiosUseCase.mergeAudio(path, this);
  }

  public void trackVoiceOverVideo() {
    // TODO(jliarte): 29/11/16 user event tracker implementationVoice
  }

  public void startRecording() {
    if (!isRecording) {
      isRecording = true;
      voiceOverView.cleanTempDirectoryPathVoiceOverRecorded(currentProject
          .getProjectPathIntermediateAudioFilesVoiceOverRecord());
    }
    voiceOverView.playVideo();
    audioRecorder.startRecording();
  }

  public void pauseRecording(){

    audioRecorder.pauseRecording();
    voiceOverView.pauseVideo();
  }

  public void resumeRecording(){
    audioRecorder.resumeRecording();
    voiceOverView.playVideo();
  }

  public void stopRecording() throws IOException {
    audioRecorder.stopRecording();
    //UtilsAudio.copyWaveFile(fileRecordedPcm().getAbsolutePath(),
      //  fileRecordedWav().getAbsolutePath());
    isRecording = false;
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

  private PullableSource mic() {
    return new PullableSource.Default(
        new AudioRecordConfig.Default(
            MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
            AudioFormat.CHANNEL_IN_MONO, 48000
        )
    );
  }

  @NonNull
  private File fileRecordedPcm() {
    return new File(currentProject.getProjectPathIntermediateFiles(), "AudioVoiceOver.pcm");
  }

  @NonNull
  private File fileRecordedWav() {
    return new File(currentProject.getProjectPathIntermediateFiles(), "AudioVoiceOver1.wav");
  }

  public boolean isRecording() {
    return isRecording;
  }
}
