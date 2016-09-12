package com.videonasocialmedia.vimojo.export;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.transcoder.overlay.Image;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatUseCase;
import com.videonasocialmedia.vimojo.export.domain.OnGetVideonaFormatListener;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.ExportIntentConstants;

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

                final int videoId = intent.getIntExtra(ExportIntentConstants.VIDEO_ID, -51456);

                final boolean isVideoTrimmed = intent.getBooleanExtra(ExportIntentConstants.IS_VIDEO_TRIMMED, false);
                final int startTimeMs = intent.getIntExtra(ExportIntentConstants.START_TIME_MS, 0);
                final int finishTimeMs = intent.getIntExtra(ExportIntentConstants.FINISH_TIME_MS, 0);

                final boolean isAddedText = intent.getBooleanExtra(ExportIntentConstants.IS_TEXT_ADDED, false);
                final String text = intent.getStringExtra(ExportIntentConstants.TEXT_TO_ADD);
                final String textPosition = intent.getStringExtra(ExportIntentConstants.TEXT_POSITION);


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
                        sendResultBroadcast(videoId, false);
                    }

                    @Override
                    public void onTranscodeFailed(Exception e) {
                        video.deleteTempVideo();
                        sendResultBroadcast(videoId, false);
                    }

                };
                if (video != null) {

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
        intent.putExtra(ExportIntentConstants.VIDEO_EXPORTED, success);
        intent.putExtra(ExportIntentConstants.VIDEO_ID, videoId);
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
