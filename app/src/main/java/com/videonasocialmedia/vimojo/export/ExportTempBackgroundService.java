package com.videonasocialmedia.vimojo.export;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatUseCase;
import com.videonasocialmedia.vimojo.export.domain.OnGetVideonaFormatListener;
import com.videonasocialmedia.vimojo.export.domain.RelaunchExportTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

/**
 * Created by alvaro on 5/09/16.
 */
public class ExportTempBackgroundService extends Service implements OnGetVideonaFormatListener {

    public static final String ACTION = "com.videonasocialmedia.vimojo.android.service.receiver";

    GetVideonaFormatUseCase getVideonaFormatUseCase;
    private VideonaFormat videoFormat;

    public ExportTempBackgroundService(){
       // getVideonaFormatUseCase = new GetVideonaFormatUseCase();
       // getVideonaFormatUseCase.getVideonaFormatFromProject(this);
        // TODO:(alvaro.martinez) 12/09/16 Get format from project, future functionality, use case created
        videoFormat = new VideonaFormat();
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

                final int videoId = intent.getIntExtra(IntentConstants.VIDEO_ID, -51456);

                final boolean isVideoRelaunch = intent.getBooleanExtra(IntentConstants.RELAUNCH_EXPORT_TEMP, false);

                final boolean isVideoTrimmed = intent.getBooleanExtra(IntentConstants.IS_VIDEO_TRIMMED, false);
                final int startTimeMs = intent.getIntExtra(IntentConstants.START_TIME_MS, 0);
                final int finishTimeMs = intent.getIntExtra(IntentConstants.FINISH_TIME_MS, 0);

                final boolean isAddedText = intent.getBooleanExtra(IntentConstants.IS_TEXT_ADDED, false);
                final String text = intent.getStringExtra(IntentConstants.TEXT_TO_ADD);
                final String textPosition = intent.getStringExtra(IntentConstants.TEXT_POSITION);


                final Video video = getVideo(videoId);
                MediaTranscoderListener useCaseListener = new MediaTranscoderListener() {
                    @Override
                    public void onTranscodeProgress(double v) {
                    }

                    @Override
                    public void onTranscodeCompleted() {
                        video.setTempPathFinished(true);
                        sendResultBroadcast(videoId, true);
                    }

                    @Override
                    public void onTranscodeCanceled() {
                        video.deleteTempVideo();
                        if(video.isTrimmedVideo())
                            video.setTrimmedVideo(false);
                        if(video.hasText())
                            video.setTextToVideoAdded(false);
                        sendResultBroadcast(videoId, false);
                    }

                    @Override
                    public void onTranscodeFailed(Exception e) {
                        video.deleteTempVideo();
                        if(video.isTrimmedVideo())
                            video.setTrimmedVideo(false);
                        if(video.hasText())
                            video.setTextToVideoAdded(false);
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
                        trimVideo(video, useCaseListener, videoFormat, startTimeMs, finishTimeMs);
                    }


                } else {
                    useCaseListener.onTranscodeFailed(null);
                }
            }
        }).start();

        return START_NOT_STICKY;
    }

    private void relaunchExportVideo(Video video, MediaTranscoderListener useCaseListener, VideonaFormat videoFormat) {

        RelaunchExportTempBackgroundUseCase useCase = new RelaunchExportTempBackgroundUseCase();
        useCase.relaunchExport(video, useCaseListener, videoFormat);

    }

    private void addTextToVideo(Video video, MediaTranscoderListener useCaseListener,
                                VideonaFormat videoFormat, String text, String textPosition) {

        ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase = new ModifyVideoTextAndPositionUseCase();
        modifyVideoTextAndPositionUseCase.addTextToVideo(video, videoFormat, text, textPosition, useCaseListener);

    }

    private void trimVideo(Video video, MediaTranscoderListener useCaseListener, VideonaFormat videoFormat,
                           int startTimeMs, int finishTimeMs) {

        ModifyVideoDurationUseCase modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
        modifyVideoDurationUseCase.trimVideo(video, videoFormat, startTimeMs, finishTimeMs, useCaseListener);

    }

    private void sendResultBroadcast(int videoId, boolean success) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(IntentConstants.VIDEO_EXPORTED, success);
        intent.putExtra(IntentConstants.VIDEO_ID, videoId);
        sendBroadcast(intent);
    }

    private Video getVideo(int videoId) {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            for (Media media : videoList) {
                if (media.getIdentifier() == videoId) {
                    return (Video) media;
                }
            }
        }
        return null;
    }

    @Override
    public void onVideonaFormat(VideonaFormat videonaFormat) {
        this.videoFormat = videonaFormat;
    }

    @Override
    public void onVideonaErrorFormat() {
        // Error
    }
}
