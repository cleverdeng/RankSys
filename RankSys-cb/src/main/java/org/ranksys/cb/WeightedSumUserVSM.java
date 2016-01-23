/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.cb;

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public class WeightedSumUserVSM<U, F> extends UserVSM<U, F> {

    public WeightedSumUserVSM(FastPreferenceData<U, ?> recommenderData, FastFeatureData<?, F, Double> featureData) {
        super(recommenderData, featureData);
    }

    @Override
    public Stream<IdxDouble> getUidxFeatureModel(int uidx) {
        Int2DoubleOpenHashMap map = new Int2DoubleOpenHashMap();
        map.defaultReturnValue(0.0);

        recommenderData.getUidxPreferences(uidx).forEach(pref -> {
            featureData.getIidxFeatures(pref.idx).forEach(fv -> {
                map.addTo(fv.idx, pref.v * fv.v);
            });
        });

        return map.int2DoubleEntrySet().stream()
                .map(e -> new IdxDouble(e.getIntKey(), e.getDoubleValue()));
    }

}
