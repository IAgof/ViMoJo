package com.videonasocialmedia.vimojo.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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
              f.delete();
            }
          } else {
            f.delete();
          }
        }
      }
    }
  }

  // TODO(jliarte): 29/11/16 if this method uses some SDK constants, maybe it should belong to SDK?
  public static void cleanOldVideoIntermediates(File directory) {
    if (directory.exists()) {
      File[] files = directory.listFiles();
      if (files != null) { //some JVMs return null for empty dirs
        for (File f : files) {
          if (!f.isDirectory() && f.getName().startsWith(com.videonasocialmedia.videonamediaframework.model.Constants.INTERMEDIATE_FILE_PREFIX)) {
            f.delete();
          }
        }
      }
    }
  }

  public static void createFolder(String projectPath) {
    File f = new File(projectPath);
    if(!f.exists())
      f.mkdirs();
  }

  public static void deleteDirectory(File directory){
    cleanDirectory(directory);
    if(directory.exists())
      directory.delete();
  }

  public static void copyDirectory(File sourceDir, File destDir)
      throws IOException {
    File[] files = sourceDir.listFiles();
    if (files != null && files.length > 0) {
      for (File f : files) {
        if (f.isDirectory()) {
          // create the directory in the destination
          File newDir = new File(destDir, f.getName());
          newDir.mkdir();
          copyDirectory(f, newDir);
        } else {
          File destFile = new File(destDir, f.getName());
          copySingleFile(f, destFile);
        }
      }
    }
  }

  private static void copySingleFile(File sourceFile, File destFile)
      throws IOException {
    if (!destFile.exists()) {
      destFile.createNewFile();
    }
    FileChannel sourceChannel = null;
    FileChannel destChannel = null;
    try {
      sourceChannel = new FileInputStream(sourceFile).getChannel();
      destChannel = new FileOutputStream(destFile).getChannel();
      sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
    } finally {
      if (sourceChannel != null) {
        sourceChannel.close();
      }
      if (destChannel != null) {
        destChannel.close();
      }
    }
  }

  public static int getDuration(String path) {
      MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    int fileDuration;
    try {
      retriever.setDataSource(path);

      fileDuration = Integer.parseInt(retriever.extractMetadata(
          MediaMetadataRetriever.METADATA_KEY_DURATION));
    } catch (Exception e) {
      fileDuration = 0;
    }
      return fileDuration;
    }
}
