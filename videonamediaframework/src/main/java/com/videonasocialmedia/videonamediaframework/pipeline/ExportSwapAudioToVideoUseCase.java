package com.videonasocialmedia.videonamediaframework.pipeline;

import android.util.Log;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alvaro on 23/10/16.
 */

public class ExportSwapAudioToVideoUseCase implements ExporterVideoSwapAudio {

  private final OnExportEndedSwapAudioListener onExportEndedSwapAudioListener;
  private String audioFilePath;

  public ExportSwapAudioToVideoUseCase(OnExportEndedSwapAudioListener
                                           onExportEndedSwapAudioListener) {
    this.onExportEndedSwapAudioListener = onExportEndedSwapAudioListener;
  }

  @Override
  public void export(String videoFilePath, String newAudioFilePath, String outputFilePath) {

    this.audioFilePath = newAudioFilePath;

    Movie result = null;
    try {
      result = getFinalMovie(videoFilePath, newAudioFilePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (result != null) {
      saveFinalVideo(result, outputFilePath);
      //tils.cleanDirectory(new File(videoExportedTempPath));
    }
  }


  private Movie getFinalMovie(String videoFilePath, String newAudioFilePath) throws IOException {
    Movie result;
    Movie movie = MovieCreator.build(videoFilePath);

    File musicFile = new File(newAudioFilePath);
    if (musicFile == null) {
      onExportEndedSwapAudioListener.onExportError("Music not found");
    }
    ArrayList<String> audio = new ArrayList<>();
    audio.add(musicFile.getPath());
    double movieDuration = getMovieDuration(movie);
    result = swapAudio(movie, newAudioFilePath, movieDuration);

    return result;
  }

  private Movie swapAudio(Movie movie, String audioPath, double movieDuration) throws IOException {

    Movie finalMovie = new Movie();

    List<Track> videoTrack = new LinkedList<>();

    for (Track t : movie.getTracks()) {
      if (t.getHandler().equals("vide")) {
        videoTrack.add(t);
      }
    }

    Movie audioMovie = MovieCreator.build(audioPath);
    List<Track> audioTracks = new LinkedList<>();

    for (Track t : audioMovie.getTracks()) {
      if (t.getHandler().equals("soun")) {
        audioTracks.add(t);
      }
    }

    finalMovie.addTrack(new AppendTrack(videoTrack.toArray(new Track[videoTrack.size()])));
    finalMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));

    return finalMovie;

  }

  private double getMovieDuration(Movie movie) {
    double movieDuration = movie.getTracks().get(0).getDuration();
    double timeScale = movie.getTimescale();
    movieDuration = movieDuration / timeScale * 1000;
    return movieDuration;
  }


  private void saveFinalVideo(Movie result, String outputFilePath) {
    try {
      long start = System.currentTimeMillis();
      com.videonasocialmedia.videonamediaframework.muxer.utils.Utils.createFile(result, outputFilePath);
      long spent = System.currentTimeMillis() - start;
      Log.d("WRITING VIDEO FILE", "time spent in millis: " + spent);
      onExportEndedSwapAudioListener.onExportSuccess();
      deleteAudioTempFile();
    } catch (IOException | NullPointerException e) {
      onExportEndedSwapAudioListener.onExportError(String.valueOf(e));
    }
  }

  private void deleteAudioTempFile() {
    new File(audioFilePath).deleteOnExit();
  }


  /**
   * Created by jca on 27/5/15.
   */
  public static interface OnExportEndedSwapAudioListener {
      void onExportError(String error);
      void onExportSuccess();
  }
}
