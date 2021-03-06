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

/*
 * @(#)LRMIMethod.java 1.0  28/04/2005 02:19:41
 */

package com.gigaspaces.lrmi;

import com.gigaspaces.internal.reflection.IMethod;

/**
 * This class provides LRMI method info constructed by {@link RemoteMethodCache}.
 *
 * @author Igor Goldenberg
 * @see com.gigaspaces.lrmi.RemoteMethodCache
 * @since 4.0
 */
@com.gigaspaces.api.InternalApi
public class LRMIMethod {
    final public boolean isOneWay;
    final public int orderId;
    final public IMethod realMethod;
    final public boolean isCallBack;
    final public boolean isAsync;
    final public boolean useStubCache;
    final public boolean supported;
    final public boolean isLivenessPriority;
    final public boolean isMonitoringPriority;
    final public boolean isCustomTracking;
    final public Class<?>[] methodTypes;
    final public String realMethodString;

    public LRMIMethod(IMethod realMethod, boolean isOneWay, boolean isCallBack, boolean isAsync, boolean useStubCache, boolean livenessPriority, boolean monitoringPriority, boolean isCustomTracking, int orderId) {
        this(realMethod, isOneWay, isCallBack, isAsync, useStubCache, livenessPriority, monitoringPriority, isCustomTracking, orderId, true);
    }

    /**
     * Private lrmi method that constructs an unsupported method representation
     */
    private LRMIMethod(IMethod realMethod, boolean isOneWay, boolean isCallBack, boolean isAsync, boolean useStubCache, boolean livenessPriority, boolean monitoringPriority, boolean isCustomTracking, int orderId, boolean supported) {
        this.realMethod = realMethod;
        this.isOneWay = isOneWay;
        this.isCallBack = isCallBack;
        this.useStubCache = useStubCache;
        this.isCustomTracking = isCustomTracking;
        this.orderId = orderId;
        this.isAsync = isAsync;
        this.supported = supported;
        this.isLivenessPriority = livenessPriority;
        this.isMonitoringPriority = monitoringPriority;
        this.methodTypes = this.realMethod == null ? null : this.realMethod.getParameterTypes();
        this.realMethodString = LRMIUtilities.getMethodDisplayString(this.realMethod);
    }

    public static LRMIMethod wrapAsUnsupported(IMethod<?> realMethod) {
        return new LRMIMethod(realMethod, false, false, false, false, false, false, false, -1, false);
    }

    @Override
    public String toString() {
        return "Method = " + realMethod + ", IsOneWay = "
                + isOneWay + ", IsAsync = " + isAsync + ", IsCallBack = "
                + isCallBack + ", UseStubCache = " + useStubCache + ", IsLivenessPriority=" + isLivenessPriority
                + ", IsMonitoringPriority=" + isMonitoringPriority + ", IsCustomTracking=" + isCustomTracking;
    }

    /**
     * @return true if the service supports this method
     */
    public boolean isSupported() {
        return supported;
    }
}
