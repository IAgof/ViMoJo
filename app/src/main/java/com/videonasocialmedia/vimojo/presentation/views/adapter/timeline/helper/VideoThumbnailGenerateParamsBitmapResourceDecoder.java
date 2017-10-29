package com.videonasocialmedia.vimojo.presentation.views.adapter.timeline.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.io.IOException;

/** Handles pooling to reduce/prevent GC lagging from too many {@link Bitmap#createBitmap}s */
public class VideoThumbnailGenerateParamsBitmapResourceDecoder
        implements ResourceDecoder<VideoThumbnailGenerateParams, Bitmap> {
  private final Context context;

  public VideoThumbnailGenerateParamsBitmapResourceDecoder(Context context) {
    this.context = context;
  }

  @Override
  public Resource<Bitmap> decode(VideoThumbnailGenerateParams source, int width, int height)
          throws IOException {
    BitmapPool pool = Glide.get(context).getBitmapPool();
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    retriever.setDataSource(source.video.getMediaPath());
    long microSecond = source.video.getStartTime() * 1000;
    Bitmap frameAtTime = retriever.getFrameAtTime(microSecond,
            MediaMetadataRetriever.OPTION_CLOSEST);
    retriever.release();
    Bitmap bitmap = frameAtTime;
    return BitmapResource.obtain(bitmap, pool);
  }

  @Override
  public String getId() {
    // be careful if you change the Generator implementation you have to change this
    // otherwise the users may see a cached image; or clear cache on app update
    return VideoThumbnailGenerateParamsBitmapResourceDecoder.class.getCanonicalName();
  }
}
