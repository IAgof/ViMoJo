package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.GenerateVoiceOverUseCase;
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
public class VoiceOverRecordPresenter implements OnVideosRetrieved {

    private final String LOG_TAG = getClass().getSimpleName();
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private GenerateVoiceOverUseCase genereateVoiceOverUseCase;
    private VoiceOverView voiceOverView;
    public UserEventTracker userEventTracker;
    public Project currentProject;
    private boolean isRecording = false;
    private Recorder audioRecorder;

    @Inject
    public VoiceOverRecordPresenter(VoiceOverView voiceOverView, GetMediaListFromProjectUseCase
            getMediaListFromProjectUseCase,
                                    GetPreferencesTransitionFromProjectUseCase
                                            getPreferencesTransitionFromProjectUseCase,
                                    GenerateVoiceOverUseCase genereateVoiceOverUseCase) {
        this.voiceOverView = voiceOverView;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
                getPreferencesTransitionFromProjectUseCase;
        this.genereateVoiceOverUseCase = genereateVoiceOverUseCase;
        this.currentProject = loadCurrentProject();
        setupAudioRecorder();
    }

    protected void setupAudioRecorder() {
        cleanPreviousVoiceOverFiles();
        audioRecorder = OmRecorder.pcm(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override
                    public void onAudioChunkPulled(AudioChunk audioChunk) {
                    }
                }), fileRecordedPcm());
    }

    private void cleanPreviousVoiceOverFiles() {
        if (fileRecordedPcm().exists()) {
            fileRecordedPcm().delete();
        }
        if (fileRecordedWav().exists()) {
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
        if (fileRecordedPcm().exists()) {
            String destFile = currentProject.getProjectPathIntermediateFiles() + File.separator
                    + finalNamePathAudioMerge;
            genereateVoiceOverUseCase.generateVoiceOver(fileRecordedPcm().getAbsolutePath(),
                    destFile);
            voiceOverView.navigateToSoundVolumeActivity(destFile);
        } else {
            voiceOverView.showError("Please, record some voice over");
        }
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

    public void pauseRecording() {
        audioRecorder.pauseRecording();
        voiceOverView.pauseVideo();
    }

    public void resumeRecording() {
        audioRecorder.resumeRecording();
        voiceOverView.playVideo();
    }

    public void stopRecording() throws IOException {
        audioRecorder.stopRecording();
        //UtilsAudio.copyWaveFile(fileRecordedPcm().getAbsolutePath(),
        //  fileRecordedWav().getAbsolutePath());
        isRecording = false;
        voiceOverView.disableRecordButton();
    }

    public void cancelVoiceOverRecorded() {
        voiceOverView.resetVoiceOverRecorded();
        voiceOverView.cleanTempDirectoryPathVoiceOverRecorded(currentProject
                .getProjectPathIntermediateAudioFilesVoiceOverRecord());
    }

    protected PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.DEFAULT, AudioFormat.ENCODING_PCM_16BIT,
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
        return new File(currentProject.getProjectPathIntermediateFiles(), "AudioVoiceOver.pcm.wav");
    }

    public boolean isRecording() {
        return isRecording;
    }
}
