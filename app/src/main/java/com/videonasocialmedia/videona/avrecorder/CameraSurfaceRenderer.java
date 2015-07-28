package com.videonasocialmedia.videona.avrecorder;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.avrecorder.gles.FullFrameRect;
import com.videonasocialmedia.videona.avrecorder.gles.GlUtil;
import com.videonasocialmedia.videona.avrecorder.gles.Texture2dProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @hide
 */
class CameraSurfaceRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "CameraSurfaceRenderer";
    private static final boolean VERBOSE = true;
    private final float[] mSTMatrix = new float[16];
    boolean showBox = false;
    Context context;
    private CameraEncoder mCameraEncoder;
    private FullFrameRect mFullScreenCamera;
    private FullFrameRect mFullScreenOverlay;     // For texture overlay
    private int mOverlayTextureId;
    private int mCameraTextureId;
    private boolean mRecordingEnabled;
    private int mFrameCount;
    // Keep track of selected filters + relevant state
    private boolean mIncomingSizeUpdated;
    private int mIncomingWidth;
    private int mIncomingHeight;
    private int mCurrentFilter;
    private int mNewFilter;


    /**
     * Constructs CameraSurfaceRenderer.
     * <p/>
     *
     * @param recorder video encoder object
     */
    public CameraSurfaceRenderer(CameraEncoder recorder, Context context) {
        mCameraEncoder = recorder;

        mCameraTextureId = -1;
        mFrameCount = -1;

        SessionConfig config = recorder.getConfig();
        mIncomingWidth = config.getVideoWidth();
        mIncomingHeight = config.getVideoHeight();
        mIncomingSizeUpdated = true;        // Force texture size update on next onDrawFrame

        mCurrentFilter = -1;
        mNewFilter = Filters.FILTER_NONE;

        mRecordingEnabled = false;
        this.context = context;
    }


    /**
     * Notifies the renderer that we want to stop or start recording.
     */
    public void changeRecordingState(boolean isRecording) {
        Log.d(TAG, "changeRecordingState: was " + mRecordingEnabled + " now " + isRecording);
        mRecordingEnabled = isRecording;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");
        // Set up the texture blitter that will be used for on-screen display.  This
        // is *not* applied to the recording, because that uses a separate shader.
        mFullScreenCamera = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
        // For texture overlay:
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        mFullScreenOverlay = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));
        mOverlayTextureId = GlUtil.createTextureFromImage(context, R.drawable.watermark720);
        //mOverlayTextureId = GlUtil.createTextureWithTextContent("HolaMundo");
        mCameraTextureId = mFullScreenCamera.createTextureObject();
        mCameraEncoder.onSurfaceCreated(mCameraTextureId, mOverlayTextureId);
        mFrameCount = 0;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        Log.d(TAG, "onSurfaceChanged " + width + "x" + height);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if (VERBOSE) {
            if (mFrameCount % 30 == 0) {
                Log.d(TAG, "onDrawFrame tex=" + mCameraTextureId);
                mCameraEncoder.logSavedEglState();
            }
        }

        if (mCurrentFilter != mNewFilter) {
            Filters.updateFilter(mFullScreenCamera, mNewFilter);
            mCurrentFilter = mNewFilter;
            mIncomingSizeUpdated = true;
        }

        if (mIncomingSizeUpdated) {
            mFullScreenCamera.getProgram().setTexSize(mIncomingWidth, mIncomingHeight);
            mFullScreenOverlay.getProgram().setTexSize(mIncomingWidth, mIncomingHeight);
            mIncomingSizeUpdated = false;
            Log.i(TAG, "setTexSize on display Texture");
        }


        // Draw the video frame.
        if (mCameraEncoder.isSurfaceTextureReadyForDisplay()) {
            mCameraEncoder.getSurfaceTextureForDisplay().updateTexImage();
            mCameraEncoder.getSurfaceTextureForDisplay().getTransformMatrix(mSTMatrix);
            // Drawing texture overlay:
            // GLES20.glViewport(0, 0, 1920, 1080);
            mFullScreenCamera.drawFrame(mCameraTextureId, mSTMatrix);
            // GLES20.glViewport(0, 0, 150, 150);

            // mFullScreenOverlay.drawFrame(mOverlayTextureId, mSTMatrix);
        }
        mFrameCount++;
    }


    public void signalVertialVideo(FullFrameRect.SCREEN_ROTATION isVertical) {
        if (mFullScreenCamera != null) mFullScreenCamera.adjustForVerticalVideo(isVertical, false);
    }

    /**
     * Changes the filter that we're applying to the camera preview.
     */
    public void changeFilterMode(int filter) {
        mNewFilter = filter;
    }

    public void handleTouchEvent(MotionEvent ev) {
        mFullScreenCamera.handleTouchEvent(ev);
    }

}