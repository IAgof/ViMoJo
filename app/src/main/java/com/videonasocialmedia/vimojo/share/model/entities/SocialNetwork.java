package com.videonasocialmedia.vimojo.share.model.entities;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;

/**
 * Created by jca on 9/12/15.
 */
public class SocialNetwork implements OptionsToShareList {

    private final String idSocialNetwork;
    private final String name;
    private final String androidPackageName;
    private final String androidActivityName;
    private final Drawable icon;
    private final String defaultMessage;

    public SocialNetwork(String idSocialNetwork,String name, String androidPackageName, String androidActivityName,
                         Drawable icon, String defaultMessage) {
        this.idSocialNetwork = idSocialNetwork;
        this.name = name;
        this.androidPackageName = androidPackageName;
        this.androidActivityName = androidActivityName;
        this.icon=icon;
        this.defaultMessage = defaultMessage;
    }

    public String getName() {
        return name;
    }

    public String getAndroidPackageName() {
        return androidPackageName;
    }

    public String getAndroidActivityName() {
        return androidActivityName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getIdSocialNetwork() {
        return idSocialNetwork;
    }

    @Override
    public int getListShareType() {
        return OptionsToShareList.typeSocialNetwork;
    }
}
