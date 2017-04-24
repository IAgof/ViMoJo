package com.videonasocialmedia.videonamediaframework.pipeline;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Image;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;

import java.io.IOException;

public class TranscoderHelper {

  private TextToDrawable drawableGenerator;
  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();

  public TranscoderHelper(TextToDrawable drawableGenerator, MediaTranscoder mediaTranscoder) {
    this.drawableGenerator = drawableGenerator;
    this.mediaTranscoder = mediaTranscoder;
  }

  // TODO:(alvaro.martinez) 22/11/16 unify in one constructor ¿?
  public TranscoderHelper(MediaTranscoder mediaTranscoder) {
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

  public void generateOutputVideoWithOverlayImage(Video video, VideonaFormat format,
                                                  MediaTranscoderListener listener)
          throws IOException  {
    Image imageText = getImageFromTextAndPosition(video.getClipText(), video.getClipTextPosition());

    mediaTranscoder.transcodeAndOverlayImageToVideo(video.getMediaPath(), video.getTempPath(),
            format, listener, imageText);
  }

  public void generateOutputVideoWithTrimming(Video video, VideonaFormat format,
                                              MediaTranscoderListener listener)
          throws IOException {
    mediaTranscoder.transcodeAndTrimVideo(video.getMediaPath(), video.getTempPath(), format,
            listener, video.getStartTime(), video.getStopTime());
  }

  @NonNull
  public Image getImageFromTextAndPosition(String text, String textPosition) {
    Drawable textDrawable = drawableGenerator.createDrawableWithTextAndPosition(text, textPosition,
            Constants.DEFAULT_CANVAS_WIDTH, Constants.DEFAULT_CANVAS_HEIGHT);

    return new Image(textDrawable, Constants.DEFAULT_CANVAS_WIDTH, Constants.DEFAULT_CANVAS_HEIGHT);
  }

  public void generateFileWithAudioFadeInFadeOut(String inputFile, int timeFadeInMs, int timeFadeOutMs,
                                                 String tempDirectory, String outputFile,
                                                 OnAudioEffectListener listener) throws IOException {

    mediaTranscoder.audioFadeInFadeOutToFile(inputFile, timeFadeInMs, timeFadeOutMs, tempDirectory,
            outputFile, listener);
  }
}