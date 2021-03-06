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

package org.openspaces.test.common.mock;

import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.sync.IntroduceTypeData;

public class MockIntroduceTypeData implements IntroduceTypeData {
    private final SpaceTypeDescriptor typeDescriptor;

    public MockIntroduceTypeData(SpaceTypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    public SpaceTypeDescriptor getTypeDescriptor() {
        return typeDescriptor;
    }
}
