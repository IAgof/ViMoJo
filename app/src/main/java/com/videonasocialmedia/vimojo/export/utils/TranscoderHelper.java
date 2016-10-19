package com.videonasocialmedia.vimojo.export.utils;

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

public class TranscoderHelper {

  private TextToDrawable drawableGenerator = new TextToDrawable();
  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();

  public TranscoderHelper(TextToDrawable drawableGenerator, MediaTranscoder mediaTranscoder) {
    this.drawableGenerator = drawableGenerator;
    this.mediaTranscoder = mediaTranscoder;
  }

  public void generateOutputVideoWithOverlayImageAndTrimming(Video videoToEdit,
                                                             VideonaFormat format,
                                                             MediaTranscoderListener listener)
          throws IOException {
    Image imageText = getImageFromTextAndPosition(videoToEdit.getClipText(),
            videoToEdit.getClipTextPosition());

    mediaTranscoder.transcodeTrimAndOverlayImageToVideo(videoToEdit.getMediaPath(),
            videoToEdit.getTempPath(), format, listener, imageText, videoToEdit.getStartTime(),
            videoToEdit.getStopTime());
  }

  @NonNull
  public Image getImageFromTextAndPosition(String text, String textPosition) {
    Drawable textDrawable = drawableGenerator.createDrawableWithTextAndPosition(text, textPosition,
            Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);

    return new Image(textDrawable, Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);
  }
}