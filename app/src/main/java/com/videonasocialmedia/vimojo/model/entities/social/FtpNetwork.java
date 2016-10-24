package com.videonasocialmedia.vimojo.model.entities.social;

import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;

/**
 *
 */
public class FtpNetwork implements OptionsToShareList {
    private final String idFTP;
    private final String name;
    private final int icon;

    public FtpNetwork(String idFTP, String name, int icon) {
        this.idFTP = idFTP;
        this.name = name;
        this.icon=icon;
    }

    public String getIdFTP() {
        return idFTP;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    @Override
    public int getListShareType() {
        return OptionsToShareList.typeFtp;
    }
}
