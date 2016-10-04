package com.videonasocialmedia.vimojo.export.domain;
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
 * Created by alvaro on 28/09/16.
 */

public class RelaunchExportTempBackgroundUseCase {

    public void relaunchExport(Video videoToEdit, MediaTranscoderListener listener, VideonaFormat videonaFormat) {

        videoToEdit.increaseNumTriesToExportVideo();

        try {
            transcodeVideo(videoToEdit.getMediaPath(), videoToEdit.getTempPath(),
                    videonaFormat, listener, videoToEdit.getTextToVideo(), videoToEdit.getTextPositionToVideo(),
                    videoToEdit.getStartTime(), videoToEdit.getStopTime());
        } catch (IOException e) {
            e.printStackTrace();
            listener.onTranscodeFailed(e);
        }

    }


    private void transcodeVideo(String mediaPath, String tempPath, VideonaFormat format,
                                MediaTranscoderListener listener, String text,
                                String textPosition, int startTime, int stopTime) throws IOException {

        Image imageText = getImageFromTextAndPosition(text, textPosition);

        MediaTranscoder.getInstance().transcodeTrimAndOverlayImageToVideo(mediaPath,
                tempPath,format, listener, imageText, startTime, stopTime);
    }

    @NonNull
    public Image getImageFromTextAndPosition(String text, String textPosition) {
        Drawable textDrawable = TextToDrawable.createDrawableWithTextAndPosition(text, textPosition, Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);

        return new Image(textDrawable, Constants.DEFAULT_VIMOJO_WIDTH,Constants.DEFAULT_VIMOJO_HEIGHT);
    }

}
