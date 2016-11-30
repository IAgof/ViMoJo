package com.videonasocialmedia.videonamediaframework.pipeline;

/**
 * Created by alvaro on 25/10/16.
 */
public interface ExporterVideoSwapAudio {
  void export(String videoFilePath, String newAudioFilePath, String outputFilePath);
}
