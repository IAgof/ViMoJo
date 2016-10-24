package com.videonasocialmedia.avrecorder;

import java.io.File;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Configuration information for a Broadcasting or Recording session.
 * Includes meta data, video + audio encoding
 * and muxing parameters
 */
public class SessionConfig {

    private VideoEncoderConfig mVideoConfig;
    private AudioEncoderConfig mAudioConfig;
    private File mOutputDirectory;
    private Muxer mMuxer;

    /**
     * Creates a new session configuration to record
     *
     * @param destinationFolderPath the folder where the video should be recorded;
     */
    public SessionConfig(String destinationFolderPath) {
        mVideoConfig = new VideoEncoderConfig(1280, 720, 10 * 1000 * 1000, 25);
        mAudioConfig = new AudioEncoderConfig(1, 48000, 192 * 1000);
        File outputFile = createOutputFile(destinationFolderPath);
        mMuxer = AndroidMuxer.create(outputFile.getAbsolutePath(), Muxer.FORMAT.MPEG4);
    }

    public SessionConfig(String destinationFolderPath, VideoEncoderConfig videoEncoderConfig){
        mVideoConfig = new VideoEncoderConfig(videoEncoderConfig.getWidth(), videoEncoderConfig.getHeight(),
                videoEncoderConfig.getBitRate(), videoEncoderConfig.getFrameRate());
        mAudioConfig = new AudioEncoderConfig(1, 48000, 192 * 1000);
        File outputFile = createOutputFile(destinationFolderPath);
        mMuxer = AndroidMuxer.create(outputFile.getAbsolutePath(), Muxer.FORMAT.MPEG4);
    }

    public SessionConfig(String destinationFolderPath, int numTracks) {
        mVideoConfig = new VideoEncoderConfig(1280, 720, 10 * 1000 * 1000, 25);
        mAudioConfig = new AudioEncoderConfig(1, 48000, 192 * 1000);
        File outputFile = createOutputFile(destinationFolderPath);
        mMuxer = AndroidMuxer.create(outputFile.getAbsolutePath(), Muxer.FORMAT.MPEG4, numTracks);
    }

    /**
     *
     * @param destinationFolderPath the folder where the video should be recorded;
     * @param width the width of the video in pixels
     * @param height the height of the video in pixels
     * @param videoBitrate
     * @param frameRate
     * @param audioChannels 1 or 2 channels
     * @param audioFrequency usually 48000 Hz o 441000 Hz
     * @param audioBitrate
     */
    public SessionConfig(String destinationFolderPath, int width, int height, int videoBitrate, int frameRate,
                         int audioChannels, int audioFrequency, int audioBitrate){
        mVideoConfig = new VideoEncoderConfig(width, height, videoBitrate, frameRate);
        mAudioConfig = new AudioEncoderConfig(audioChannels, audioFrequency, audioBitrate);
        File outputFile = createOutputFile(destinationFolderPath);
        mMuxer = AndroidMuxer.create(outputFile.getAbsolutePath(), Muxer.FORMAT.MPEG4);
    }


    public SessionConfig(Muxer muxer, VideoEncoderConfig videoConfig, AudioEncoderConfig audioConfig) {
        mVideoConfig = checkNotNull(videoConfig);
        mAudioConfig = checkNotNull(audioConfig);
        mMuxer = checkNotNull(muxer);
    }

    private File createOutputFile(String path) {
        // Not time stamp, reuse name
        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String fileName = "VID_temp.mp4";
        File rootDir = new File(path);
        rootDir.mkdir();
        File vTemp = new File(rootDir, fileName);
        if(vTemp.exists()) {
            vTemp.delete(); // Delete old temp files.
        }
        return new File(rootDir, fileName);
    }

    public VideoEncoderConfig getVideoConfig(){
        return mVideoConfig;
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

    public int getTotalBitrate() {
        return mVideoConfig.getBitRate() + mAudioConfig.getBitrate();
    }

    public int getVideoWidth() {
        return mVideoConfig.getWidth();
    }

    public int getVideoHeight() {
        return mVideoConfig.getHeight();
    }

    public int getVideoBitRate() {
        return mVideoConfig.getBitRate();
    }

    public int getVideoFrameRate() {
        return mVideoConfig.getFrameRate();
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
