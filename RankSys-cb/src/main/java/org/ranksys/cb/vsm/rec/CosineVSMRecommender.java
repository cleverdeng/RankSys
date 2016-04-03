/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.cb.vsm.rec;

import org.ranksys.cb.vsm.UserVSM;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.rec.fast.FastRankingRecommender;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import static java.lang.Math.sqrt;

/**
 * VSM-based recommender whose scoring function is the cosine between user and
 * item vectors.
 *
 * @author Saúl Vargas (saul.vargas@mendeley.com)
 * @param <U> user type
 * @param <F> feature type
 * @param <I> item type
 */
public class CosineVSMRecommender<U, I, F> extends FastRankingRecommender<U, I> {

    private final FastFeatureData<I, F, Double> features;
    private final UserVSM<U, F> uvsm;
    private final Int2DoubleMap itemNormMap;

    /**
     * Constructors
     *
     * @param users user index
     * @param items item index
     * @param features feature data
     * @param uvsm user vector space model
     */
    public CosineVSMRecommender(FastUserIndex<U> users, FastItemIndex<I> items, FastFeatureData<I, F, Double> features, UserVSM<U, F> uvsm) {
        super(users, items);
        this.features = features;
        this.uvsm = uvsm;

        this.itemNormMap = new Int2DoubleOpenHashMap();
        itemNormMap.defaultReturnValue(0.0);
        features.getIidxWithFeatures().forEach(iidx -> {
            double itemNorm = features.getIidxFeatures(iidx)
                    .mapToDouble(fv -> fv.v2 * fv.v2)
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
        uvsm.getUidxFeatureModel(uidx).forEach(fv -> {
            userNorm[0] += fv.v2 * fv.v2;
            features.getFidxItems(fv.v1).forEach(iv -> {
                featureScores.addTo(iv.v1, fv.v2 * iv.v2);
            });
        });
        userNorm[0] = sqrt(userNorm[0]);

        featureScores.int2DoubleEntrySet().forEach(e -> {
            e.setValue(e.getDoubleValue() / (userNorm[0] * itemNormMap.get(e.getIntKey())));
        });

        return featureScores;
    }

}
