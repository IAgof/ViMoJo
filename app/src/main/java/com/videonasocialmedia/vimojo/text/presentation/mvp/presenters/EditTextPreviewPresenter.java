package com.videonasocialmedia.vimojo.text.presentation.mvp.presenters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruth on 1/09/16.
 */
public class EditTextPreviewPresenter implements OnVideosRetrieved {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private Video videoToEdit;
    private String position;
    private final int WIDTH_CANVAS= 1280;
    private final int HEIGTH_CANVAS = 720;
    private final float SIZE_FONT= 70f;
    private final String FILE_IMAGE ="imagenOfText.png";

    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;


    private EditTextView editTextView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;

    public EditTextPreviewPresenter(EditTextView editTextView, UserEventTracker userEventTracker) {
        this.editTextView = editTextView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void init(int videoToEditTextIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoToEditTextIndex);
            v.add(videoToEdit);
            onVideosRetrieved(v);
        }
//
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        editTextView.showPreview(videoList);
        Video video = videoList.get(0);
        //editTextView.showTrimBar(video.getStartTime(), video.getStopTime(), video.getFileDuration());

    }

    @Override
    public void onNoVideosRetrieved() {
        editTextView.showError("No videos");

    }

    public void setText(String path) {
    }

    public boolean isTextValidAndNotEmply(String text1) {
        if (isEmptyField(text1)) {
            editTextView.showError("Introduce un texto");
            return false;
        }
        return true;
    }

    private boolean isEmptyField(String text) {
        return text == null || text.length() == 0;
    }


    public void createDrawableWithText(String text, Paint.Align align) {
        TextPaint textPaint = null;

        switch (align){
            case LEFT:
                textPaint= createPaint(Paint.Align.LEFT);
                break;
            case CENTER:
                textPaint =createPaint(Paint.Align.CENTER);
                break;
        }

        Bitmap bmp = createCanvas(text, WIDTH_CANVAS,textPaint, align);

        Context appContext = VimojoApplication.getAppContext();
        Drawable drawable = new BitmapDrawable(appContext.getResources(),bmp);
        editTextView.showText(drawable);

       /*try {
            createPathTemp(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    private Bitmap createCanvas(String text, int width, TextPaint textPaint, Paint.Align align) {
        final Rect bounds= new Rect();
        textPaint.getTextBounds(text,0,text.length(), bounds);

        int height= (int)((SIZE_FONT*2)+30);

        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.TRANSPARENT);

        final Canvas canvas = new Canvas(bmp);
        int xPos=0;
        int yPos=(int) SIZE_FONT;

        switch (align){
            case LEFT:
                xPos=10;
                break;
            case CENTER:
                xPos = (canvas.getWidth() / 2);
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
                setTextSize(SIZE_FONT);
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
        Context appContext = VimojoApplication.getAppContext();
        File sd = appContext.getExternalFilesDir(null);
        File wFile = new File(sd.getAbsolutePath(), FILE_IMAGE);
        FileOutputStream stream = new FileOutputStream(wFile);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bmp.recycle();
        stream.close();
    }

    /*public String getPathImagenOfText(){

        Context appContext = VimojoApplication.getAppContext();
        File sd = appContext.getExternalFilesDir(null);
        String pathImagenOfText= sd.getAbsolutePath()+FILE_IMAGE;
        return  pathImagenOfText;
    }*/


}

