package com.videonasocialmedia.vimojo.share.domain;

import com.videonasocialmedia.vimojo.share.model.entities.SocialNetwork;
import com.videonasocialmedia.vimojo.share.model.sources.SocialNetworkAppsProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jca on 11/12/15.
 */
public class ObtainNetworksToShareUseCase {

    SocialNetworkAppsProvider provider;


    public ObtainNetworksToShareUseCase() {
        this.provider = new SocialNetworkAppsProvider();
    }

    public List<SocialNetwork> ObtainAllNetworks() {
        return provider.getSocialNetworksAppsInstalled();
    }

    public List<SocialNetwork> obtainMainNetworks() {
        List<SocialNetwork> networksList = provider.getSocialNetworksAppsInstalled();
        List<SocialNetwork> mainNetworksList = new ArrayList<>();
        for (SocialNetwork app : networksList) {
            if (isMainNetwork(app)) {
                mainNetworksList.add(app);
            }
        }
        return mainNetworksList;
    }

    private boolean isMainNetwork(SocialNetwork app) {
        String appName = app.getName();
        return appName.equalsIgnoreCase("Twitter")
                || appName.equalsIgnoreCase("Facebook")
                || appName.equalsIgnoreCase("Whatsapp")
                || appName.equalsIgnoreCase("GooglePlus")
                || appName.equalsIgnoreCase("Youtube")
                || appName.equalsIgnoreCase("Instagram")
                || appName.equalsIgnoreCase("Instagram Stories");
    }

    public List<SocialNetwork> obtainVishowNetworks() {
        List<SocialNetwork> networksList = provider.getSocialNetworksAppsInstalled();
        List<SocialNetwork> mainNetworksList = new ArrayList<>();
        for (SocialNetwork app : networksList) {
            if (app.getName().equals("Instagram Stories")) {
                mainNetworksList.add(app);
            }
        }
        return mainNetworksList;
    }
}
