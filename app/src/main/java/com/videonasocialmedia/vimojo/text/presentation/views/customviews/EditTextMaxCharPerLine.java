package com.videonasocialmedia.vimojo.text.presentation.views.customviews;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.Arrays;

/**
 * Created by ruth on 5/09/16.
 */
public class EditTextMaxCharPerLine implements InputFilter {


        private final int mLineChars;

        public EditTextMaxCharPerLine(int pLineChars) {
            mLineChars = pLineChars;
        }

        @Override
        public CharSequence filter(CharSequence src, int srcStart, int srcEnd, Spanned dest, int destStart, int destEnd) {
            CharSequence original = dest.subSequence(0,destStart);
            CharSequence replacement = src.subSequence(srcStart,srcEnd);

            if(replacement.length() < 1){
                return null;
            }

            int lastLineCharIndex = -1;

            for (int j = destStart - 1; j >= 0; j--){
                if(original.charAt(j) == '\n'){
                    lastLineCharIndex = j;
                    break;
                }
            }

            int charsAfterLine = lastLineCharIndex < 0 ? original.length() : original.length() - lastLineCharIndex;

            StringBuilder sb = new StringBuilder();

            for (int k = 0; k < replacement.length(); k++){

                if(charsAfterLine == mLineChars+1){
                    charsAfterLine = 0;
                    sb.append('\n');
                }

                sb.append(replacement.charAt(k));
                charsAfterLine++;

            }


            return sb;
        }

        public static void applyAutoWrap(EditText et, int wrapLength){
            InputFilter[] filters = et.getFilters();
            InputFilter[] newFilters = Arrays.copyOf(filters, filters.length + 1);
            newFilters[filters.length] = new EditTextMaxCharPerLine(wrapLength);
            et.setFilters(newFilters);
        }
    }


