package com.videonasocialmedia.vimojo.text.domain;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.transcoder.overlay.Image;
import com.videonasocialmedia.transcoder.overlay.Overlay;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by alvaro on 1/09/16.
 */
public class ModifyVideoTextAndPositionUseCase {

    public void addTextToVideo (Video videoToEdit, VideonaFormat format, String text, String textPosition, MediaTranscoderListener listener) {
        try {

            Drawable textDrawable = TextToDrawable.createDrawableWithTextAndPosition(text, textPosition);

            Image imageText = new Image(textDrawable, Constants.DEFAULT_VIMOJO_WIDTH,Constants.DEFAULT_VIMOJO_HEIGHT);

            videoToEdit.setTempPathFinished(false);
            videoToEdit.setTempPath();

            if(videoToEdit.isEdited()) {

                MediaTranscoder.getInstance().transcodeTrimAndOverlayImageToVideo(videoToEdit.getMediaPath(),
                        videoToEdit.getTempPath(),format, listener, imageText, videoToEdit.getStartTime(),
                        videoToEdit.getStopTime());
            } else {

                MediaTranscoder.getInstance().transcodeAndOverlayImageToVideo(videoToEdit.getMediaPath(), videoToEdit.getTempPath(),
                        format, listener, imageText);
            }
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

