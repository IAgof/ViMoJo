package com.videonasocialmedia.vimojo.sound.domain;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Audio;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.pipeline.AudioCompositionExportSession;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by alvaro on 16/09/16.
 */
public class MergeVoiceOverAudiosUseCase {

    private static final String TAG = "MergeVoiceOverAudiosUC";

    @Inject
    public MergeVoiceOverAudiosUseCase() {
    }

    public void mergeAudio(String pathAudioMerge, final OnMergeVoiceOverAudiosListener listener) {
        // TODO(jliarte): 30/11/16 make this in just one step and build AVComposition?
        //                Move this to presenter and pass composition as an argument?
        Project project = Project.getInstance(null,null,null,null);
        ArrayList<String> audioPathList =
            createAudioPathList(project.getProjectPathIntermediateAudioFilesVoiceOverRecord());
        final String pathAudioEdited = pathAudioMerge;

        VMComposition audioComposition = new VMComposition();
        try {
            addAudioTracksToComposition(audioPathList, audioComposition);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            logErrorMessage("IllegalItemOnTrack adding audio tracks - "
                    + String.valueOf(illegalItemOnTrack));
        }
        new AudioCompositionExportSession(audioComposition).exportAsyncronously(pathAudioEdited,
                new AudioCompositionExportSession.ExportSessionListener() {
            @Override
            public void onSuccess() {
                listener.onMergeVoiceOverAudioSuccess(pathAudioEdited);
            }

            @Override
            public void onError(String errorMessage) {
                logErrorMessage(errorMessage);
                listener.onMergeVoiceOverAudioError(errorMessage);
            }
        });

//        oldMergeCode(audioPathList, pathAudioEdited);
    }

    private void addAudioTracksToComposition(ArrayList<String> audioPathList,
                                             VMComposition audioComposition)
            throws IllegalItemOnTrack {
        Track audioTrack = audioComposition.getAudioTracks().get(0);
        for (String audioPath: audioPathList) {
            Audio itemToAdd = new Audio(audioPathList.indexOf(audioPath), audioPath,
                    Audio.DEFAULT_VOLUME, null);
            audioTrack.insertItem(itemToAdd);
        }
    }

    private ArrayList<String> createAudioPathList(String path) {
        // (jliarte): 29/11/16 this uses IO, so it should be in a background thread
        File directory = new File(path);
        ArrayList<String> audiosList = new ArrayList<String>();
        for(File audio: directory.listFiles()){
            audiosList.add(audio.getAbsolutePath());
        }
        return audiosList;
    }

    private void logErrorMessage(String msg) {
        Crashlytics.log(msg);
        Log.d(TAG, msg+" - Error in export session: " + msg);
    }

//    private void logExceptionWithMessage(Exception e, String msg) {
//        Crashlytics.log(msg);
//        Crashlytics.logException(e);
//        Log.d(TAG, msg+" - "+String.valueOf(e));
//    }

//    private void oldMergeCode(ArrayList<String> audioPathList, String pathAudioEdited) {
//        Movie result = appendFiles(audioPathList, true);
//        if (result != null) {
//            try {
//                createFile(result, pathAudioEdited);
//                listener.onMergeVoiceOverAudioSuccess(pathAudioEdited);
//            } catch (IOException | NullPointerException e) {
//                listener.onMergeVoiceOverAudioError(String.valueOf(e));
//            } catch (NoSuchElementException e) {
//                logExceptionWithMessage(e, "saveFinalVideo: Exception caught in 20161011 debugging session w/ pablo");
//                listener.onMergeVoiceOverAudioError(String.valueOf(e));
//            }
//        }
//    }


//    private Movie appendFiles(ArrayList<String> videoTranscodedPaths, boolean addOriginalAudio) {
//        Movie merge;
//        try {
//            merge = appender.appendVideos(videoTranscodedPaths, addOriginalAudio);
//        } catch (Exception e) {
//            merge = null;
//            listener.onMergeVoiceOverAudioError(String.valueOf(e));
//        }
//        return merge;
//    }

}
