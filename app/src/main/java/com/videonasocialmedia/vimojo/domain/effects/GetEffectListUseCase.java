package com.videonasocialmedia.vimojo.domain.effects;

import com.videonasocialmedia.videonamediaframework.model.media.effects.Effect;
import com.videonasocialmedia.vimojo.model.sources.EffectProvider;

import java.util.List;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class GetEffectListUseCase {

    public static List<Effect> getColorEffectList() {
        return EffectProvider.getColorEffectList();
    }

    public static List<Effect> getDistortionEffectList() {
        return EffectProvider.getDistortionEffectList();
    }

    public static List<Effect> getShaderEffectsList() {
        return EffectProvider.getShaderEffectList();
    }

    public static List<Effect> getOverlayEffectsList() {
        return EffectProvider.getOverlayFilterList();
    }

  /*  public static Effect getOverlayEffectGift() {
        return EffectProvider.getOverlayEffectGift();
    }*/
}
