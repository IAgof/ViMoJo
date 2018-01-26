package com.videonasocialmedia.vimojo.presentation.views.listener;

import com.videonasocialmedia.vimojo.share.model.entities.FtpNetwork;
import com.videonasocialmedia.vimojo.share.model.entities.SocialNetwork;
import com.videonasocialmedia.vimojo.share.model.entities.VimojoNetwork;

/**
 * Created by ruth on 18/10/16.
 */
public interface OnOptionsToShareListClickListener {

    void onFtpClicked(FtpNetwork ftp);

    void onSocialNetworkClicked (SocialNetwork socialNetwork);

  void onVimojoClicked(VimojoNetwork vimojoNetwork);
}
