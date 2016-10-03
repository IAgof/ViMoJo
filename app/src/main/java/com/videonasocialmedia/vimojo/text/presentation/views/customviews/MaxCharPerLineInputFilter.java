package com.videonasocialmedia.vimojo.text.presentation.views.customviews;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.Arrays;

/**
 * Created by ruth on 5/09/16.
 */
public class MaxCharPerLineInputFilter implements InputFilter {

    private final int maxCharsPerLine;

    public MaxCharPerLineInputFilter(int maxChars) {
            maxCharsPerLine = maxChars;
        }

    @Override
    public CharSequence filter(CharSequence src, int srcStart, int srcEnd, Spanned dest, int destStart, int destEnd) {
        CharSequence currentText = dest.subSequence(0,destStart);
        CharSequence newTextWritten = src.toString().substring(srcStart,srcEnd);

        StringBuilder stringBuilder = new StringBuilder();

        if(newTextWritten.length() < 1) { // BACKSPACE??
            return null;
        }

        int lastLineStartIndex = findLastLineStartIndex(destStart, currentText);
        int charsWrittenInLastLine = lastLineStartIndex < 0 ? currentText.length() : currentText.length() - lastLineStartIndex;

        if (charsWrittenInLastLine + newTextWritten.length() < maxCharsPerLine) {
            return null;
        } else {
            int remainingCharSize = maxCharsPerLine - charsWrittenInLastLine;
            stringBuilder.append(newTextWritten.subSequence(0, remainingCharSize) + "\n");
            if (newTextWritten.length() > remainingCharSize) {
                stringBuilder.append(newTextWritten.subSequence(remainingCharSize + 1, newTextWritten.length()-1));
            }
            return stringBuilder;
        }
    }

    private int findLastLineStartIndex(int destStart, CharSequence currentText) {
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
            newFilters[filters.length] = new MaxCharPerLineInputFilter(wrapLength);
            et.setFilters(newFilters);
        }
    }


