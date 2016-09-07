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
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruth on 1/09/16.
 */
public class EditTextPreviewPresenter implements OnVideosRetrieved {

    private final String LOG_TAG = getClass().getSimpleName();

    private Video videoToEdit;
    private final int WIDTH_CANVAS= 1280;
    private final int HEIGTH_CANVAS = 720;
    private final float SIZE_FONT= 70f;
    private final int NUM_MAX_LINES_TO_DRAW =2;

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
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        editTextView.showPreview(videoList);
        Video video = videoList.get(0);
    }

    @Override
    public void onNoVideosRetrieved() {
        editTextView.showError("No videos");
    }

    public void createDrawableWithText(String text, VideoEditTextActivity.TextPosition position) {

        TextPaint textPaint = null;
        Context appContext = VimojoApplication.getAppContext();
        Typeface typeFont;
        switch (position){
            case TOP:
                typeFont= Typeface.createFromAsset(appContext.getAssets(),"fonts/Roboto-Bold.ttf");
                textPaint= createPaint(Paint.Align.LEFT, typeFont);
                break;
            case CENTER:
                typeFont= Typeface.createFromAsset(appContext.getAssets(),"fonts/Roboto-Bold.ttf");
                textPaint =createPaint(Paint.Align.CENTER, typeFont);
                break;

            case BOTTOM:
                typeFont= Typeface.createFromAsset(appContext.getAssets(),"fonts/Roboto-Light.ttf");
                textPaint= createPaint(Paint.Align.LEFT, typeFont);
                break;
        }
        Bitmap bmp = createCanvas(text, WIDTH_CANVAS,HEIGTH_CANVAS, textPaint, position);

        Drawable drawable = new BitmapDrawable(appContext.getResources(),bmp);
        editTextView.showText(drawable);
    }

    private Bitmap createCanvas(String text, int width,int height, TextPaint textPaint, VideoEditTextActivity.TextPosition position) {

        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.TRANSPARENT);

        final Canvas canvas = new Canvas(bmp);
        int xPos=0;
        int yPos=0;

        switch (position){
            case TOP:
                xPos=10;
                yPos=(int) SIZE_FONT;
                break;
            case CENTER:
                xPos = (canvas.getWidth() / 2);
                yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
                break;
            case BOTTOM:
                xPos=10;
                yPos=height-height/12;
        }
        drawTextLines(text,textPaint, canvas, xPos, yPos, position);
        return bmp;
    }

    private TextPaint createPaint(final Paint.Align align, final Typeface typeface) {
        final TextPaint textPaint = new TextPaint() {

            {
                setColor(Color.WHITE);
                setTextAlign(align);
                setTypeface(typeface);
                setTextSize(SIZE_FONT);
                setAntiAlias(true);
            }
        };
        return textPaint;
    }

    private void drawTextLines(String text, TextPaint textPaint, Canvas canvas, int xPos, int yPos, VideoEditTextActivity.TextPosition position) {
        int numLineTotal=0;


        for (String line : text.split("\n")) {
            numLineTotal++;
        }

        switch (position){
            case TOP:
                    drawNumMaxLine(text, textPaint, canvas, xPos, yPos);
                break;

            case CENTER:

                if (numLineTotal<2){
                    drawNumMaxLine(text, textPaint, canvas, xPos, yPos);

                }else {
                    yPos= (int) (yPos-SIZE_FONT);
                    drawNumMaxLine(text, textPaint, canvas, xPos, yPos);
                }
                break;

            case BOTTOM:

                if (numLineTotal<2){
                    drawNumMaxLine(text, textPaint, canvas, xPos, yPos);

                }else {
                    yPos= (int) (yPos-SIZE_FONT);
                    drawNumMaxLine(text, textPaint, canvas, xPos, yPos);
                }
                break;

        }


    }

    private void drawNumMaxLine(String text, TextPaint textPaint, Canvas canvas, int xPos, int yPos) {
        int numLinesToDraw=0;

        for (String line : text.split("\n")) {
            if(numLinesToDraw < NUM_MAX_LINES_TO_DRAW) {
                canvas.drawText(line, xPos, yPos, textPaint);
                yPos += textPaint.descent() - textPaint.ascent();
                numLinesToDraw++;
            }
        }
    }


}

