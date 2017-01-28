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
package com.gigaspaces.start.manager;

import com.gigaspaces.start.manager.XapManagerConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by niv on 1/10/2017.
 */
public class XapManagerConfigTests {

    @Test
    public void hostOnly() {
        XapManagerConfig config = XapManagerConfig.parse("host");
        Assert.assertEquals("host", config.getHost());
        Assert.assertEquals(0, config.getProperties().size());
    }

    @Test
    public void hostAndProperty() {
        XapManagerConfig config = XapManagerConfig.parse("host;foo=bar");
        Assert.assertEquals("host", config.getHost());
        Assert.assertEquals(1, config.getProperties().size());
        Assert.assertEquals("bar", config.getProperties().getProperty("foo"));
    }

    @Test
    public void hostAndMultipleProperties() {
        XapManagerConfig config = XapManagerConfig.parse("host;rest=8080;zookeeper=2888:3888;lus=1234");
        Assert.assertEquals("host", config.getHost());
        Assert.assertEquals(3, config.getProperties().size());
        Assert.assertEquals("8080", config.getProperties().getProperty("rest"));
        Assert.assertEquals("2888:3888", config.getProperties().getProperty("zookeeper"));
        Assert.assertEquals("1234", config.getProperties().getProperty("lus"));
    }

}