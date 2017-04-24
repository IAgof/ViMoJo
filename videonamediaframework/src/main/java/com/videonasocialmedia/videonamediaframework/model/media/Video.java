/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Danny R. Fonseca Arboleda
 */
package com.videonasocialmedia.videonamediaframework.model.media;

import android.media.MediaMetadataRetriever;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.videonasocialmedia.videonamediaframework.model.Constants.*;

/**
 * A media video item that represents a file (or part of a file) that can be used in project video
 * track.
 *
 * @see com.videonasocialmedia.videonamediaframework.model.media.Media
 */
public class Video extends Media {

    private static final AtomicInteger count = new AtomicInteger(0);

    public static String VIDEO_FOLDER_PATH;

    /**
     * The total duration of the file media resource
     */
    private int fileDuration;
    // TODO(jliarte): 24/10/16 review this public field
    public String tempPath;
    private String clipText;
    private String clipTextPosition;
    private boolean isTempPathFinished = false;
    private boolean isTextToVideoAdded = false;
    private boolean isTrimmedVideo = false;

    // TODO(jliarte): 14/06/16 this entity should not depend on MediaMetadataRetriever as it is part of android
    /* Needed to allow mockito inject it */
    private MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    private int duration;

    private int numTriesToExportVideo = 0;
    private String uuid = UUID.randomUUID().toString();


    /**
     * protected default empty constructor, trying to get injectMocks working
     */
    protected Video() {
        super();
    }

    /**
     * Constructor of minimum number of parameters. Default constructor.
     *
     * @see com.videonasocialmedia.videonamediaframework.model.media.Media
     */
    public Video(String mediaPath) {
        super(-1, null, mediaPath, 0, 0, null);
        try {
            retriever.setDataSource(mediaPath);

            fileDuration = Integer.parseInt(retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION));
            startTime = 0;
            stopTime = fileDuration;
        } catch (Exception e) {
            fileDuration = 0;
            stopTime = 0;
        }
    }

    public Video(String mediaPath, int fileStartTime, int duration) {
        super(-1, null, mediaPath, fileStartTime, duration, null);
        fileDuration = getFileDuration(mediaPath);
    }

    public Video(Video video) {
        super(-1, null, video.getMediaPath(), video.getStartTime(),
                video.getDuration(), null);
        fileDuration = video.getFileDuration();
        stopTime = video.getStopTime();
        isTextToVideoAdded = video.hasText();
        clipText = video.getClipText();
        clipTextPosition = video.getClipTextPosition();
        if(video.isEdited()) {
            tempPath = video.getTempPath();
        }
        isTempPathFinished = video.outputVideoIsFinished();
        isTrimmedVideo = video.isTrimmedVideo();
    }

    public int getFileDuration() {
        return fileDuration;
    }

    private int getFileDuration(String path) {
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        return Integer.parseInt(retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempDirectory) {
        // TODO(jliarte): 18/11/16 tmp path should not be a constant depending on Android SDK but
        //                taken from Project path or VMComposition path and passed to constructor
//        String tempDirectory = Constants.PATH_APP_TEMP_INTERMEDIATE_FILES;
        tempPath = tempDirectory + File.separator
            + INTERMEDIATE_FILE_PREFIX + identifier + "_" + System.currentTimeMillis() + ".mp4";
    }

    public void setTempPathToPreviousEdition(String tempPath){
        this.tempPath = tempPath;
    }

    public boolean outputVideoIsFinished() {
        return isTempPathFinished;
    }

    public void setTempPathFinished(boolean tempPathFinished) {
        isTempPathFinished = tempPathFinished;
    }

    public void deleteTempVideo() {
        if (tempPath != null) {
            File f = new File(tempPath);
            f.delete();
            tempPath = null;
        }
    }

    public void createIdentifier() {
        if (identifier < 1)
            this.identifier = count.addAndGet(1);
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    // TODO(jliarte): 24/10/16 review this design as it gives problem with persistence
    public boolean isEdited() {
        return tempPath!=null;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getClipText() {
        return clipText;
    }

    public void setClipText(String clipText) {
        this.clipText = clipText;
    }

    public String getClipTextPosition() {
        return clipTextPosition;
    }

    public void setClipTextPosition(String clipTextPosition) {
        this.clipTextPosition = clipTextPosition;
    }

    public boolean hasText() {
        return isTextToVideoAdded;
    }

    public void setTextToVideoAdded(boolean textToVideoAdded) {
        isTextToVideoAdded = textToVideoAdded;
    }

    public boolean isTrimmedVideo() {
        return isTrimmedVideo;
    }

    public void setTrimmedVideo(boolean trimmedVideo) {
        isTrimmedVideo = trimmedVideo;
    }

    public int getNumTriesToExportVideo() {
        return numTriesToExportVideo;
    }

    public void increaseNumTriesToExportVideo(){
        numTriesToExportVideo++;
    }

    public boolean isTextToVideoAdded() {
        return isTextToVideoAdded;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
