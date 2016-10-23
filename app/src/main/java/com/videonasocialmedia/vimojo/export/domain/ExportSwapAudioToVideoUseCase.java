package com.videonasocialmedia.vimojo.export.domain;

import android.util.Log;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.videonasocialmedia.muxer.AudioTrimmer;
import com.videonasocialmedia.muxer.Trimmer;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alvaro on 23/10/16.
 */

public class ExportSwapAudioToVideoUseCase implements Exporter{

    private final OnExportEndedListener onExportEndedListener;

    public ExportSwapAudioToVideoUseCase(OnExportEndedListener onExportEndedListener){
        this.onExportEndedListener = onExportEndedListener;
    }

    @Override
    public void export(String videoFilePath, String newAudioFilePath, String outputFilePath) {


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
                onExportEndedListener.onExportError("Music not found");
            }
            ArrayList<String> audio = new ArrayList<>();
            audio.add(musicFile.getPath());
            double movieDuration = getMovieDuration(movie);
            result = addAudio(movie, audio, movieDuration);

        return result;
    }

    private double getMovieDuration(Movie movie) {
        double movieDuration = movie.getTracks().get(0).getDuration();
        double timeScale = movie.getTimescale();
        movieDuration = movieDuration / timeScale * 1000;
        return movieDuration;
    }


    private Movie addAudio(Movie movie, ArrayList<String> audioPaths, double movieDuration) {
        ArrayList<Movie> audioList = new ArrayList<>();
        List<Track> audioTracks = new LinkedList<>();
        Trimmer trimmer = new AudioTrimmer();

        // TODO change this for do while
        for (String audio : audioPaths) {
            try {
                audioList.add(trimmer.trim(audio, 0, movieDuration));
            } catch (IOException | NullPointerException e) {
                onExportEndedListener.onExportError(String.valueOf(e));
            }
        }

        for (Movie m : audioList) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
            }
        }

        if (audioTracks.size() > 0) {
            try {
                movie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            } catch (IOException | NullPointerException e) {
                onExportEndedListener.onExportError(String.valueOf(e));
                // TODO se debe continuar sin música o lo paro??
            }
        }

        return movie;
    }

    private void saveFinalVideo(Movie result, String outputFilePath) {
        try {
            long start = System.currentTimeMillis();
            com.videonasocialmedia.muxer.utils.Utils.createFile(result, outputFilePath);
            long spent = System.currentTimeMillis() - start;
            Log.d("WRITING VIDEO FILE", "time spent in millis: " + spent);
            onExportEndedListener.onExportSuccess(new Video(outputFilePath));
        } catch (IOException | NullPointerException e) {
            onExportEndedListener.onExportError(String.valueOf(e));
        }
    }


}
