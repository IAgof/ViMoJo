package com.videonasocialmedia.vimojo.text.domain;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.transcoder.overlay.Image;
import com.videonasocialmedia.vimojo.export.utils.TranscoderHelper;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.IOException;

/**
 * Created by alvaro on 1/09/16.
 */
public class ModifyVideoTextAndPositionUseCase {

    private TextToDrawable drawableGenerator = new TextToDrawable();
    private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
    protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator, mediaTranscoder);

    public void addTextToVideo(Video videoToEdit, VideonaFormat format, String text, String textPosition,
                               MediaTranscoderListener listener) {
        try {

            videoToEdit.setClipText(text);
            videoToEdit.setClipTextPosition(textPosition);
            videoToEdit.setTempPathFinished(false);
            videoToEdit.setTempPath();
            videoToEdit.setTextToVideoAdded(true);

            if(videoToEdit.isTrimmedVideo()) {
                transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(videoToEdit, format, listener);
            } else {
                transcodeTrimAndOverlayImageToVideo(videoToEdit.getMediaPath(), videoToEdit.getTempPath(),
                        format, listener, text, textPosition);
            }
        } catch (IOException e) {
            // TODO(javi.cabanas): 2/8/16 mangage io expception on external library and send onTranscodeFailed if neccessary
            listener.onTranscodeFailed(e);
        }
    }

    private void transcodeTrimAndOverlayImageToVideo(String mediaPath, String tempPath, VideonaFormat format,
                                                     MediaTranscoderListener listener, String text,
                                                     String textPosition) throws IOException {

        Image imageText = getImageFromTextAndPosition(text, textPosition);

        mediaTranscoder.transcodeAndOverlayImageToVideo(mediaPath,
                tempPath,format, listener, imageText);
    }

    @NonNull
    public Image getImageFromTextAndPosition(String text, String textPosition) {
        Drawable textDrawable = drawableGenerator.createDrawableWithTextAndPosition(text, textPosition,
                Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);

        return new Image(textDrawable, Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);
    }


}

