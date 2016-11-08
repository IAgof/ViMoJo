package com.videonasocialmedia.vimojo.domain.social;

import com.videonasocialmedia.vimojo.model.entities.social.FtpNetwork;
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
