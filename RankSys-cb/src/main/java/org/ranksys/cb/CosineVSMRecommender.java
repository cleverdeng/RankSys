/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.cb;

import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.rec.fast.FastRankingRecommender;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import static java.lang.Math.sqrt;

/**
 *
 * @author saul
 */
public class CosineVSMRecommender<U, I, F> extends FastRankingRecommender<U, I> {

    private final FastFeatureData<I, F, Double> fd;
    private final UserVSM<U, F> ufm;
    private final Int2DoubleMap itemNormMap;

    public CosineVSMRecommender(FastUserIndex<U> users, FastItemIndex<I> items, FastFeatureData<I, F, Double> fd, UserVSM<U, F> ufm) {
        super(users, items);
        this.fd = fd;
        this.ufm = ufm;

        this.itemNormMap = new Int2DoubleOpenHashMap();
        itemNormMap.defaultReturnValue(0.0);
        fd.getIidxWithFeatures().forEach(iidx -> {
            double itemNorm = fd.getIidxFeatures(iidx)
                    .mapToDouble(fv -> fv.v * fv.v)
                    .sum();
            itemNorm = sqrt(itemNorm);
            itemNormMap.put(iidx, itemNorm);
        });

    }

    /**
     * Returns a map of item-score pairs.
     *
     * @param uidx index of the user whose scores are predicted
     * @return a map of item-score pairs
     */
    @Override
    public Int2DoubleMap getScoresMap(int uidx) {
        Int2DoubleOpenHashMap featureScores = new Int2DoubleOpenHashMap();
        featureScores.defaultReturnValue(0.0);

        double[] userNorm = {0.0};
        ufm.getUidxFeatureModel(uidx).forEach(fv -> {
            userNorm[0] += fv.v * fv.v;
            fd.getFidxItems(fv.idx).forEach(iv -> {
                featureScores.addTo(iv.idx, fv.v * iv.v);
            });
        });
        userNorm[0] = sqrt(userNorm[0]);

        featureScores.int2DoubleEntrySet().forEach(e -> {
            e.setValue(e.getDoubleValue() / (userNorm[0] * itemNormMap.get(e.getIntKey())));
        });

        return featureScores;
    }

}
