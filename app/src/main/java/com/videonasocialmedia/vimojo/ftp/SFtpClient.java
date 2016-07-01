package com.videonasocialmedia.vimojo.ftp;

import android.util.Log;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.apache.commons.net.io.CopyStreamListener;

/**
 *
 */
public class SFtpClient implements FtpService {
    Session session = null;
    ChannelSftp channelSftp = null;
    private JSch jSch;

    public SFtpClient() {
        this.jSch = new JSch();
    }

    @Override
    public void upload(String host, String user, String pass, String srcFile, String dstFile, CopyStreamListener listener) {
        try {
            session = jSch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            session.setPassword(pass);
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.put(srcFile, dstFile);
            // TODO(javi.cabanas): 23/6/16 notify finish
            Log.d("Uploading", "ok");
        } catch (JSchException | SftpException e) {
            // TODO(javi.cabanas): 23/6/16 notify error
            Log.e("Uploading", "error", e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
