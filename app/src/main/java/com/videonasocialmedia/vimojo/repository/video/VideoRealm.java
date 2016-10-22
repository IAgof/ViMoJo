package com.videonasocialmedia.vimojo.repository.video;

import io.realm.RealmObject;

/**
 * Created by Alejandro on 21/10/16.
 */

public class VideoRealm extends RealmObject {
    public  String VIDEO_FOLDER_PATH;
    public int fileDuration;
    public String tempPath;
    public String clipText;
    public String clipTextPosition;
    public boolean isTextToVideoAdded = false;
    public boolean isTrimmedVideo = false;
    public int stopTime;
    public int startTime;
}
