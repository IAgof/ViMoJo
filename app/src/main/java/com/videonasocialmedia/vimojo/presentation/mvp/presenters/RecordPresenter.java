/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.videonasocialmedia.avrecorder.AudioVideoRecorder;
import com.videonasocialmedia.avrecorder.SessionConfig;
import com.videonasocialmedia.avrecorder.VideoEncoderConfig;
import com.videonasocialmedia.avrecorder.event.CameraEncoderResetEvent;
import com.videonasocialmedia.avrecorder.event.CameraOpenedEvent;
import com.videonasocialmedia.avrecorder.event.MuxerFinishedEvent;
import com.videonasocialmedia.avrecorder.view.GLCameraView;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.eventbus.events.AddMediaItemToTrackSuccessEvent;
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.views.RecordView;
import com.videonasocialmedia.vimojo.utils.AnalyticsConstants;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * @author Juan Javier Cabanas
 */
/**
 * @deprecated RecordPresenter use camera1, avrecorder.
 */

public class RecordPresenter implements OnLaunchAVTransitionTempFileListener,
    TranscoderHelperListener {

    /**
     * LOG_TAG
     */
    private static final String LOG_TAG = "RecordPresenter";
    private final UserEventTracker userEventTracker;
    private boolean firstTimeRecording;
    private RecordView recordView;
    private SessionConfig config;
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    private AudioVideoRecorder recorder;
    private int recordedVideosNumber;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;
    private String resolution;
    private Context context;
    private GLCameraView cameraPreview;
    protected Project currentProject;
    private int height;

    private boolean externalIntent;

    private Drawable drawableFadeTransitionVideo;
    private VideonaFormat videoFormat;
    private UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase;
    private LaunchTranscoderAddAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase;
    private GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase;

    @Inject
    public RecordPresenter(Context context, RecordView recordView, UserEventTracker userEventTracker,
                           GLCameraView cameraPreview, SharedPreferences sharedPreferences,
                           boolean externalIntent,
                           AddVideoToProjectUseCase addVideoToProjectUseCase,
                           UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase,
                           LaunchTranscoderAddAVTransitionsUseCase
                                   launchTranscoderAddAVTransitionsUseCase,
                           GetVideoFormatFromCurrentProjectUseCase
                                   getVideonaFormatFromCurrentProjectUseCase) {
        this.context = context;
        this.recordView = recordView;
        this.userEventTracker = userEventTracker;
        this.cameraPreview = cameraPreview;
        this.sharedPreferences = sharedPreferences;
        this.externalIntent = externalIntent;
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.updateVideoRepositoryUseCase = updateVideoRepositoryUseCase;
        this.launchTranscoderAddAVTransitionUseCase = launchTranscoderAddAVTransitionsUseCase;
        this.getVideonaFormatFromCurrentProjectUseCase = getVideonaFormatFromCurrentProjectUseCase;
        this.currentProject = loadCurrentProject();
        preferencesEditor = sharedPreferences.edit();
        recordedVideosNumber = 0;

    }

    public Project loadCurrentProject() {
        return Project.getInstance(null,null,null);
    }

    private VideoEncoderConfig getVideoEncoderConfigFromProfileProject() {

        int width = currentProject.getProfile().getVideoResolution().getWidth();
        height = currentProject.getProfile().getVideoResolution().getHeight();
        int bitRate = currentProject.getProfile().getVideoQuality().getVideoBitRate();
        int frameRate = currentProject.getProfile().getVideoFrameRate().getFrameRate();

        return new VideoEncoderConfig(width, height, bitRate, frameRate);
    }

    public String getResolution() {
        return config.getVideoWidth() + "x" + config.getVideoHeight();
    }

    public void onStart() {
        if (recorder == null || recorder.isReleased()) {

            if(currentProject.getProfile() != null) {

                initRecorder(cameraPreview);
            }
        }
        recordView.showResolutionSelected(height);
        hideInitialsButtons();
        recordView.hidePrincipalViews();
    }

    private void initRecorder(GLCameraView cameraPreview) {
        checkLastTempFileRecordVideo();
        config = new SessionConfig(Constants.PATH_APP_TEMP, getVideoEncoderConfigFromProfileProject());
        try {
            recorder = new AudioVideoRecorder(config);
            recorder.setPreviewDisplay(cameraPreview);
            firstTimeRecording = true;
        } catch (IOException ioe) {
            Log.e("ERROR", "ERROR", ioe);
        }
    }

    // Save file if user go to home without stop video
    // Move to master last video recorded temp.
    // Video temp has to be bigger than 1MB to consider is video file
    // TODO:(alvaro.martinez) 21/10/16 Check how to save video if user go to home 
    private void checkLastTempFileRecordVideo() {

        String tempFileName = Constants.PATH_APP_TEMP + File.separator + Constants.VIDEO_TEMP_RECORD_FILENAME;
        File vTemp = new File(tempFileName);

        if(vTemp.exists() && vTemp.length() > 1024*1024) {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "VID_" + timeStamp + ".mp4";
            String destinationFile = Constants.PATH_APP_MASTERS + File.separator + fileName;
            try {
                Utils.moveFile(tempFileName, destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Utils.addFileToVideoGallery(destinationFile);
        }
    }

    private void hideInitialsButtons() {
        recordView.hideChronometer();
    }

    public void onResume() {
        EventBus.getDefault().register(this);
        if(recorder != null)
            recorder.onHostActivityResumed();
        if (!externalIntent)
            showThumbAndNumber();
        Log.d(LOG_TAG, "resume presenter");
    }

    private void showThumbAndNumber() {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        final List mediaInProject = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (mediaInProject != null && mediaInProject.size() > 0) {
            int lastItemIndex = mediaInProject.size() - 1;
            final Video lastItem = (Video) mediaInProject.get(lastItemIndex);
            this.recordedVideosNumber = mediaInProject.size();
            recordView.showVideosRecordedNumber(recordedVideosNumber);
            recordView.showRecordedVideoThumbWithText(lastItem.getMediaPath());
        } else {
            recordView.hideVideosRecordedNumber();
        }
    }

    public void onPause() {
        EventBus.getDefault().unregister(this);
        recorder.onHostActivityPaused();
        Log.d(LOG_TAG, "onPause presenter");
        recordView.hideProgressDialog();
    }

    public void stopRecord() {
        if (recorder.isRecording()) {
            trackUserInteracted(AnalyticsConstants.RECORD, AnalyticsConstants.STOP);
            recorder.stopRecording();
        }
    }

    /**
     * Sends button clicks to Mixpanel Analytics
     *
     * @param interaction
     * @param result
     */
    private void trackUserInteracted(String interaction, String result) {
        JSONObject userInteractionsProperties = new JSONObject();
        try {
            userInteractionsProperties.put(AnalyticsConstants.ACTIVITY, context.getClass().getSimpleName());
            userInteractionsProperties.put(AnalyticsConstants.RECORDING, recorder.isRecording());
            userInteractionsProperties.put(AnalyticsConstants.INTERACTION, interaction);
            userInteractionsProperties.put(AnalyticsConstants.RESULT, result);
            userEventTracker.mixpanel.track(AnalyticsConstants.USER_INTERACTED, userInteractionsProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onStop() {
        if (recorder.isRecording())
            recorder.stopRecording();
    }

    public void onDestroy() {
        recorder.release();
    }

    public void requestRecord() {
        if (!recorder.isRecording()) {
            if (!firstTimeRecording) {
                try {
                    if(currentProject!= null) {
                        resetRecorder();
                    }
                } catch (IOException ioe) {
                    //recordView.showError();
                }
            } else {
                startRecord();
            }
        }
    }

    private void resetRecorder() throws IOException {
        config = new SessionConfig(Constants.PATH_APP_TEMP, getVideoEncoderConfigFromProfileProject());
        recorder.reset(config);
    }

    private void startRecord() {
        userEventTracker.mixpanel.timeEvent(AnalyticsConstants.VIDEO_RECORDED);
        trackUserInteracted(AnalyticsConstants.RECORD, AnalyticsConstants.START);
        recorder.startRecording();
        recordView.lockScreenRotation();
        recordView.showStopButton();
        recordView.startChronometer();
        recordView.showChronometer();
        recordView.hideSettingsOptions();
        recordView.hideVideosRecordedNumber();
        recordView.hideRecordedVideoThumbWithText();
        firstTimeRecording = false;
    }


    public void onEventMainThread(CameraEncoderResetEvent e) {
        startRecord();
    }

    public void onEventMainThread(CameraOpenedEvent e) {

        Log.d(LOG_TAG, "camera opened, camera != null");
        //Calculate orientation, rotate if needed
        //recordView.unlockScreenRotation();
        if (firstTimeRecording) {
            recordView.unlockScreenRotation();
        }

    }

    public void onEventMainThread(MuxerFinishedEvent e) {
        String finalPath = moveVideoToMastersFolder();
        if (externalIntent) {
            recordView.finishActivityForResult(finalPath);
        } else {
            addVideoToProjectUseCase.addVideoToTrack(finalPath, this);
        }
    }

    private String moveVideoToMastersFolder() {

        String originalFile = config.getOutputPath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "VID_" + timeStamp + ".mp4";
        String destinationFile = Constants.PATH_APP_MASTERS + File.separator + fileName;
        try {
            Utils.moveFile(originalFile, destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.addFileToVideoGallery(destinationFile);

        int numTotalVideosRecorded = sharedPreferences
                .getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
        preferencesEditor.putInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED,
                ++numTotalVideosRecorded);
        preferencesEditor.commit();
        trackTotalVideosRecordedSuperProperty();
        double clipDuration = 0.0;
        try {
            clipDuration = Utils.getFileDuration(destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        trackVideoRecorded(clipDuration);
        return destinationFile;
    }

    private void trackTotalVideosRecordedSuperProperty() {
        JSONObject totalVideoRecordedSuperProperty = new JSONObject();
        int numPreviousVideosRecorded;
        try {
            numPreviousVideosRecorded =
                    userEventTracker.mixpanel.getSuperProperties().getInt(AnalyticsConstants.TOTAL_VIDEOS_RECORDED);
        } catch (JSONException e) {
            numPreviousVideosRecorded = 0;
        }
        try {
            totalVideoRecordedSuperProperty.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
                    ++numPreviousVideosRecorded);
            userEventTracker.mixpanel.registerSuperProperties(totalVideoRecordedSuperProperty);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void trackVideoRecorded(Double clipDuration) {
        JSONObject videoRecordedProperties = new JSONObject();
        resolution = config.getVideoWidth() + "x" + config.getVideoHeight();
        int totalVideosRecorded = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
        try {
            videoRecordedProperties.put(AnalyticsConstants.VIDEO_LENGTH, clipDuration);
            videoRecordedProperties.put(AnalyticsConstants.RESOLUTION, resolution);
            videoRecordedProperties.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
                    totalVideosRecorded);
            userEventTracker.mixpanel.track(AnalyticsConstants.VIDEO_RECORDED, videoRecordedProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        trackVideoRecordedUserTraits();
    }

    private void trackVideoRecordedUserTraits() {
        /* TODO: why do we update quality and resolution on video recorded?? This should be only updated in settings
        JSONObject userProfileProperties = new JSONObject();
        try {
            userProfileProperties.put(AnalyticsConstants.RESOLUTION, sharedPreferences.getString(
                    AnalyticsConstants.RESOLUTION, resolution));
            userProfileProperties.put(AnalyticsConstants.QUALITY,
                    sharedPreferences.getInt(AnalyticsConstants.QUALITY, config.getVideoBitRate()));
            mixpanel.getPeople().set(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        userEventTracker.mixpanel.getPeople().increment(AnalyticsConstants.TOTAL_VIDEOS_RECORDED, 1);
        userEventTracker.mixpanel.getPeople().set(AnalyticsConstants.LAST_VIDEO_RECORDED,
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
    }

    public void onEvent(AddMediaItemToTrackSuccessEvent e) {
        String path = e.videoAdded.getMediaPath();
        recordView.showRecordButton();
        recordView.showSettingsOptions();
        recordView.stopChronometer();
        recordView.hideChronometer();
        recordView.reStartScreenRotation();
        recordView.showRecordedVideoThumbWithText(path);
        recordView.showVideosRecordedNumber(++recordedVideosNumber);
    }

    public int getProjectDuration() {
        return currentProject.getDuration();
    }

    public int getNumVideosOnProject() {
        return recordedVideosNumber;
    }

    public void changeCamera() {
        //TODO controlar el estado del flash

        int camera = recorder.requestOtherCamera();

        if (camera == 0) {
            recordView.showBackCameraSelected();

        } else {

            if (camera == 1) {
                recordView.showFrontCameraSelected();
            }
        }
        checkFlashSupport();

    }

    public void checkFlashSupport() {

        // Check flash support
        int flashSupport = recorder.checkSupportFlash(); // 0 true, 1 false, 2 ignoring, not prepared

        Log.d(LOG_TAG, "checkSupportFlash flashSupport " + flashSupport);

        if (flashSupport == 0) {
            recordView.showFlashSupported(true);
            Log.d(LOG_TAG, "checkSupportFlash flash Supported camera");
        } else {
            if (flashSupport == 1) {
                recordView.showFlashSupported(false);
                Log.d(LOG_TAG, "checkSupportFlash flash NOT Supported camera");
            }
        }
    }

    public void setFlashOff() {
        boolean on = recorder.setFlashOff();
        recordView.showFlashOn(on);
    }

    public void toggleFlash() {
        boolean on = recorder.toggleFlash();
        recordView.showFlashOn(on);
    }

    public void rotateCamera(int rotation) {
        recorder.rotateCamera(rotation);
    }


    @Override
    public void videoToLaunchAVTransitionTempFile(Video video, String intermediatesTempAudioFadeDirectory) {

        video.setTempPath(currentProject.getProjectPathIntermediateFiles());

        videoFormat = currentProject.getVMComposition().getVideoFormat();
        drawableFadeTransitionVideo = context.getDrawable(R.drawable.alpha_transition_white);

        launchTranscoderAddAVTransitionUseCase.launchExportTempFile(drawableFadeTransitionVideo, video, videoFormat,
            intermediatesTempAudioFadeDirectory, this);
    }

    @Override
    public void onSuccessTranscoding(Video video) {
        Log.d(LOG_TAG, "onSuccessTranscoding " + video.getTempPath());
        updateVideoRepositoryUseCase.succesTranscodingVideo(video);
    }

    @Override
    public void onErrorTranscoding(Video video, String message) {
        Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
        if(video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO){
            video.increaseNumTriesToExportVideo();
            Project currentProject = Project.getInstance(null, null, null);
            launchTranscoderAddAVTransitionUseCase.launchExportTempFile(context
                    .getDrawable(R.drawable.alpha_transition_white), video,
                getVideonaFormatFromCurrentProjectUseCase.getVideonaFormatFromCurrentProject(),
                currentProject.getProjectPathIntermediateFileAudioFade(), this);
        } else {
            updateVideoRepositoryUseCase.errorTranscodingVideo(video,
                Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.AVTRANSITION.name());
        }
    }

}
