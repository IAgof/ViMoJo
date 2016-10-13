package com.videonasocialmedia.vimojo.utils;

import java.io.File;

/**
 * Created by jliarte on 11/10/16.
 */

public class AndroidUtils {
    public static void deleteFileIfExists(String filePath) {
        File fileToDelete = new File(filePath);
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }
    }

}
