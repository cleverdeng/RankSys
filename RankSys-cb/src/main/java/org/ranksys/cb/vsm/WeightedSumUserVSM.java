/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.cb.vsm;

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.stream.Stream;

/**
 * User vector space model in which the representation of each user in based
 * on the items in her profile weighted by the value of the preferences.
 *
 * @author Saúl Vargas (saul.vargas@mendeley.com)
 * @param <U> user type
 * @param <F> item type
 */
public class WeightedSumUserVSM<U, F> extends UserVSM<U, F> {

    /**
     * Constructor.
     *
     * @param preferences user-item preferences data
     * @param features item-feature data
     */
    public WeightedSumUserVSM(FastPreferenceData<U, ?> preferences, FastFeatureData<?, F, Double> features) {
        super(preferences, features);
    }

    /**
     * Get the vector representing the user in the feature space - fast version.
     *
     * @param uidx user
     * @return stream of feature-value pairs
     */
    @Override
    public Stream<IdxDouble> getUidxFeatureModel(int uidx) {
        Int2DoubleOpenHashMap map = new Int2DoubleOpenHashMap();
        map.defaultReturnValue(0.0);

        preferences.getUidxPreferences(uidx).forEach(pref -> {
            features.getIidxFeatures(pref.idx).forEach(fv -> {
                map.addTo(fv.idx, pref.v * fv.v);
            });
        });

        return map.int2DoubleEntrySet().stream()
                .map(e -> new IdxDouble(e.getIntKey(), e.getDoubleValue()));
    }

}
