package com.videonasocialmedia.vimojo.trim.domain;


import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.engine.MediaTrimmerEngine;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {

    public void trimVideo(Video videoToEdit, final int startTimeMs, final int finishTimeMs, MediaTranscoderListener listener) {

        try {
            videoToEdit.setFileStartTime(startTimeMs);
            videoToEdit.setFileStopTime(finishTimeMs);

            Future<Void> mFuture = MediaTranscoder.getInstance().transcodeVideo(videoToEdit.getMediaPath(), videoToEdit.getTempPath(),
                    new VideonaFormat(), listener, null, null, startTimeMs, finishTimeMs);
        } catch (IOException e) {
            // TODO(javi.cabanas): 2/8/16 mangage io expception on external library and send onTranscodeFailed if neccessary
        }
    }
}
