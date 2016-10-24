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
        FtpList.add(new FtpNetwork(ConfigPreferences.FTP1, "FTP", R.drawable.activity_share_icon_ftp_green));
        FtpList.add(new FtpNetwork(ConfigPreferences.FTP2, "Breaking news", R.drawable.activity_share_icon_ftp_red));
    }

}
