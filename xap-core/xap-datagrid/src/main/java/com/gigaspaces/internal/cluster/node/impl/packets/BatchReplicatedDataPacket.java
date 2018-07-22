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

package com.gigaspaces.internal.cluster.node.impl.packets;

import com.gigaspaces.internal.cluster.node.impl.IIncomingReplicationFacade;
import com.gigaspaces.internal.cluster.node.impl.backlog.globalorder.GlobalOrderDiscardedReplicationPacket;
import com.gigaspaces.internal.cluster.node.impl.backlog.multibucketsinglefile.DeletedMultiBucketOrderedPacket;
import com.gigaspaces.internal.cluster.node.impl.backlog.multibucketsinglefile.DiscardedMultiBucketOrderedPacket;
import com.gigaspaces.internal.cluster.node.impl.backlog.multibucketsinglefile.DummyDiscardedOrderedPacket;
import com.gigaspaces.internal.cluster.node.impl.groups.IReplicationTargetGroup;
import com.gigaspaces.internal.cluster.node.impl.router.AbstractGroupNameReplicationPacket;
import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.internal.utils.Textualizer;
import com.gigaspaces.internal.version.PlatformLogicalVersion;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@com.gigaspaces.api.InternalApi
public class BatchReplicatedDataPacket
        extends AbstractGroupNameReplicationPacket<Object> {
    private static final long serialVersionUID = 1L;
    private List<IReplicationOrderedPacket> _batch;

    private long _startKey = 0;

    private int _totalBatchKeySize = 0;

    private int _totalDiscardedPacketsKeys = 0;

    private boolean _compressed = false;

    private transient boolean _compressable = false;

    private transient boolean _clean = true;

    public BatchReplicatedDataPacket() {
    }

    public BatchReplicatedDataPacket(String groupName) {
        super(groupName);
    }

    public boolean isCompressed(){
        return _compressed;
    }

    public boolean isCompressable() {
        return _compressable;
    }

    @Override
    public Object accept(IIncomingReplicationFacade replicationFacade) {
        IReplicationTargetGroup targetGroup = replicationFacade.getReplicationTargetGroup(getGroupName());
        if(_compressed) return targetGroup.processCompressedBatch(getSourceLookupName(), getSourceUniqueId(), this);
        return targetGroup.processBatch(getSourceLookupName(), getSourceUniqueId(), _batch);
    }

    public void readExternalImpl(ObjectInput in, PlatformLogicalVersion endpointLogicalVersion) throws IOException,
            ClassNotFoundException {
        _batch = IOUtils.readObject(in);
        _compressed = in.readBoolean();
        if(_compressed) {
            _startKey = in.readLong();
            _totalBatchKeySize = in.readInt();
            _totalDiscardedPacketsKeys = in.readInt();
        }
    }

    public void writeExternalImpl(ObjectOutput out, PlatformLogicalVersion endpointLogicalVersion) throws IOException {
        IOUtils.writeObject(out, _batch);
        out.writeBoolean(_compressed);
        if(_compressed) {
            out.writeLong(_startKey);
            out.writeInt(_totalBatchKeySize);
            out.writeInt(_totalDiscardedPacketsKeys);
        }
    }

    public void setBatch(List<IReplicationOrderedPacket> batch) {
        if (!_clean || batch == null)
            throw new IllegalStateException("Attempt to override packet batch when it was not released");
        _batch = batch;
        _clean = false;
        _startKey = _batch.size() > 0 ? _batch.get(0).getKey() : 0;
        _totalBatchKeySize = 0;
        _totalDiscardedPacketsKeys = 0;
        _compressable = containsDiscarded();
    }

    private boolean containsDiscarded() {

        for(IReplicationOrderedPacket packet: _batch){
            if(packet.isDiscardedPacket()) return true;
        }

        return false;
    }

    public List<IReplicationOrderedPacket> getBatch() {
        return _batch;
    }

    public void clean() {
        _clean = true;
        _batch = null;
        _compressed = false;
        _startKey = 0;
        _totalBatchKeySize = 0;
        _totalDiscardedPacketsKeys = 0;
        _compressable = false;
    }

    @Override
    public void toText(Textualizer textualizer) {
        super.toText(textualizer);
        textualizer.append("batch", _batch);
    }

    public void compressBatch(){

//        System.out.println("~~~~~~~~START COMPRESSION~~~~~~~~~~~~~~~~" + Thread.currentThread().getName());

//        System.out.println(toString());

        if(_compressed) return;

        Iterator<IReplicationOrderedPacket> it = _batch.listIterator();

        while(it.hasNext()){
            IReplicationOrderedPacket packet = it.next();

            int packetKeyRange = (int) (packet.getEndKey() - packet.getKey() + 1);

            if(packet.isDiscardedPacket()){
                it.remove();
                _totalDiscardedPacketsKeys += packetKeyRange;
            }

            _totalBatchKeySize += packetKeyRange;
        }

        if(_batch.size() == 0) {
            _batch.add(createDiscardedPacket(_startKey, _startKey + _totalBatchKeySize - 1));
        }

        _compressed = true;


//        System.out.println("~~~~~~~~END COMPRESSION~~~~~~~~~~~~~~~~" + Thread.currentThread().getName());
//        System.out.println(toString());
    }

    public List<IReplicationOrderedPacket> decompressBatch() {
//        System.out.println("~~~~~~~~START DECOMPRESSION~~~~~~~~~~~~~~~~" + Thread.currentThread().getName());
//        System.out.println(toString());

        if(_compressed){

            List<IReplicationOrderedPacket> result = new LinkedList<IReplicationOrderedPacket>();

            long currentStartKey = _startKey;
            long endKey = _startKey + _totalBatchKeySize - 1;

            for(IReplicationOrderedPacket packet : _batch){
//                if(!packet.getData().isSingleEntryData()) System.out.println("~~~~~~~~MultipleOperationType~~~~~~~~~~~~~~~~" + packet.getData().getMultipleOperationType());
                if(currentStartKey < packet.getKey()){
                    result.add(createDiscardedPacket(currentStartKey,packet.getKey() - 1));
                }
                result.add(packet);
                currentStartKey = packet.getEndKey() + 1 ;
            }

            if(currentStartKey < endKey){
                result.add(createDiscardedPacket(currentStartKey, endKey));
            }

//            System.out.println("~~~~~~~~END DECOMPRESSION~~~~~~~~~~~~~~~~" + Thread.currentThread().getName());
//
//
//            System.out.println("-----------result: " + result);

            return result;
        }

        return _batch;
    }

    private GlobalOrderDiscardedReplicationPacket createDiscardedPacket(long startKey, long endKey){
        GlobalOrderDiscardedReplicationPacket p = new GlobalOrderDiscardedReplicationPacket(startKey);
        p.setEndKey(endKey);
        return p;
    }

    @Override
    public String toString() {
        return "BatchReplicatedDataPacket{" +
                "_batch=" + _batch +
                ", _startKey=" + _startKey +
                ", _totalBatchKeySize=" + _totalBatchKeySize +
                ", _totalDiscardedPacketsKeys=" + _totalDiscardedPacketsKeys +
                ", _compressed=" + _compressed +
                ", _clean=" + _clean +
                '}';
    }
}
