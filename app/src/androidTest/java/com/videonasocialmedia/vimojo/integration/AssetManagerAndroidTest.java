package com.videonasocialmedia.vimojo.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

/**
 * Created by jliarte on 11/09/17.
 */

public class AssetManagerAndroidTest {
  protected String getAssetPath(String resName) throws IOException {
    InputStream vid = getInstrumentation().getContext().getResources().getAssets().open(resName);
    String testFileName = getInstrumentation().getTargetContext().getExternalCacheDir() + "/"
            + resName;
    createFileFromInputStream(vid, testFileName);
    return testFileName;
  }

  private File createFileFromInputStream(InputStream inputStream, String testFileName) {
    try {
      File f = new File(testFileName);
      OutputStream outputStream = new FileOutputStream(f);
      byte buffer[] = new byte[1024];
      int length = 0;

      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }

      outputStream.close();
      inputStream.close();

      return f;
    } catch (IOException e) {
      //Logging exception
    }
    return null;
  }
}
