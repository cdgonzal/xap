/*
 * Copyright (c) 2008-2016, GigaSpaces Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gigaspaces.internal.cluster.node.impl.backlog.multibucketsinglefile;

import com.gigaspaces.internal.cluster.node.impl.filters.IReplicationInFilterCallback;
import com.gigaspaces.internal.cluster.node.impl.packets.data.DiscardReplicationPacketData;
import com.gigaspaces.internal.cluster.node.impl.packets.data.IReplicationPacketData;
import com.gigaspaces.internal.cluster.node.impl.processlog.multibucketsinglefile.IMultiBucketSingleFileProcessLog;
import com.gigaspaces.internal.cluster.node.impl.processlog.multibucketsinglefile.MultiBucketSingleFileProcessResult;
import com.gigaspaces.internal.cluster.node.impl.processlog.multibucketsinglefile.ParallelBatchProcessingContext;
import com.gigaspaces.internal.collections.CollectionsFactory;
import com.gigaspaces.internal.collections.ShortLongIterator;
import com.gigaspaces.internal.collections.ShortLongMap;
import com.gigaspaces.internal.collections.ShortObjectMap;
import com.gigaspaces.internal.collections.standard.StandardShortObjectMap;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


@com.gigaspaces.api.InternalApi
public class DummyDiscardedOrderedPacket
        extends AbstractMultiBucketOrderedPacket {

    private static final long serialVersionUID = 1L;
    private long _globalEndKey;


    public DummyDiscardedOrderedPacket() {
    }

    public DummyDiscardedOrderedPacket(long startGlobalKey,
                                       long endGlobalKey) {

        super(startGlobalKey, new StandardShortObjectMap<BucketKey>());

        _globalEndKey = endGlobalKey;
    }

    public IReplicationPacketData<?> getData() {
        return DiscardReplicationPacketData.instance();
    }

    public long getEndKey() {
        return _globalEndKey;
    }

    public boolean isDataPacket() {
        return false;
    }

    @Override
    public boolean isDiscardedPacket() {
        return true;
    }

    @Override
    public MultiBucketSingleFileProcessResult process(String sourceLookupName, IMultiBucketSingleFileProcessLog processLog, IReplicationInFilterCallback inFilterCallback, ParallelBatchProcessingContext context, int segmentIndex) {
        return null;
    }

    @Override
    public PacketConsumeState getConsumeState(short bucketIndex) {
        return null;
    }

    @Override
    public boolean setConsumed() {
        return false;
    }

    @Override
    public IMultiBucketSingleFileReplicationOrderedPacket replaceWithDiscarded() {
        return null;
    }

    @Override
    public DummyDiscardedOrderedPacket clone() {
        // Immutable
        return this;
    }

    @Override
    public IMultiBucketSingleFileReplicationOrderedPacket cloneWithNewData(
            IReplicationPacketData<?> newData) {
        throw new UnsupportedOperationException();
    }




}
