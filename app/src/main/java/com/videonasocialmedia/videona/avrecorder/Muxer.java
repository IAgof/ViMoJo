package com.videonasocialmedia.videona.avrecorder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Base Muxer class for interaction with MediaCodec based
 * encoders
 * @hide
 */
public abstract class Muxer {
    private static final String TAG = "Muxer";

    public enum FORMAT { MPEG4 }

    private final int mExpectedNumTracks = 2;           // TODO: Make this configurable?

    protected FORMAT mFormat;
    protected String mOutputPath;
    protected int mNumTracks;
    protected int mNumTracksFinished;
    protected long mFirstPts;
    protected long mLastPts[];

    protected Muxer(String outputPath, FORMAT format){
        Log.i(TAG, "Created muxer for output: " + outputPath);
        mOutputPath = checkNotNull(outputPath);
        mFormat = format;
        mNumTracks = 0;
        mNumTracksFinished = 0;
        mFirstPts = 0;
        mLastPts = new long[mExpectedNumTracks];
        for(int i=0; i< mLastPts.length; i++) {
            mLastPts[i] = 0;
        }
    }

    /**
     * Returns the absolute output path.
     *
     * e.g /sdcard/app/uuid/index.m3u8
     * @return
     */
    public String getOutputPath(){
        return mOutputPath;
    }

    /**
     * Adds the specified track and returns the track index
     *
     * @param trackFormat MediaFormat of the track to add. Gotten from MediaCodec#dequeueOutputBuffer
     *                    when returned status is INFO_OUTPUT_FORMAT_CHANGED
     * @return index of track in output file
     */
    public int addTrack(MediaFormat trackFormat){
        mNumTracks++;
        return mNumTracks - 1;
    }

    /**
     * Called by the hosting Encoder
     * to notify the Muxer that it should no
     * longer assume the Encoder resources are available.
     *
     */
    public void onEncoderReleased(int trackIndex){
    }

    public void release(){
      //  if(mEventBus != null)
      //      mEventBus.post(new MuxerFinishedEvent());
        //TODO MuxerFinishedEvent
        Log.d("Muxer", "release recorder");
       // RecordPresenter.listenerReleaseRecorder.onReleaseRecord();
    }

    public boolean isStarted(){
        return false;
    }

    /**
     * Write the MediaCodec output buffer. This method <b>must</b>
     * be overridden by subclasses to release encodedData, transferring
     * ownership back to encoder, by calling encoder.releaseOutputBuffer(bufferIndex, false);
     *
     * @param trackIndex
     * @param encodedData
     * @param bufferInfo
     */
    public void writeSampleData(MediaCodec encoder, int trackIndex, int bufferIndex, ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo){
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            signalEndOfTrack();
        }
    }

    public abstract void forceStop();

    protected boolean allTracksFinished(){
        return (mNumTracks == mNumTracksFinished);
    }

    protected boolean allTracksAdded(){
        return (mNumTracks == mExpectedNumTracks);
    }

    /**
     * Muxer will call this itself if it detects BUFFER_FLAG_END_OF_STREAM
     * in writeSampleData.
     */
    protected void signalEndOfTrack(){
        mNumTracksFinished++;
    }


    /**
     * Return a relative pts given an absolute pts and trackIndex.
     *
     * This method advances the state of the Muxer, and must only
     * be called once per call to {@link #writeSampleData(MediaCodec, int, int, ByteBuffer, MediaCodec.BufferInfo)}.
    */
    protected long getNextRelativePts(long absPts, int trackIndex) {
        if (mFirstPts == 0) {
            mFirstPts = absPts;
            return 0;
        }
        return getSafePts(absPts - mFirstPts, trackIndex);
    }
    
    /**
     * Sometimes packets with non-increasing pts are dequeued from the MediaCodec output buffer.
     * This method ensures that a crash won't occur due to non monotonically increasing packet timestamp.
     */
    private long getSafePts(long pts, int trackIndex) {
        if (mLastPts[trackIndex] >= pts) {
            // Enforce a non-zero minimum spacing
            // between pts
            mLastPts[trackIndex] += 9643;
            return mLastPts[trackIndex];
        }
        mLastPts[trackIndex] = pts;
        return pts;
    }
}
