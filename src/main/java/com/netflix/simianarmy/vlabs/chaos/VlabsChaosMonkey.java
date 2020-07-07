/*
 *
 *  Copyright 2012 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.simianarmy.vlabs.chaos;

import com.google.common.collect.Lists;
import com.netflix.simianarmy.*;
import com.netflix.simianarmy.MonkeyRecorder.Event;
import com.netflix.simianarmy.basic.chaos.BasicChaosMonkey;
import com.netflix.simianarmy.chaos.*;
import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;
import com.netflix.simianarmy.vlabs.VlabsChaosMonkeyContext;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The Class BasicChaosMonkey.
 */
public class VlabsChaosMonkey extends BasicChaosMonkey {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VlabsChaosMonkey.class);

    /** The Constant NS. */
    private static final String NS = "simianarmy.chaos.vlabs.";

    /** The cfg. */
    private final MonkeyConfiguration cfg;

    /** The runs per day. */
    private final long runsPerDay;

    /** The minimum value of the maxTerminationCountPerday property to be considered non-zero. **/
    private static final double MIN_MAX_TERMINATION_COUNT_PER_DAY = 0.001;

    private final MonkeyCalendar monkeyCalendar;

    // When a mandatory termination is triggered due to the minimum termination limit is breached,
    // the value below is used as the termination probability.
    private static final double DEFAULT_MANDATORY_TERMINATION_PROBABILITY = 0.5;

    private final List<ChaosType> allChaosTypes;

    /**
     * Instantiates a new basic chaos monkey.
     * @param ctx
     *            the ctx
     */
    public VlabsChaosMonkey(VlabsChaosMonkeyContext ctx) {
        super(ctx);

        this.cfg = ctx.configuration();
        this.monkeyCalendar = ctx.calendar();

        Calendar open = monkeyCalendar.now();
        Calendar close = monkeyCalendar.now();
        open.set(Calendar.HOUR, monkeyCalendar.openHour());
        close.set(Calendar.HOUR, monkeyCalendar.closeHour());

        allChaosTypes = Lists.newArrayList();
        allChaosTypes.add(new ShutdownInstanceChaosType(cfg));
        allChaosTypes.add(new BlockAllNetworkTrafficChaosType(cfg));
        allChaosTypes.add(new DetachVolumesChaosType(cfg));
        allChaosTypes.add(new BurnCpuChaosType(cfg));
        allChaosTypes.add(new BurnIoChaosType(cfg));
        allChaosTypes.add(new KillProcessesChaosType(cfg));
        allChaosTypes.add(new NullRouteChaosType(cfg));
        allChaosTypes.add(new FailEc2ChaosType(cfg));
        allChaosTypes.add(new FailDnsChaosType(cfg));
        allChaosTypes.add(new FailDynamoDbChaosType(cfg));
        allChaosTypes.add(new FailS3ChaosType(cfg));
        allChaosTypes.add(new FillDiskChaosType(cfg));
        allChaosTypes.add(new NetworkCorruptionChaosType(cfg));
        allChaosTypes.add(new NetworkLatencyChaosType(cfg));
        allChaosTypes.add(new NetworkLossChaosType(cfg));

        TimeUnit freqUnit = ctx.scheduler().frequencyUnit();
        if (TimeUnit.DAYS == freqUnit) {
            runsPerDay = ctx.scheduler().frequency();
        } else {
            long units = freqUnit.convert(close.getTimeInMillis() - open.getTimeInMillis(), TimeUnit.MILLISECONDS);
            runsPerDay = units / ctx.scheduler().frequency();
        }
    }
}
