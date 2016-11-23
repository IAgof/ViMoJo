package com.videonasocialmedia.videonamediaframework.pipeline;

import android.util.Log;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.videonasocialmedia.videonamediaframework.muxer.Appender;
import com.videonasocialmedia.videonamediaframework.muxer.AudioTrimmer;
import com.videonasocialmedia.videonamediaframework.muxer.Trimmer;
import com.videonasocialmedia.videonamediaframework.muxer.VideoTrimmer;
import com.videonasocialmedia.videonamediaframework.muxer.utils.Utils;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.utils.FileUtils;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Juan Javier Cabanas
 * @author Verónica Lago Fominaya
 */
public class ExporterImpl implements Exporter {

    private static final int MAX_SECONDS_WAITING_FOR_TEMP_FILES = 600;
    private final String tempFilesDirectory;
    private String tempVideoExportedPath;
    private String tempTranscodePath;
    private static final String TAG = "Exporter implementation";

    private OnExportEndedListener onExportEndedListener;
    private final VMComposition vMComposition;
    private boolean trimCorrect = true;
    private boolean transcodeCorrect = true;
    private ArrayList<String> videoTranscoded;
    private int numFilesToTranscoder = 1;
    private int numFilesTranscoded = 0;
    private Profile profile;

    public ExporterImpl(String tempFilesDirectory, VMComposition vmComposition, Profile profile,
                        OnExportEndedListener onExportEndedListener) {
        this.onExportEndedListener = onExportEndedListener;
        this.vMComposition = vmComposition;
        this.profile = profile;
        this.tempFilesDirectory = tempFilesDirectory;
        tempTranscodePath = tempFilesDirectory  + File.separator + "transcode";
        tempVideoExportedPath = tempFilesDirectory + File.separator + "export";
    }

    @Override
    public void export() {
        waitForOutputFilesFinished();

        LinkedList<Media> medias = getMediasFromComposition();
        ArrayList<String> videoTrimmedPaths = createVideoPathList(medias);

        Movie result = appendFiles(videoTrimmedPaths);
        if (result != null) {
            saveFinalVideo(result);
            FileUtils.cleanDirectory(new File(tempVideoExportedPath));
        }
    }

    private ArrayList<String> createVideoPathList(LinkedList<Media> medias) {
        ArrayList <String> result = new ArrayList<>();
        for (Media media:medias) {
            Video video= (Video) media;
            if (video.isEdited()){
                result.add(video.getTempPath());
            } else {
                result.add(video.getMediaPath());
            }
        }
        return result;
    }

    private LinkedList<Media> getMediasFromComposition() {
        LinkedList<Media> medias = vMComposition.getMediaTrack().getItems();
        return medias;
    }

    // TODO(jliarte): 17/11/16 check if this code is still relevant
    private ArrayList<String> trimVideos(LinkedList<Media> medias) {
        final File tempDir = new File(tempVideoExportedPath);
        if (!tempDir.exists())
            tempDir.mkdirs();
        ArrayList<String> videoTrimmedPaths = new ArrayList<>();
        Trimmer trimmer;
        Movie movie;
        int index = 0;
        do {
            try {
                String videoTrimmedTempPath = tempVideoExportedPath
                        + File.separator + "video_trimmed_" + index + ".mp4";
                int startTime = medias.get(index).getStartTime();
                int endTime = medias.get(index).getStopTime();
                int editedFileDuration = medias.get(index).getStopTime()
                        - medias.get(index).getStartTime();
                int originalFileDuration = ( (Video) medias.get(index) ).getFileDuration();
                if (editedFileDuration < originalFileDuration) {
                    trimmer = new VideoTrimmer();
                    movie = trimmer.trim(medias.get(index).getMediaPath(), startTime, endTime);
                    Utils.createFile(movie, videoTrimmedTempPath);
                    videoTrimmedPaths.add(videoTrimmedTempPath);
                } else {
                    videoTrimmedPaths.add(medias.get(index).getMediaPath());
                }
            } catch (IOException | NullPointerException e) {
                trimCorrect = false;
                videoTrimmedPaths = null;
                onExportEndedListener.onExportError(String.valueOf(e));
            }
            index++;
        } while (trimCorrect && medias.size() > index);

        return videoTrimmedPaths;
    }

    private Movie appendFiles(ArrayList<String> videoTranscoded) {
        Movie result;
        if (vMComposition.hasMusic()) {
            Movie merge = appendVideos(videoTranscoded, false);

//            Music music = (Music) project.getAudioTracks().get(0).getItems().getFirst();
            Music music = vMComposition.getMusic();
            // TODO(alvaro) 060616 check if music is downloaded in a repository, not here.
//            File musicFile = Utils.getMusicFileByName(music.getMusicTitle(),
//                    music.getMusicResourceId());
            File musicFile = new File(music.getMediaPath());
            if (musicFile == null) {
                onExportEndedListener.onExportError("Music not found");
            }
            ArrayList<String> audio = new ArrayList<>();
            audio.add(musicFile.getPath());
            double movieDuration = getMovieDuration(merge);
            result = addAudio(merge, audio, movieDuration);
        } else {
            result = appendVideos(videoTranscoded, true);
        }
        return result;
    }

    private void saveFinalVideo(Movie result) {
        try {
            long start = System.currentTimeMillis();
            String pathVideoEdited = tempFilesDirectory + File.separator + "V_EDIT_"
                    + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
            Utils.createFile(result, pathVideoEdited);
            long spent = System.currentTimeMillis() - start;
            Log.d("WRITING VIDEO FILE", "time spent in millis: " + spent);
            onExportEndedListener.onExportSuccess(new Video(pathVideoEdited));
        } catch (IOException | NullPointerException e) {
            onExportEndedListener.onExportError(String.valueOf(e));
        }
    }

    private Movie appendVideos(ArrayList<String> videoTranscodedPaths, boolean addOriginalAudio) {
        Appender appender = new Appender();
        Movie merge;
        try {
            merge = appender.appendVideos(videoTranscodedPaths, addOriginalAudio);
        } catch (Exception e) {
            merge = null;
            onExportEndedListener.onExportError(String.valueOf(e));
        }
        return merge;
    }

    private double getMovieDuration(Movie mergedVideoWithoutAudio) {
        double movieDuration = mergedVideoWithoutAudio.getTracks().get(0).getDuration();
        double timeScale = mergedVideoWithoutAudio.getTimescale();
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

    private void waitForOutputFilesFinished() {
        LinkedList<Media> medias = getMediasFromComposition();
        int countWaiting = 0;
        for (Media media : medias) {
            Video video = (Video) media;
            if (video.isEdited()) {
                while (!video.outputVideoIsFinished()) {
                    try {
                        if (countWaiting > MAX_SECONDS_WAITING_FOR_TEMP_FILES) {
                            break;
                        }
                        countWaiting++;
                        Thread.sleep(1000);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
}