package com.videonasocialmedia.vimojo.trim.domain;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.List;

/**
 *
 */
public class TrimBackgroundService extends Service {

    public static final String ACTION = "com.videonasocialmedia.vimojo.android.service.receiver";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final int videoId = intent.getIntExtra("videoId", -51456);
        final int startTimeMs = intent.getIntExtra("startTimeMs", 0);
        final int finishTimeMs = intent.getIntExtra("finishTimeMs", 0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Video video = getVideo(videoId);
                MediaTranscoderListener trimUseCaseListener = new MediaTranscoderListener() {
                    @Override
                    public void onTranscodeProgress(double v) {
                    }

                    @Override
                    public void onTranscodeCompleted() {
                        video.setTempPathFinished(true);
                        sendTrimResultBroadcast(videoId, true);
                    }

                    @Override
                    public void onTranscodeCanceled() {
                        video.deleteTempVideo();
                        sendTrimResultBroadcast(videoId, false);
                    }

                    @Override
                    public void onTranscodeFailed(Exception e) {
                        video.deleteTempVideo();
                        sendTrimResultBroadcast(videoId, false);
                    }

                };
                if (video != null) {
                    ModifyVideoDurationUseCase modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
                    video.setTempPath();
                    modifyVideoDurationUseCase.trimVideo(video, startTimeMs, finishTimeMs, trimUseCaseListener);
                } else {
                    trimUseCaseListener.onTranscodeFailed(null);
                }
            }
        }).start();

        return START_NOT_STICKY;
    }

    private void sendTrimResultBroadcast(int videoId, boolean success) {
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", success);
        intent.putExtra("videoId", videoId);
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

}
