/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.cb.vsm;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import java.util.stream.Stream;

/**
 * User vector space model.
 *
 * @author Saúl Vargas (saul.vargas@mendeley.com)
 * @param <U> user type
 * @param <F> feature type
 */
public abstract class UserVSM<U, F> {

    /**
     * User-item preference data.
     */
    protected final FastPreferenceData<U, ?> preferences;

    /**
     * Item-feature data.
     */
    protected final FastFeatureData<?, F, Double> features;

    /**
     * Constructor.
     *
     * @param preferences user-item preference data
     * @param features item-feature data
     */
    public UserVSM(FastPreferenceData<U, ?> preferences, FastFeatureData<?, F, Double> features) {
        this.preferences = preferences;
        this.features = features;
    }

    /**
     * Get the vector representing the user in the feature space.
     *
     * @param u user
     * @return stream of feature-value pairs
     */
    public Stream<IdDouble<F>> getUserFeatureModel(final U u) {
        return getUidxFeatureModel(preferences.user2uidx(u))
                .map(fv -> new IdDouble<F>(features.fidx2feature(fv.idx), fv.v));
    }

    /**
     * Get the vector representing the user in the feature space - fast version.
     *
     * @param uidx user
     * @return stream of feature-value pairs
     */
    public abstract Stream<IdxDouble> getUidxFeatureModel(int uidx);
}
