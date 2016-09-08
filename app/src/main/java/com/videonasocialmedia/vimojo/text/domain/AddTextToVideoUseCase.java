package com.videonasocialmedia.vimojo.text.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.transcoder.overlay.Overlay;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by alvaro on 1/09/16.
 */
public class AddTextToVideoUseCase {

    public void addTextToVideo (Video videoToEdit, VideonaFormat format, Overlay overlay, MediaTranscoderListener listener) {
        try {

            String inPath = videoToEdit.getMediaPath();
            if(videoToEdit.isEdited()) {
                waitForOutputFilesFinished(videoToEdit);
                inPath = videoToEdit.getTempPath();
            }
            videoToEdit.setTempPathFinished(false);
            videoToEdit.setTempPath();
            MediaTranscoder.getInstance().transcodeAndOverlayImageToVideo(inPath, videoToEdit.getTempPath(),
                    format, listener, overlay);
        } catch (IOException e) {
            // TODO(javi.cabanas): 2/8/16 mangage io expception on external library and send onTranscodeFailed if neccessary
        }
    }

    public void waitForOutputFilesFinished(Video videoToEdit) {

            if (videoToEdit.isEdited()) {
                while (!videoToEdit.outputVideoIsFinished()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

}

