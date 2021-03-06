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

package com.gigaspaces.cluster.activeelection.core;

import com.gigaspaces.internal.server.space.quiesce.QuiesceHandler;

/**
 * Created by moran on 4/12/15.
 */
@com.gigaspaces.api.InternalApi
public class SplitBrainRecoveryHolder {

    private final SplitBrainRecoveryPolicy splitBrainRecoveryPolicy;
    private final QuiesceHandler quiesceHandler;

    public SplitBrainRecoveryHolder(SplitBrainRecoveryPolicy splitBrainRecoveryPolicy, QuiesceHandler quiesceHandler) {

        this.splitBrainRecoveryPolicy = splitBrainRecoveryPolicy;
        this.quiesceHandler = quiesceHandler;
    }

    public SplitBrainRecoveryPolicy getSplitBrainRecoveryPolicy() {
        return splitBrainRecoveryPolicy;
    }

    public QuiesceHandler getQuiesceHandler() {
        return quiesceHandler;
    }
}
