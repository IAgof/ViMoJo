package com.videonasocialmedia.vimojo.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 */
public class FtpClient implements FtpService {

    public static final int FTP_CONNECT_TIMEOUT = 10000; // connection timeout in ms

    public class FTPClientException extends Exception {
        public static final int FTP_ERROR_HOST_UNREACHABLE = 1;
        public static final int FTP_ERROR_UNAUTHORIZED = 2;
        public static final int FTP_ERROR_FILE_NOT_FOUND = 3;
        public static final int FTP_ERROR_IO = 4;

        public int getCode() {
            return code;
        }

        private final int code;

        public FTPClientException(String message, int code) {
            super(message);
            this.code = code;
        }
    }

    @Override
    public void upload(String host, String user, String pass, String srcFile, String dstFile,
                       CopyStreamListener listener)
            throws FTPClientException, IOException {
        FTPClient client = new FTPClient();
        FileInputStream fis = null;

        try {
            int reply;
            client.setConnectTimeout(FTP_CONNECT_TIMEOUT);
            client.connect(host);
            reply = client.getReplyCode();
            if (reply!=220) {
                throw new FTPClientException("Error connecting FTP server", FTPClientException.FTP_ERROR_HOST_UNREACHABLE);
            }
            client.setCopyStreamListener(listener);
            client.login(user, pass);
            reply = client.getReplyCode();
            if(reply==530) {
                throw new FTPClientException("Invalid credentials for FTP server", FTPClientException.FTP_ERROR_UNAUTHORIZED);
            }

            client.setFileType(FTP.BINARY_FILE_TYPE);
            fis = new FileInputStream(srcFile);
            client.storeFile(dstFile, fis);
            reply = client.getReplyCode();
            client.logout();
        } catch (FileNotFoundException e) {
            throw new FTPClientException("Error sending file to FTP server. File not Found", FTPClientException.FTP_ERROR_FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new FTPClientException("Error connecting FTP server", FTPClientException.FTP_ERROR_HOST_UNREACHABLE);
        } finally {
            if (fis != null) {
                fis.close();
            }
            client.disconnect();
        }
    }
}
