package com.videonasocialmedia.vimojo.trim.domain;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TrimBackgroundService extends Service implements MediaTranscoderListener {

    public static final String ACTION = "com.videonasocialmedia.vimojo.android.service.receiver";
    Video video;
    protected Project currentProject;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int videoId = intent.getIntExtra("videoId", -51456);
        int startTimeMs = intent.getIntExtra("startTimeMs", 0);
        int finishTimeMs = intent.getIntExtra("finishTimeMs", 0);

        getVideo(videoId);
        if (video != null) {
            ModifyVideoDurationUseCase modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
            video.setTempPath();
            modifyVideoDurationUseCase.trimVideo(video, startTimeMs, finishTimeMs, this);
        } else {
            onTranscodeFailed(video.getTempPath(), null);
        }

        return START_NOT_STICKY;
    }

    private void getVideo(int videoId) {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            for (Media media : videoList) {
                if (media.getIdentifier() == videoId) {
                    video = (Video) media;
                }
            }
        }
    }

    @Override
    public void onTranscodeProgress(double v) {

    }

    @Override
    public void onTranscodeCompleted(String outPath) {
        setTempPathFinished(outPath, true);
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", true);
        sendBroadcast(intent);
    }

    @Override
    public void onTranscodeCanceled(String outPath) {
        setTempPathFinished(outPath, false);
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", false);
        sendBroadcast(intent);
    }

    @Override
    public void onTranscodeFailed(String outPath, Exception e) {
        setTempPathFinished(outPath, false);
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", false);
        sendBroadcast(intent);
    }

    private void setTempPathFinished(String outPath, boolean isTempPathFinished){

        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();

        if (videoList != null) {
            for (Media media : videoList) {;
                Video vid = (Video) media;
                if(vid.getTempPath().compareTo(outPath) == 0){
                    if(isTempPathFinished){
                        vid.setTempPathFinished(isTempPathFinished);
                    } else {
                        vid.deleteTempVideo();
                    }
                }
            }
        }
    }

}
