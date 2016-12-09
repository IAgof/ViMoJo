package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.vimojo.model.entities.social.SocialNetwork;

import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public interface ShareVideoView {

    void playPreview();

    void pausePreview();

    void showError(String message);

    void showOptionsShareList(List<OptionsToShareList> optionsShareList);

    void hideShareNetworks();

    void showMoreNetworks(List<SocialNetwork> networks);

    void hideExtraNetworks();

    void setVideo(String videoOver);
}
