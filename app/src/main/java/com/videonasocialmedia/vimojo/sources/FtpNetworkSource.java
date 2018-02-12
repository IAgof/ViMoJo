package com.videonasocialmedia.vimojo.sources;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.share.model.entities.FtpNetwork;
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
        // TODO:(alvaro.martinez) 12/01/18 Now we only use one FTP, not two. Implement feature, I want to add more FTPs
        //  FtpList.add(new FtpNetwork(ConfigPreferences.FTP2, "Breaking news", R.drawable.activity_share_icon_ftp_red));
    }

}
