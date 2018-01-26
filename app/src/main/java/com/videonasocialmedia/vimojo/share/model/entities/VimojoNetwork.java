package com.videonasocialmedia.vimojo.share.model.entities;

import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;

/**
 *
 */
public class VimojoNetwork implements OptionsToShareList {
    private final String idVimojo;
    private final String name;
    private final int icon;

    public VimojoNetwork(String idVimojo, String name, int icon) {
        this.idVimojo = idVimojo;
        this.name = name;
        this.icon=icon;
    }

    public String getIdVimojo() {
        return idVimojo;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    @Override
    public int getListShareType() {
        return OptionsToShareList.typeVimojoNetwork;
    }
}
