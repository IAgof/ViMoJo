package com.videonasocialmedia.avrecorder;

/**
 * @hide
 */
public class VideoEncoderConfig {
    protected final int width;
    protected final int height;
    protected final int bitRate;
    protected final int frameRate;

    public VideoEncoderConfig(int width, int height, int bitRate, int frameRate) {
        this.width = width;
        this.height = height;
        this.bitRate = bitRate;
        this.frameRate = frameRate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBitRate() {
        return bitRate;
    }

    public int getFrameRate() {
        return frameRate;
    }

    @Override
    public String toString() {
        return "VideoEncoderConfig: " + width + "x" + height + " @" + bitRate + " bps";
    }
}