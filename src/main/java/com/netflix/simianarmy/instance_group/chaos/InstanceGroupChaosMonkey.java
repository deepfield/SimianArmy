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
package com.netflix.simianarmy.instance_group.chaos;

import com.netflix.simianarmy.*;
import com.netflix.simianarmy.basic.chaos.BasicChaosMonkey;
import com.netflix.simianarmy.chaos.*;
import com.netflix.simianarmy.instance_group.InstanceGroupChaosMonkeyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class InstanceGroupChaosMonkey.
 */
public class InstanceGroupChaosMonkey extends BasicChaosMonkey {

    /** The Constant LOGGER. */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceGroupChaosMonkey.class);

    /** The Constant NS. */
    private static final String NS = "simianarmy.chaos.";

    /** The cfg. */
    private final MonkeyConfiguration cfg;

    /**
     * Instantiates a new basic chaos monkey.
     * @param ctx
     *            the ctx
     */
    public InstanceGroupChaosMonkey(InstanceGroupChaosMonkeyContext ctx) {
        super(ctx);

        this.cfg = ctx.configuration();
    }

    /** {@inheritDoc} */
    @Override
    public void doMonkeyBusiness() {
        context().resetEventReport();
        cfg.reload();
        String[] vLabsNames = this.cfg.getStr(NS + "vlabs.name").split(",");
        if (!isChaosMonkeyEnabled()) {
            return;
        }
        for (ChaosCrawler.InstanceGroup group : context().chaosCrawler().groups(vLabsNames)) {
            Boolean exit = monkeyBusiness(group);
            if (exit) {
                break;
            }
        }
    }
}
