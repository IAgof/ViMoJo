package com.videonasocialmedia.vimojo.sources;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.model.entities.social.FtpNetwork;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FtpNetworkSource {
    private Context context;

    private List<FtpNetwork> FtpList = new ArrayList();

    public List<FtpNetwork> retrieveFtpList() {
        if (FtpList.size() == 0)
            populateFtpList();
        return FtpList;
    }

    private void populateFtpList() {
        FtpList.add(new FtpNetwork(ConfigPreferences.FTP1, "FTP 1", R.drawable.ftp));
        FtpList.add(new FtpNetwork(ConfigPreferences.FTP2, "FTP 2", R.drawable.ftp));
    }

}
