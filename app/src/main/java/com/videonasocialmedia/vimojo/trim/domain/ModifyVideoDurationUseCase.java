package com.videonasocialmedia.vimojo.trim.domain;


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
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {

    public void trimVideo(Video videoToEdit, VideonaFormat format, final int startTimeMs,
                          final int finishTimeMs, MediaTranscoderListener listener) {

        try {

            videoToEdit.setStartTime(startTimeMs);
            videoToEdit.setStopTime(finishTimeMs);
            videoToEdit.setTempPathFinished(false);
            videoToEdit.setTempPath();
            videoToEdit.setTrimmedVideo(true);

            if(videoToEdit.isTextToVideoAdded()){
                transcodeTrimAndOverlayImageToVideo(videoToEdit.getMediaPath(), videoToEdit.getTempPath(),
                        format, listener, videoToEdit.getTextToVideo(), videoToEdit.getTextPositionToVideo(),
                        startTimeMs, finishTimeMs);
            } else {
                transcodeAndTrimVideo(videoToEdit.getMediaPath(),videoToEdit.getTempPath(), format,
                        listener, startTimeMs, finishTimeMs);
            }
        } catch (IOException e) {
            // TODO(javi.cabanas): 2/8/16 manage io exception on external library and send onTranscodeFailed if neccessary
            listener.onTranscodeFailed(e);
        }
    }

    private void transcodeAndTrimVideo(String mediaPath, String tempPath, VideonaFormat format,
                                       MediaTranscoderListener listener, int startTimeMs, int finishTimeMs) {

        try {
            MediaTranscoder.getInstance().transcodeAndTrimVideo(mediaPath, tempPath,
                    format, listener, startTimeMs, finishTimeMs);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onTranscodeFailed(e);
        }
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
        Drawable textDrawable = TextToDrawable.createDrawableWithTextAndPosition(text, textPosition, Constants.DEFAULT_VIMOJO_WIDTH,Constants.DEFAULT_VIMOJO_HEIGHT);

        return new Image(textDrawable, Constants.DEFAULT_VIMOJO_WIDTH,Constants.DEFAULT_VIMOJO_HEIGHT);
    }
}
