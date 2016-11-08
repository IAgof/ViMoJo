package com.videonasocialmedia.vimojo.presentation.views.listener;

import com.videonasocialmedia.vimojo.model.entities.social.FtpNetwork;
import com.videonasocialmedia.vimojo.model.entities.social.SocialNetwork;

/**
 * Created by ruth on 18/10/16.
 */
public interface OnOptionsToShareListClickListener {

    void onFtpClicked(FtpNetwork ftp);

    void onSocialNetworkClicked (SocialNetwork socialNetwork);
}
