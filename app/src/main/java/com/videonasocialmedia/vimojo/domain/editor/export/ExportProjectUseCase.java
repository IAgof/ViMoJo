/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.domain.editor.export;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;

import java.util.LinkedList;


public class ExportProjectUseCase implements OnExportEndedListener {

    private OnExportFinishedListener onExportFinishedListener;
    private Exporter exporter;
    private Project project;

    public ExportProjectUseCase(OnExportFinishedListener onExportFinishedListener) {
        this.onExportFinishedListener = onExportFinishedListener;
        project = Project.getInstance(null, null, null);
        exporter = new ExporterImpl(project, this);
    }

    public void export() {
        if(isTempFilesFinished()) {
           exporter.export();
        } else {
            export();
        }
    }

    @Override
    public void onExportError(String error) {
        onExportFinishedListener.onExportError(error);
    }

    @Override
    public void onExportSuccess(Video video) {
        onExportFinishedListener.onExportSuccess(video);

    }

    public boolean isTempFilesFinished() {
        LinkedList<Media> medias = getMediasFromProject();
        for (Media media:medias) {
            Video video= (Video) media;
            if (video.isTrimmed()){
                if(!video.isTempPathFinished()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private LinkedList<Media> getMediasFromProject() {
        LinkedList<Media> medias = project.getMediaTrack().getItems();
        return medias;
    }
}
