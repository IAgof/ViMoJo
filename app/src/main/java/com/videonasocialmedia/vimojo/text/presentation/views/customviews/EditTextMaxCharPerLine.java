package com.videonasocialmedia.vimojo.text.presentation.views.customviews;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.Arrays;

/**
 * Created by ruth on 5/09/16.
 */
public class EditTextMaxCharPerLine implements InputFilter {


    private final int maxCharsPerLine;

    public EditTextMaxCharPerLine(int maxChars) {
            maxCharsPerLine = maxChars;
        }

    @Override
    public CharSequence filter(CharSequence src, int srcStart, int srcEnd, Spanned dest, int destStart, int destEnd) {
        CharSequence currentText = dest.subSequence(0,destStart);
        CharSequence newTextWritten = src.subSequence(srcStart,srcEnd);

        if(newTextWritten.length() < 1){
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        int lastLineCharIndex = findLastLineCharIndex(destStart, currentText);
        int charsAfterLine = lastLineCharIndex < 0 ? currentText.length() : currentText.length() - lastLineCharIndex;

        if (charsAfterLine + newTextWritten.length() < maxCharsPerLine) {
            return null;
        } else {
            int remainingCharSize = maxCharsPerLine - charsAfterLine;
            stringBuilder.append(newTextWritten.subSequence(0, Math.max(0, remainingCharSize -1))+"\n");
            if (newTextWritten.length() >= remainingCharSize) {
                stringBuilder.append(newTextWritten.toString().substring(remainingCharSize-1));
            }
            return stringBuilder;
        }
    }

    private int findLastLineCharIndex(int destStart, CharSequence currentText) {
        int lastLineCharIndex = -1;
        for (int j = destStart - 1; j >= 0; j--){
            if(currentText.charAt(j) == '\n'){
                lastLineCharIndex = j;
                break;
            }
        }
        return lastLineCharIndex;
    }

    public static void applyAutoWrap(EditText et, int wrapLength){
            InputFilter[] filters = et.getFilters();
            InputFilter[] newFilters = Arrays.copyOf(filters, filters.length + 1);
            newFilters[filters.length] = new EditTextMaxCharPerLine(wrapLength);
            et.setFilters(newFilters);
        }
    }


