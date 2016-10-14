/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

import com.coremedia.iso.IsoFile;
import com.videonasocialmedia.vimojo.VimojoApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Locale;

/**
 * Utils.
 */
public class Utils {
    /**
     * Checks if there is sufficient space to put the input size in the directory
     *
     * @param size the size in megabytes
     * @return boolean true if there is sufficient space, false if not
     */
    public static boolean isAvailableSpace(float size) {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = stat.getAvailableBytes();
        float megabytesAvailable = bytesAvailable / (1024.f * 1024.f);

        return size <= megabytesAvailable;
    }

    public static void copyResourceToTemp(Context ctx, String name, int musicResourceId,
                                          String fileTypeExtensionConstant) throws IOException {
        String nameFile = String.valueOf(name);
        File file = new File(Constants.PATH_APP_TEMP + File.separator + nameFile +
                fileTypeExtensionConstant);

        if (!file.exists() || !file.isFile()) {
            if (!file.isFile())
                file.delete();
            InputStream in = ctx.getResources().openRawResource(musicResourceId);
            try {
                FileOutputStream out = new FileOutputStream(Constants.PATH_APP_TEMP + File.separator +
                        nameFile + fileTypeExtensionConstant);
                byte[] buff = new byte[1024];
                int read = 0;
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
                out.close();
            } catch (FileNotFoundException e) {
                //TODO show error message
            } finally {
                in.close();
            }
        }
    }

    public static void copyMusicResourceToTemp(Context ctx, String name, int musicResourceId) throws IOException {
        copyResourceToTemp(ctx, name, musicResourceId, Constants.AUDIO_MUSIC_FILE_EXTENSION);
    }

    public static File getMusicFileByName(String musicName, int musicResourceId) {
        File f = new File(Constants.PATH_APP_TEMP + File.separator + musicName + Constants.AUDIO_MUSIC_FILE_EXTENSION);
        if (!f.exists()) {
            try {
                copyMusicResourceToTemp(VimojoApplication.getAppContext(), musicName, musicResourceId);
            } catch (IOException e) {
                //TODO show error message
                f = null;
            }
        }
        return f;
    }

    public static void copyFile(String originalPath, String finalPath) throws IOException {
        File originalFile = new File(originalPath);
        File destinationFile = new File(finalPath);

        if (originalFile.exists() && originalFile.isFile()) {
            FileChannel inChannel = new FileInputStream(originalFile).getChannel();
            FileChannel outChannel = new FileOutputStream(destinationFile).getChannel();
            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } finally {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }
        }
    }

    public static Uri obtainUriToShare(Context context, String videoPath) {
        Uri uri;
        if (videoPath != null) {
            ContentResolver resolver = context.getContentResolver();
            uri = getUriFromContentProvider(resolver, videoPath);
            if (uri == null) {
                uri = createUriToShare(resolver, videoPath);
            }
        } else {
            uri = null;
        }
        return uri;
    }

    private static Uri createUriToShare(ContentResolver resolver, String videoPath) {
        ContentValues content = new ContentValues(4);
        content.put(MediaStore.Video.VideoColumns.TITLE, videoPath);
        content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                System.currentTimeMillis());
        content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        content.put(MediaStore.Video.Media.DATA, videoPath);
        return resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                content);
    }

    private static Uri getUriFromContentProvider(ContentResolver resolver, String videoPath) {
        Uri uri = null;
        String[] retCol = {MediaStore.Audio.Media._ID};
        Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                retCol,
                MediaStore.MediaColumns.DATA + "='" + videoPath + "'", null, null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id);
            cursor.close();
        }
        return uri;
    }

    /**
     * Returns whether the current device is running Android 4.4, KitKat, or newer
     */
    public static boolean isKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static void cleanDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) { //some JVMs return null for empty dirs
                for (File f : files) {
                    if (f.isDirectory()) {
                        cleanDirectory(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
    }

    public static void removeVideo(String path) {
        File file = new File(path);
        if (file != null) {
            if (file.isDirectory()) {
                cleanDirectory(file);
            } else {
                file.delete();
            }
        }
    }

    // Glide circle imageView
    public static Bitmap getCircularBitmapImage(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP,
                BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        squaredBitmap.recycle();
        //return addWhiteBorder(bitmap, 10);
        return bitmap;
    }

    private static Bitmap addWhiteBorderToBitmap (Bitmap bmp, int borderSize) {
        int size = Math.min(bmp.getWidth(), bmp.getHeight());
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2,
                bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(bmp, BitmapShader.TileMode.CLAMP,
                BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        float r = size / 2f;
       // canvas.drawCircle(r, r, r, paint);
        bmp.recycle();
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bmp, borderSize, borderSize, paint);
        return bmpWithBorder;
    }

    private Bitmap addWhiteBorderOriginal(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2,
                bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }


    public static String getDeviceInfo(){
        StringBuilder deviceInfo = new StringBuilder();

        deviceInfo.append(" ---------------------------------------------");
        deviceInfo.append(" Brand: " + android.os.Build.BRAND);
        deviceInfo.append(" Device: " + android.os.Build.DEVICE);
        deviceInfo.append(" Model: " + android.os.Build.MODEL);
        deviceInfo.append(" Hardware: " + android.os.Build.HARDWARE);
        deviceInfo.append(" Language: " + Locale.getDefault().getLanguage());


        return  deviceInfo.toString();
    }

    //   PackageInfo infor = getApplication().getPackageManager().getPackageInfo("com.videonasocialmedia.videona", PackageManager.GET_ACTIVITIES);
    public static String getAppInfo(PackageInfo info){

        StringBuilder appInfo = new StringBuilder();
        appInfo.append(" ---------------------------------------------" );
        appInfo.append(" Version name: " + info.versionName);
        appInfo.append(" Version code: " + String.valueOf(info.versionCode));
        appInfo.append(" Install location: " + String.valueOf(info.installLocation));
        appInfo.append(" PackageName: " + info.packageName);


        return appInfo.toString();
    }

    // Utils to setTheme to app, always call before every Activity setContentView.
    // Be carefull with delay
    /*
    // MUST BE SET BEFORE setContentView
		Utils.onActivityCreateSetTheme(this);
     */

    //sTheme enum from SharedPreference with value saved
    private static int sTheme;

    public final static int THEME_VIDEONA = 0;
    public final static int THEME_VIDEONA_2 = 1;



    public static void changeToTheme(Activity activity, int theme) {

        sTheme = theme;
        // Save theme in Preferences, data persistent

        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
            default:
            case THEME_VIDEONA:
                // Note, if theme == theme default do nothing, es quicker.
                //activity.setTheme(R.style.VideonaTheme);
                break;

            case THEME_VIDEONA_2:
                // If theme different from defatul, setTheme
                //activity.setTheme(R.style.VideonaTheme);
                break;
        }
    }

    public static double getFileDuration(String filePath) throws IOException {
        IsoFile isoFile = new IsoFile(filePath);
        double lengthInMSeconds = (double)
                (isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                        isoFile.getMovieBox().getMovieHeaderBox().getTimescale()) * 1000;
        return lengthInMSeconds;
    }

    public static void addFileToVideoGallery(String file) {
        MediaScannerConnection.scanFile(VimojoApplication.getAppContext(),
                new String[] { file }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
               // Log.i("ExternalStorage", "Scanned " + path + ":");
            }
        });
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
