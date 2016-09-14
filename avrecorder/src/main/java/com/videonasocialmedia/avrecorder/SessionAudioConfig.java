package com.videonasocialmedia.avrecorder;

import java.io.File;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Configuration information for a Broadcasting or Recording session.
 * Includes meta data, video + audio encoding
 * and muxing parameters
 */
public class SessionAudioConfig {

    private final AudioEncoderConfig mAudioConfig;
    private File mOutputDirectory;
    private Muxer mMuxer;

    /**
     * Creates a new session configuration to record
     *
     * @param destinationFolderPath the folder where the video should be recorded;
     */
    public SessionAudioConfig(String destinationFolderPath) {

        mAudioConfig = new AudioEncoderConfig(1, 48000, 192 * 1000);
        File outputFile = createOutputFile(destinationFolderPath);
        mMuxer = AndroidMuxer.create(outputFile.getAbsolutePath(), Muxer.FORMAT.MPEG4);
    }

    /**
     * @param destinationFolderPath the folder where the video should be recorded;
     * @param audioChannels 1 or 2 channels
     * @param audioFrequency usually 48000 Hz o 441000 Hz
     * @param audioBitrate
     */
    public SessionAudioConfig(String destinationFolderPath,
                              int audioChannels, int audioFrequency, int audioBitrate){
        mAudioConfig = new AudioEncoderConfig(audioChannels, audioFrequency, audioBitrate);
        File outputFile = createOutputFile(destinationFolderPath);
        mMuxer = AndroidMuxer.create(outputFile.getAbsolutePath(), Muxer.FORMAT.MPEG4);
    }

    public SessionAudioConfig(Muxer muxer, AudioEncoderConfig audioConfig) {
        mAudioConfig = checkNotNull(audioConfig);
        mMuxer = checkNotNull(muxer);
    }

    private File createOutputFile(String path) {
        // Not time stamp, reuse name
        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String fileName = "AUD_temp.m4a";
        File rootDir = new File(path);
        rootDir.mkdir();
        File vTemp = new File(rootDir, fileName);
        if(vTemp.exists()) {
            vTemp.delete(); // Delete old temp files.
        }
        return new File(rootDir, fileName);
    }

    public Muxer getMuxer() {
        return mMuxer;
    }

    public File getOutputDirectory() {
        return mOutputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        mOutputDirectory = outputDirectory;
    }

    public String getOutputPath() {
        return mMuxer.getOutputPath();
    }

    public int getNumAudioChannels() {
        return mAudioConfig.getNumChannels();
    }

    public int getAudioBitrate() {
        return mAudioConfig.getBitrate();
    }

    public int getAudioSamplerate() {
        return mAudioConfig.getSampleRate();
    }

}
