package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {
    public void modifyVideoStartTime(Video video, int starTime) {
        video.setFileStartTime(starTime);
        video.setDuration(video.getFileStopTime() - video.getFileStartTime());
    }

    public void modifyVideoFinishTime(Video video, int finishTime) {
        video.setFileStopTime(finishTime);
        video.setDuration(video.getFileStopTime() - video.getFileStartTime());
    }
}
