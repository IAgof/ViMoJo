package com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.helper;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.TimeLineVideoViewHolder;

/** Boilerplate because of the degeneration in ModelType == DataType, but important for caching.
  *  @see TimeLineVideoViewHolder#generator */
public class VideoThumbnailGenerateParamsPassthroughModelLoader
        implements ModelLoader<VideoThumbnailGenerateParams, VideoThumbnailGenerateParams> {
  @Override
  public DataFetcher<VideoThumbnailGenerateParams> getResourceFetcher(
          final VideoThumbnailGenerateParams model, int width, int height) {
    return new DataFetcher<VideoThumbnailGenerateParams>() {
      @Override
      public VideoThumbnailGenerateParams loadData(Priority priority) throws Exception {
        return model;
      }

      @Override
      public void cleanup() {

      }

      @Override
      public String getId() {
        return model.getId();
      }

      @Override
      public void cancel() {

      }
    };
  }
}
