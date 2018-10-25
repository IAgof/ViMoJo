package com.videonasocialmedia.vimojo.asset.domain.helper;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

/**
 * Created by jliarte on 23/07/18.
 */

/**
 * Generate a MD5 checksum of a given file
 */
public class HashCountGenerator {
  @Inject
  public HashCountGenerator() {
  }

  public String getHash(String mediaPath) {
    File file = new File(mediaPath);
    if (!file.exists()) {
      return "";
    }
    return calculateMD5(file);
  }

  private static String calculateMD5(File updateFile) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      Log.e("calculateMD5", "Exception while getting Digest", e);
      return "";
    }

    InputStream is;
    try {
      is = new FileInputStream(updateFile);
    } catch (FileNotFoundException e) {
      Log.e("calculateMD5", "Exception while getting FileInputStream", e);
      return "";
    }

    byte[] buffer = new byte[8192];
    int read;
    try {
      while ((read = is.read(buffer)) > 0) {
        digest.update(buffer, 0, read);
      }
      byte[] md5sum = digest.digest();
      BigInteger bigInt = new BigInteger(1, md5sum);
      String output = bigInt.toString(16);
      // Fill to 32 chars
      output = String.format("%32s", output).replace(' ', '0');
      return output;
    } catch (IOException e) {
      throw new RuntimeException("Unable to process file for MD5", e);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        Log.e("calculateMD5", "Exception on closing MD5 input stream", e);
      }
    }
  }
}
