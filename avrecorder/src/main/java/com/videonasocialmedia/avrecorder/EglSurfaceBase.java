/* * Copyright 2013 Google Inc. All rights reserved. * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *      http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package com.videonasocialmedia.avrecorder;import android.graphics.Bitmap;import android.graphics.Matrix;import android.opengl.EGL14;import android.opengl.EGLSurface;import android.opengl.GLES20;import android.util.Log;import java.io.BufferedOutputStream;import java.io.File;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.IOException;import java.nio.ByteBuffer;import java.nio.ByteOrder;/** * Common base class for EGL surfaces. * <p/> * There can be multiple surfaces associated with a single context. * @hide */public class EglSurfaceBase {    protected static final String TAG = "EglSurfaceBase";    // EglBase object we're associated with.  It may be associated with multiple surfaces.    protected EglCore mEglCore;    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;    private int mWidth = -1;    private int mHeight = -1;    protected EglSurfaceBase(EglCore eglBase) {        mEglCore = eglBase;    }    /**     * Creates a window surface.     * <p/>     *     * @param surface May be a Surface or SurfaceTexture.     */    public void createWindowSurface(Object surface) {        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {            throw new IllegalStateException("surface already created");        }        mEGLSurface = mEglCore.createWindowSurface(surface);        mWidth = mEglCore.querySurface(mEGLSurface, EGL14.EGL_WIDTH);        mHeight = mEglCore.querySurface(mEGLSurface, EGL14.EGL_HEIGHT);    }    /**     * Creates an off-screen surface.     */    public void createOffscreenSurface(int width, int height) {        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {            throw new IllegalStateException("surface already created");        }        mEGLSurface = mEglCore.createOffscreenSurface(width, height);        mWidth = width;        mHeight = height;    }    /**     * Returns the surface's width, in pixels.     */    public int getWidth() {        return mWidth;    }    /**     * Returns the surface's height, in pixels.     */    public int getHeight() {        return mHeight;    }    /**     * Release the EGL surface.     */    public void releaseEglSurface() {        mEglCore.releaseSurface(mEGLSurface);        mEGLSurface = EGL14.EGL_NO_SURFACE;        mWidth = mHeight = -1;    }    /**     * Makes our EGL context and surface current.     */    public void makeCurrent() {        mEglCore.makeCurrent(mEGLSurface);    }    /**     * Makes our EGL context and surface current for drawing, using the supplied surface     * for reading.     */    public void makeCurrentReadFrom(EglSurfaceBase readSurface) {        mEglCore.makeCurrent(mEGLSurface, readSurface.mEGLSurface);    }    /**     * Calls eglSwapBuffers.  Use this to "publish" the current frame.     *     * @return false on failure     */    public boolean swapBuffers() {        boolean result = mEglCore.swapBuffers(mEGLSurface);        if (!result) {            Log.d(TAG, "WARNING: swapBuffers() failed");        }        return result;    }    /**     * Sends the presentation time stamp to EGL.     *     * @param nsecs Timestamp, in nanoseconds.     */    public void setPresentationTime(long nsecs) {        mEglCore.setPresentationTime(mEGLSurface, nsecs);    }    /**     * Saves the EGL surface to a file.     * <p/>     * Expects that this object's EGL surface is current.     */    public void saveFrame(File file, final int scaleFactor) throws IOException {        if (!mEglCore.isCurrent(mEGLSurface)) {            throw new RuntimeException("Expected EGL context/surface is not current");        }        // glReadPixels gives us a ByteBuffer filled with what is essentially big-endian RGBA        // data (i.e. a byte of red, followed by a byte of green...).  We need an int[] filled        // with little-endian ARGB data to feed to Bitmap.        //        // If we implement this as a series of buf.get() calls, we can spend 2.5 seconds just        // copying data around for a 720p frame.  It's better to do a bulk get() and then        // rearrange the data in memory.  (For comparison, the PNG compress takes about 500ms        // for a trivial frame.)        //        // So... we set the ByteBuffer to little-endian, which should turn the bulk IntBuffer        // get() into a straight memcpy on most Android devices.  Our ints will hold ABGR data.        // Swapping B and R gives us ARGB.        //        // Making this even more interesting is the upside-down nature of GL, which means        // our output will look upside-down relative to what appears on screen if the        // typical GL conventions are used.        final long startTime = System.currentTimeMillis();        final String filename = file.toString();        final ByteBuffer buf = ByteBuffer.allocateDirect(mWidth * mHeight * 4);        buf.order(ByteOrder.LITTLE_ENDIAN);        GLES20.glReadPixels(0, 0, mWidth, mHeight,                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);        buf.rewind();        new Thread(new Runnable() {            @Override            public void run() {                BufferedOutputStream bos = null;                try {                    bos = new BufferedOutputStream(new FileOutputStream(filename));                    Bitmap fullBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);                    fullBitmap.copyPixelsFromBuffer(buf);                    Matrix m = new Matrix();                    m.preScale(1, -1);                    if (scaleFactor != 1) {                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, mWidth / scaleFactor, mHeight / scaleFactor, true);                        Bitmap flippedScaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), m, true);                        flippedScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);                        scaledBitmap.recycle();                        flippedScaledBitmap.recycle();                    } else {                        Bitmap flippedBitmap = Bitmap.createBitmap(fullBitmap, 0, 0, mWidth, mHeight, m, true);                        flippedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);                    }                    fullBitmap.recycle();                    Log.d(TAG, "Saved " + mWidth / scaleFactor + "x" + mHeight / scaleFactor + " frame as '" + filename + "' in " + (System.currentTimeMillis() - startTime) + " ms");                } catch (FileNotFoundException e) {                    e.printStackTrace();                } finally {                    if (bos != null) try {                        bos.close();                    } catch (IOException e) {                        e.printStackTrace();                    }                }            }        }).start();    }}