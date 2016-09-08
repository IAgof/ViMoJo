package com.videonasocialmedia.vimojo.text.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.utils.Constants;

/**
 * Created by alvaro on 7/09/16.
 */
public class TextToDrawable {

    private final static float SIZE_FONT= 70f;
    private final static int NUM_MAX_LINES_TO_DRAW =2;

    public static Drawable createDrawableWithTextAndPosition(String text, String positionText) {

        Drawable drawable;
        TextPaint textPaint = null;
        Context appContext = VimojoApplication.getAppContext();
        Typeface typeFont;
        VideoEditTextActivity.TextPosition position = getTypePositionFromString(positionText);
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
        Bitmap bmp = createCanvas(text, Constants.DEFAULT_VIMOJO_WIDTH,Constants.DEFAULT_VIMOJO_HEIGHT, textPaint, position);

        drawable = new BitmapDrawable(appContext.getResources(),bmp);

        return drawable;
    }

    private static VideoEditTextActivity.TextPosition getTypePositionFromString(String position) {
        if(position.compareTo(VideoEditTextActivity.TextPosition.BOTTOM.name()) == 0){
            return VideoEditTextActivity.TextPosition.BOTTOM;
        }
        if(position.compareTo(VideoEditTextActivity.TextPosition.CENTER.name()) == 0){
            return VideoEditTextActivity.TextPosition.CENTER;
        }
        if(position.compareTo(VideoEditTextActivity.TextPosition.TOP.name()) == 0){
            return VideoEditTextActivity.TextPosition.TOP;
        }

        return VideoEditTextActivity.TextPosition.CENTER;
    }

    private static Bitmap createCanvas(String text, int width, int height, TextPaint textPaint, VideoEditTextActivity.TextPosition position) {

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

    private static TextPaint createPaint(final Paint.Align align, final Typeface typeface) {
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

    private static void drawTextLines(String text, TextPaint textPaint, Canvas canvas, int xPos, int yPos, VideoEditTextActivity.TextPosition position) {
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

    private static void drawNumMaxLine(String text, TextPaint textPaint, Canvas canvas, int xPos, int yPos) {
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
