package com.videonasocialmedia.vimojo.repository.video;

import com.videonasocialmedia.vimojo.repository.project.RealmProject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alejandro on 21/10/16.
 */

public class RealmVideo extends RealmObject {
    @PrimaryKey
    public int identifier;
    public int position;
    public String mediaPath;
    // TODO(jliarte): 22/10/16 what to do with those values, as they are private, and maybe we
    //                don't need to store that information
//    public String tempPath;
//    public int fileDuration;
    public String clipText;
    public String clipTextPosition;
    public boolean isTextToVideoAdded = false;
    public boolean isTrimmedVideo = false;
    public int startTime;
    public int stopTime;

    public RealmVideo() {
    }

    public RealmVideo(int identifier, int position, String mediaPath, String clipText, String clipTextPosition,
                      boolean textToVideoAdded, boolean trimmedVideo, int startTime, int stopTime) {
        this.identifier = identifier;
        this.position = position;
        this.mediaPath = mediaPath;
        this.clipText = clipText;
        this.clipTextPosition = clipTextPosition;
        this.isTextToVideoAdded = textToVideoAdded;
        this.isTrimmedVideo = trimmedVideo;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }
}
