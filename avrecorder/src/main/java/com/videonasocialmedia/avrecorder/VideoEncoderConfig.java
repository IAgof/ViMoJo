package com.videonasocialmedia.avrecorder;

/**
 * @hide
 */
public class VideoEncoderConfig {
    protected int width;
    protected int height;
    protected int bitRate;
    protected int frameRate;

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

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }
}