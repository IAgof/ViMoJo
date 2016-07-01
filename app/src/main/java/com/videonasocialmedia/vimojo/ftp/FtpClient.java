package com.videonasocialmedia.vimojo.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 */
public class FtpClient implements FtpService {
    @Override
    public void upload(String host, String user, String pass, String srcFile, String dstFile,
                       CopyStreamListener listener)
            throws IOException {
        FTPClient client = new FTPClient();
        FileInputStream fis = null;

        try {
            client.connect(host);
            client.setCopyStreamListener(listener);
            client.login(user, pass);
            client.setFileType(FTP.BINARY_FILE_TYPE);
            fis = new FileInputStream(srcFile);
            client.storeFile(dstFile, fis);
            client.logout();
        } finally {
            if (fis != null) {
                fis.close();
            }
            client.disconnect();
        }
    }
}
