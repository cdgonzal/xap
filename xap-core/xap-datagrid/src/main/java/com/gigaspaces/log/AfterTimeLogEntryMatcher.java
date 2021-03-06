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


package com.gigaspaces.log;

import java.text.ParseException;

/**
 * A matcher filter that will match only on log entries that occurred after the specified time.
 *
 * @author kimchy
 */

public class AfterTimeLogEntryMatcher extends LogEntryMatcherFilter {

    private static final long serialVersionUID = 1;

    protected long timestamp;

    private boolean inclusive;

    public AfterTimeLogEntryMatcher(String time) throws ParseException {
        this(time, false);
    }

    public AfterTimeLogEntryMatcher(String time, boolean inclusive) throws ParseException {
        this(time, LogEntryMatchers.DEFAULT_TIME_FORMAT, inclusive);
    }

    public AfterTimeLogEntryMatcher(String time, String format) throws ParseException {
        this(time, format, LogEntryMatchers.INCLUSIVE);
    }

    public AfterTimeLogEntryMatcher(String time, String format, boolean inclusive) throws ParseException {
        this(time, format, inclusive, new AllLogEntryMatcher());
    }

    public AfterTimeLogEntryMatcher(String time, boolean inclusive, LogEntryMatcher matcher) throws ParseException {
        this(time, LogEntryMatchers.DEFAULT_TIME_FORMAT, inclusive, matcher);
    }

    public AfterTimeLogEntryMatcher(String time, LogEntryMatcher matcher) throws ParseException {
        this(time, LogEntryMatchers.DEFAULT_TIME_FORMAT, LogEntryMatchers.INCLUSIVE, matcher);
    }

    public AfterTimeLogEntryMatcher(String time, String format, LogEntryMatcher matcher) throws ParseException {
        this(time, format, LogEntryMatchers.INCLUSIVE, matcher);
    }

    public AfterTimeLogEntryMatcher(String time, String format, boolean inclusive, LogEntryMatcher matcher) throws ParseException {
        this(LogEntryMatchers.createDateFormat(format).parse(time).getTime(), inclusive, matcher);
    }

    public AfterTimeLogEntryMatcher(long timestamp) {
        this(timestamp, new AllLogEntryMatcher());
    }

    public AfterTimeLogEntryMatcher(long timestamp, LogEntryMatcher matcher) {
        this(timestamp, false, matcher);
    }

    public AfterTimeLogEntryMatcher(long timestamp, boolean inclusive) {
        this(timestamp, inclusive, new AllLogEntryMatcher());
    }

    public AfterTimeLogEntryMatcher(long timestamp, boolean inclusive, LogEntryMatcher matcher) {
        super(matcher);
        this.timestamp = timestamp;
        this.inclusive = inclusive;
    }

    protected Operation filter(LogEntry entry) {
        if (inclusive && entry.getTimestamp() >= timestamp) {
            return Operation.CONTINUE;
        }
        if (!inclusive && entry.getTimestamp() > timestamp) {
            return Operation.CONTINUE;
        }
        return Operation.BREAK;
    }
}
