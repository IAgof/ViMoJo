package com.videonasocialmedia.vimojo.export;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.transcoder.overlay.Filter;
import com.videonasocialmedia.transcoder.overlay.Image;
import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatUseCase;
import com.videonasocialmedia.vimojo.export.domain.OnGetVideonaFormatListener;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.domain.AddTextToVideoUseCase;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.ExportIntentConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by alvaro on 5/09/16.
 */
public class ExportTempBackgroundService extends Service implements OnGetVideonaFormatListener {

    public static final String ACTION = "com.videonasocialmedia.vimojo.android.service.receiver";

    GetVideonaFormatUseCase getVideonaFormatUseCase;
    private VideonaFormat videoFormat;

    public ExportTempBackgroundService(){
        getVideonaFormatUseCase = new GetVideonaFormatUseCase();
        getVideonaFormatUseCase.getVideonaFormatFromProject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        final int videoId = intent.getIntExtra(ExportIntentConstants.VIDEO_ID, -51456);
        final boolean isVideoTrimmed = intent.getBooleanExtra(ExportIntentConstants.IS_VIDEO_TRIMMED, false);
        final int startTimeMs = intent.getIntExtra(ExportIntentConstants.START_TIME_MS, 0);
        final int finishTimeMs = intent.getIntExtra(ExportIntentConstants.FINISH_TIME_MS, 0);

        final boolean isAddedText = intent.getBooleanExtra(ExportIntentConstants.IS_TEXT_ADDED, false);
        final String text = intent.getStringExtra(ExportIntentConstants.TEXT_TO_ADD);
        final int sizeX = intent.getIntExtra(ExportIntentConstants.TEXT_SIZE_X, 1280);
        final int sizeY = intent.getIntExtra(ExportIntentConstants.TEXT_SIZE_Y, 720);



        new Thread(new Runnable() {
            @Override
            public void run() {
                final Video video = getVideo(videoId);
                MediaTranscoderListener useCaseListener = new MediaTranscoderListener() {
                    @Override
                    public void onTranscodeProgress(double v) {
                    }

                    @Override
                    public void onTranscodeCompleted() {
                        video.setTempPathFinished(true);
                        sendResultBroadcast(videoId, true);
                    }

                    @Override
                    public void onTranscodeCanceled() {
                        video.deleteTempVideo();
                        sendResultBroadcast(videoId, false);
                    }

                    @Override
                    public void onTranscodeFailed(Exception e) {
                        video.deleteTempVideo();
                        sendResultBroadcast(videoId, false);
                    }

                };
                if (video != null) {

                    video.setTempPath();

                    if(isAddedText){
                        addTextToVideo(video, useCaseListener, videoFormat, text, sizeX, sizeY);
                    }

                    if(isVideoTrimmed) {
                        trimVideo(video, useCaseListener, videoFormat, startTimeMs, finishTimeMs);
                    }


                } else {
                    useCaseListener.onTranscodeFailed(null);
                }
            }
        }).start();

        return START_NOT_STICKY;
    }

    private void addTextToVideo(Video video, MediaTranscoderListener useCaseListener, VideonaFormat videoFormat, String text, int sizeX, int sizeY) {
        AddTextToVideoUseCase addTextToVideoUseCase = new AddTextToVideoUseCase();

        Drawable textDrawable = createDrawableWithText(text, Paint.Align.CENTER);

        Image imageText = new Image(textDrawable,1280,720, 0, 0);

        addTextToVideoUseCase.addTextToVideo(video, videoFormat, imageText, useCaseListener);
    }

    private void trimVideo(Video video, MediaTranscoderListener useCaseListener, VideonaFormat videoFormat, int startTimeMs, int finishTimeMs) {
        ModifyVideoDurationUseCase modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
        modifyVideoDurationUseCase.trimVideo(video, videoFormat, startTimeMs, finishTimeMs, useCaseListener);
    }


    public Drawable createDrawableWithText(String text, Paint.Align align) {
        TextPaint textPaint = null;

        switch (align){
            case LEFT:
                textPaint= createPaint(Paint.Align.LEFT);
                break;
            case CENTER:
                textPaint =createPaint(Paint.Align.CENTER);
                break;
        }

        Bitmap bitmap = createCanvas(text, 1280,textPaint, align);

        try {
            createPathTemp(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Context appContext = VimojoApplication.getAppContext();
        return new BitmapDrawable(appContext.getResources(),bitmap);

    }

    private Bitmap createCanvas(String text, int width, TextPaint textPaint, Paint.Align align) {
        final Rect bounds= new Rect();
        textPaint.getTextBounds(text,0,text.length(), bounds);

        final Bitmap bmp = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.TRANSPARENT);

        final Canvas canvas = new Canvas(bmp);
        int xPos=0;
        int yPos=0;
        switch (align){
            case LEFT:
                xPos=10;
                yPos = 10;
                break;
            case CENTER:
                xPos = (canvas.getWidth() / 2);
                yPos = (canvas.getHeight() / 2);
                break;
        }
        drawTextLines(text,textPaint, canvas, xPos, yPos);
        return bmp;

    }

    private TextPaint createPaint(final Paint.Align align) {
        final TextPaint textPaint = new TextPaint() {

            {
                setColor(Color.WHITE);
                setTextAlign(align);
                Context appContext = VimojoApplication.getAppContext();
                setTypeface(Typeface.createFromAsset(appContext.getAssets(),"fonts/Roboto-Bold.ttf"));
                setTextSize(70f);
                setAntiAlias(true);
            }
        };
        return textPaint;

    }

    private void drawTextLines(String text, TextPaint textPaint, Canvas canvas, int xPos, int yPos) {
        for (String line : text.split("\n")) {
            canvas.drawText(line, xPos, yPos, textPaint);
            yPos += textPaint.descent() - textPaint.ascent();
        }
    }

    private void createPathTemp(Bitmap bmp) throws IOException {

        String tempImageText = Constants.PATH_APP_TEMP + File.separator + "tempOUT.png";
        File wFile = new File(tempImageText);
        FileOutputStream stream = new FileOutputStream(wFile);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        //bmp.recycle();
        stream.close();
    }

    private void sendResultBroadcast(int videoId, boolean success) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(ExportIntentConstants.VIDEO_EXPORTED, success);
        intent.putExtra(ExportIntentConstants.VIDEO_ID, videoId);
        sendBroadcast(intent);
    }

    private Video getVideo(int videoId) {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            for (Media media : videoList) {
                if (media.getIdentifier() == videoId) {
                    return (Video) media;
                }
            }
        }
        return null;
    }

    @Override
    public void onVideonaFormat(VideonaFormat videonaFormat) {
        this.videoFormat = videonaFormat;
    }

    @Override
    public void onVideonaErrorFormat() {
        // Error
    }
}
