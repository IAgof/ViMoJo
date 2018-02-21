package com.videonasocialmedia.vimojo.share.domain;

import com.videonasocialmedia.vimojo.share.model.entities.FtpNetwork;
import com.videonasocialmedia.vimojo.sources.FtpNetworkSource;

import java.util.List;

/**
 * Created by ruth on 18/10/16.
 */

public class GetFtpListUseCase {
    public List<FtpNetwork> getFtpList() {
        return new FtpNetworkSource().retrieveFtpList();
    }
}
