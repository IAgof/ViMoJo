package com.videonasocialmedia.vimojo.sound.domain;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.googlecode.mp4parser.authoring.Movie;
import com.videonasocialmedia.muxer.Appender;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;

/**
 * Created by alvaro on 16/09/16.
 */
public class MergeVoiceOverAudiosUseCase {

    private static final String TAG = "MergeVoiceOverAudiosUC";
    OnMergeVoiceOverAudiosListener listener;

    public MergeVoiceOverAudiosUseCase(OnMergeVoiceOverAudiosListener listener) {
        this.listener = listener;
    }

    public void mergeAudio() {
        Movie result = appendFiles(createAudioPathList());
        if (result != null) {
            saveFinalVideo(result);
        }
    }

    private ArrayList<String> createAudioPathList() {
        File directory = new File(Constants.PATH_APP_TEMP_AUDIO);
        ArrayList<String> audiosList = new ArrayList<String>();;
        for(File audio: directory.listFiles()){
            audiosList.add(audio.getAbsolutePath());
        }

        return audiosList;
    }

    private Movie appendFiles(ArrayList<String> videoTranscoded) {
        return appendVideos(videoTranscoded, true);
    }

    private void saveFinalVideo(Movie result) {
        try {
            String pathAudioEdited = Constants.PATH_APP_TEMP + File.separator + "AUD_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
            com.videonasocialmedia.muxer.utils.Utils.createFile(result, pathAudioEdited);
            listener.onMergeVoiceOverAudioSuccess(pathAudioEdited);
        } catch (IOException | NullPointerException e) {
            listener.onMergeVoiceOverAudioError(String.valueOf(e));
        } catch (NoSuchElementException e) {
            logExceptionWithMessage(e, "saveFinalVideo: Exception caught in 20161011 debugging session w/ pablo");
            listener.onMergeVoiceOverAudioError(String.valueOf(e));
        }
    }

    private void logExceptionWithMessage(NoSuchElementException e, String msg) {
        Crashlytics.log(msg);
        Crashlytics.logException(e);
        Log.d(TAG, msg+" - "+String.valueOf(e));
    }

    private Movie appendVideos(ArrayList<String> videoTranscodedPaths, boolean addOriginalAudio) {
        Appender appender = new Appender();
        Movie merge;
        try {
            merge = appender.appendVideos(videoTranscodedPaths, addOriginalAudio);
        } catch (Exception e) {
            merge = null;
            listener.onMergeVoiceOverAudioError(String.valueOf(e));
        }
        return merge;
    }

}
