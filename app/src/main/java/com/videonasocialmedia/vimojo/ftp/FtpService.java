package com.videonasocialmedia.vimojo.ftp;

import org.apache.commons.net.io.CopyStreamListener;

import java.io.IOException;

/**
 *
 */
public interface FtpService {

    void upload(String host, String user, String pass, String srcFile, String dstFile, CopyStreamListener listener) throws IOException;
}
