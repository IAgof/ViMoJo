package com.videonasocialmedia.vimojo.text.domain;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.transcoder.overlay.Image;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.IOException;

/**
 * Created by alvaro on 1/09/16.
 */
public class ModifyVideoTextAndPositionUseCase {

    public void addTextToVideo(Video videoToEdit, VideonaFormat format, String text, String textPosition,
                                MediaTranscoderListener listener) {
        try {

            videoToEdit.setTextToVideo(text);
            videoToEdit.setTextPositionToVideo(textPosition);
            videoToEdit.setTempPathFinished(false);
            videoToEdit.setTempPath();
            videoToEdit.setTextToVideoAdded(true);

            if(videoToEdit.isTrimmedVideo()) {
                transcodeTrimAndOverlayImageToVideo(videoToEdit.getMediaPath(), videoToEdit.getTempPath(),
                        format, listener, text, textPosition, videoToEdit.getStartTime(), videoToEdit.getStopTime());
            } else {
                transcodeTrimAndOverlayImageToVideo(videoToEdit.getMediaPath(), videoToEdit.getTempPath(),
                        format, listener, text, textPosition);
            }
        } catch (IOException e) {
            // TODO(javi.cabanas): 2/8/16 mangage io expception on external library and send onTranscodeFailed if neccessary
        }
    }

    private void transcodeTrimAndOverlayImageToVideo(String mediaPath, String tempPath, VideonaFormat format,
                                                     MediaTranscoderListener listener, String text,
                                                     String textPosition) throws IOException {

        Image imageText = getImageFromTextAndPosition(text, textPosition);

        MediaTranscoder.getInstance().transcodeAndOverlayImageToVideo(mediaPath,
                tempPath,format, listener, imageText);
    }

    private void transcodeTrimAndOverlayImageToVideo(String mediaPath, String tempPath, VideonaFormat format,
                                                     MediaTranscoderListener listener, String text,
                                                     String textPosition, int startTime, int stopTime) throws IOException {

        Image imageText = getImageFromTextAndPosition(text, textPosition);

        MediaTranscoder.getInstance().transcodeTrimAndOverlayImageToVideo(mediaPath,
                tempPath,format, listener, imageText, startTime, stopTime);
    }

    @NonNull
    public Image getImageFromTextAndPosition(String text, String textPosition) {
        Drawable textDrawable = TextToDrawable.createDrawableWithTextAndPosition(text, textPosition);

        return new Image(textDrawable, Constants.DEFAULT_VIMOJO_WIDTH,Constants.DEFAULT_VIMOJO_HEIGHT);
    }


}

