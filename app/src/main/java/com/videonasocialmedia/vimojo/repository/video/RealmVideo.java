package com.videonasocialmedia.vimojo.repository.video;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alejandro on 21/10/16.
 */

public class RealmVideo extends RealmObject {
    @PrimaryKey
    public String uuid;
    public int position;
    public String mediaPath;
    public float volume;
    public String tempPath;
    // TODO(jliarte): 22/10/16 what to do with those values, as they are private, and maybe we
    //                don't need to store that information
//    public int fileDuration;
    public String clipText;
    public String clipTextPosition;
    public boolean clipTextShadow;
    public boolean isTextToVideoAdded = false;
    public boolean isTrimmedVideo = false;
    public int startTime;
    public int stopTime;
    public String videoError;
    public boolean isTranscodingTempFileFinished = true;

    public RealmVideo() {
    }

    public RealmVideo(String uuid, int position, String mediaPath, float volume, String tempPath,
                      String clipText, String clipTextPosition, boolean clipTextShadow,
                      boolean textToVideoAdded, boolean trimmedVideo, int startTime, int stopTime,
                      String videoError, boolean isTranscodingTempFileFinished) {
        this.uuid = uuid;
        this.position = position;
        this.mediaPath = mediaPath;
        this.volume = volume;
        this.tempPath = tempPath;
        this.clipText = clipText;
        this.clipTextPosition = clipTextPosition;
        this.clipTextShadow = clipTextShadow;
        this.isTextToVideoAdded = textToVideoAdded;
        this.isTrimmedVideo = trimmedVideo;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.videoError = videoError;
        this.isTranscodingTempFileFinished = isTranscodingTempFileFinished;
    }

}
