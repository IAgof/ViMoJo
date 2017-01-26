package com.videonasocialmedia.vimojo.export;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.pipeline.ApplyAudioFadeInFadeOutToVideo;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchExportTempBackgroundUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.main.DaggerExporterServiceComponent;
import com.videonasocialmedia.vimojo.main.ExporterServiceComponent;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.main.modules.DataRepositoriesModule;
import com.videonasocialmedia.vimojo.main.modules.ExporterServiceModule;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by alvaro on 5/09/16.
 */
public class ExportTempBackgroundService extends Service
        implements ApplyAudioFadeInFadeOutToVideo.OnApplyAudioFadeInFadeOutToVideoListener {

    public static final String ACTION = "com.videonasocialmedia.vimojo.android.service.receiver";
    public static final int TIME_FADE_IN_MS = 2000;
    public static final int TIME_FADE_OUT_MS = 500;

    GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase;
    private VideonaFormat videoFormat;
    @Inject VideoRepository videoRepository;
    private String tempVideoPathPreviewFadeInFadeOut;
    // TODO:(alvaro.martinez) 22/11/16 use project tmp directory
    private String intermediatesTempDirectory;
    private String intermediatesTempAudioFadeDirectory;
    @Inject ModifyVideoDurationUseCase modifyVideoDurationUseCase;
    @Inject ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase;

    private Drawable drawableFadeTransitionVideo;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;

    public ExportTempBackgroundService() {
        getExporterServiceComponent().inject(this);
        getVideoFormatFromCurrentProjectUseCase = new GetVideoFormatFromCurrentProjectUseCase();
        videoFormat = getVideoFormatFromCurrentProjectUseCase.getVideoTranscodedFormatFromCurrentProject();
        drawableFadeTransitionVideo = VimojoApplication.getAppContext().getDrawable(R.drawable.alpha_transition_black);
        getPreferencesTransitionFromProjectUseCase = new GetPreferencesTransitionFromProjectUseCase();
    }

    private ExporterServiceComponent getExporterServiceComponent() {
        return DaggerExporterServiceComponent.builder()
                .exporterServiceModule(new ExporterServiceModule())
                .dataRepositoriesModule(getDataRepositoriesModule())
                .build();
    }

    private DataRepositoriesModule getDataRepositoriesModule() {
//        return ((VimojoApplication) getApplication()).getDataRepositoriesModule();
        return new DataRepositoriesModule();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String videoId = intent.getStringExtra(IntentConstants.VIDEO_ID);

                final boolean isVideoRelaunch = intent.getBooleanExtra(
                        IntentConstants.RELAUNCH_EXPORT_TEMP, false);

                final boolean isVideoTrimmed = intent.getBooleanExtra(
                        IntentConstants.IS_VIDEO_TRIMMED, false);
                final int startTimeMs = intent.getIntExtra(IntentConstants.START_TIME_MS, 0);
                final int finishTimeMs = intent.getIntExtra(IntentConstants.FINISH_TIME_MS, 0);

                final boolean isAddedText = intent.getBooleanExtra(
                        IntentConstants.IS_TEXT_ADDED, false);
                final String text = intent.getStringExtra(IntentConstants.TEXT_TO_ADD);
                final String textPosition = intent.getStringExtra(IntentConstants.TEXT_POSITION);
                intermediatesTempDirectory = intent.getStringExtra(
                        IntentConstants.VIDEO_TEMP_DIRECTORY);
                intermediatesTempAudioFadeDirectory = intent.getStringExtra(
                    IntentConstants.VIDEO_TEMP_DIRECTORY_FADE_AUDIO);

                final Video video = getVideo(videoId);
                MediaTranscoderListener useCaseListener = new MediaTranscoderListener() {
                    @Override
                    public void onTranscodeProgress(double v) {
                    }

                    @Override
                    public void onTranscodeCompleted() {
                        try {
                            if(getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated()) {
                                applyAudioFadeInFadeOut(video);
                            } else {
                                exportTempBackgroundSuccess(video);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onTranscodeCanceled() {
                        video.deleteTempVideo();
                        if(video.isTrimmedVideo())
                            video.setTrimmedVideo(false);
                        if(video.hasText())
                            video.setTextToVideoAdded(false);
                        videoRepository.update(video);
                        sendResultBroadcast(videoId, false);
                    }

                    @Override
                    public void onTranscodeFailed(Exception e) {
                        // TODO(jliarte): 24/10/16 if transcoding fails, do we remove the effect??
                        video.deleteTempVideo();
                        if(video.isTrimmedVideo())
                            video.setTrimmedVideo(false);
                        if(video.hasText())
                            video.setTextToVideoAdded(false);
                        videoRepository.update(video);
                        sendResultBroadcast(videoId, false);
                    }

                };
                if (video != null) {
                    if(isVideoRelaunch){
                        relaunchExportVideo(video, useCaseListener, videoFormat);
                    }
                    if(isAddedText){
                        addTextToVideo(video, useCaseListener, videoFormat, text, textPosition);
                    }
                    if(isVideoTrimmed) {
                        trimVideo(video, useCaseListener,
                            videoFormat, startTimeMs, finishTimeMs);
                    }
                } else {
                    useCaseListener.onTranscodeFailed(null);
                }
            }
        }).start();

        return START_NOT_STICKY;
    }

    public void applyAudioFadeInFadeOut(Video video) throws IOException {
        tempVideoPathPreviewFadeInFadeOut = video.getTempPath();
        ApplyAudioFadeInFadeOutToVideo applyAudioFadeInFadeOutToVideo =
            new ApplyAudioFadeInFadeOutToVideo(this, intermediatesTempAudioFadeDirectory);
        applyAudioFadeInFadeOutToVideo.applyAudioFadeToVideo(video, TIME_FADE_IN_MS, TIME_FADE_OUT_MS);
    }

    private void relaunchExportVideo(Video video, MediaTranscoderListener useCaseListener,
                                     VideonaFormat videoFormat) {
        RelaunchExportTempBackgroundUseCase useCase = new RelaunchExportTempBackgroundUseCase();
        useCase.relaunchExport(drawableFadeTransitionVideo, video, useCaseListener, videoFormat);
    }

    private void addTextToVideo(Video video, MediaTranscoderListener useCaseListener,
                                VideonaFormat videoFormat, String text, String textPosition) {
        modifyVideoTextAndPositionUseCase.addTextToVideo(drawableFadeTransitionVideo, video,
            videoFormat, text, textPosition, useCaseListener);
    }

    private void trimVideo(Video video, MediaTranscoderListener useCaseListener,
                           VideonaFormat videoFormat, int startTimeMs, int finishTimeMs) {
        modifyVideoDurationUseCase.trimVideo(drawableFadeTransitionVideo, video, videoFormat,
            startTimeMs, finishTimeMs, useCaseListener);
    }

    private void sendResultBroadcast(String videoId, boolean success) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(IntentConstants.VIDEO_EXPORTED, success);
        intent.putExtra(IntentConstants.VIDEO_ID, videoId);
        sendBroadcast(intent);
    }

    private Video getVideo(String videoId) {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            for (Media video : videoList) {
                if (((Video) video).getUuid().compareTo(videoId) == 0) {
                    return (Video) video;
                }
            }
        }
        return null;
    }

    @Override
    public void OnGetAudioFadeInFadeOutError(String message, Video video) {
        video.deleteTempVideo();
        video.setTempPathToPreviousEdition(tempVideoPathPreviewFadeInFadeOut);
        exportTempBackgroundSuccess(video);
    }

    @Override
    public void OnGetAudioFadeInFadeOutSuccess(Video video) {
        exportTempBackgroundSuccess(video);
    }

    private void exportTempBackgroundSuccess(Video video) {
        video.setTempPathFinished(true);
        videoRepository.update(video);
        sendResultBroadcast(video.getUuid(), true);
    }
}
