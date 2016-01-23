/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.cb;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public abstract class UserVSM<U, F> {

    protected final FastPreferenceData<U, ?> recommenderData;
    protected final FastFeatureData<?, F, Double> featureData;

    public UserVSM(FastPreferenceData<U, ?> recommenderData, FastFeatureData<?, F, Double> featureData) {
        this.recommenderData = recommenderData;
        this.featureData = featureData;
    }

    public Stream<IdDouble<F>> getUserFeatureModel(final U u) {
        return getUidxFeatureModel(recommenderData.user2uidx(u))
                .map(fv -> new IdDouble<F>(featureData.fidx2feature(fv.idx), fv.v));
    }

    public abstract Stream<IdxDouble> getUidxFeatureModel(int uidx);
}
