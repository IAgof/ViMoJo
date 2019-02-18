package com.videonasocialmedia.vimojo.featuresToggles.domain.usecase;

import com.videonasocialmedia.vimojo.featuresToggles.domain.model.FeatureToggle;
import com.videonasocialmedia.vimojo.featuresToggles.repository.FeatureRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 14/02/19.
 */


/**
 * Use case for calling update feature on {@link FeatureRepository}
 */

public class UpdateUserFeatures {
    private FeatureRepository featureRepository;

    @Inject
    public UpdateUserFeatures(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public void update(List<FeatureToggle> features) {
        featureRepository.updateUserFeatures(features);
    }

}
