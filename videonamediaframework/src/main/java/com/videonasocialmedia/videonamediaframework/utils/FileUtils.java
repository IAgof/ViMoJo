package com.videonasocialmedia.videonamediaframework.utils;

import java.io.File;

/**
 * Created by jliarte on 27/10/16.
 */

public class FileUtils {
  public static void cleanDirectory(File directory) {
    cleanPath(directory, true);
  }

  public static void cleanDirectoryFiles(File directory) {
    cleanPath(directory, false);
  }

  private static void cleanPath(File directory, boolean cleanRecursively) {
    if (directory.exists()) {
      File[] files = directory.listFiles();
      if (files != null) { //some JVMs return null for empty dirs
        for (File f : files) {
          if (f.isDirectory()) {
            if (cleanRecursively) {
              cleanDirectory(f);
            }
          } else {
            f.delete();
          }
        }
      }
    }
  }
}
