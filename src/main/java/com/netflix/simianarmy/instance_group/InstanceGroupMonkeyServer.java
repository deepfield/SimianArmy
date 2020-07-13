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
package com.netflix.simianarmy.instance_group;

import com.netflix.simianarmy.MonkeyRunner;
import com.netflix.simianarmy.basic.BasicMonkeyServer;
import com.netflix.simianarmy.instance_group.chaos.InstanceGroupChaosMonkey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Will periodically run the configured monkeys.
 */
@SuppressWarnings("serial")
public class InstanceGroupMonkeyServer extends BasicMonkeyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceGroupMonkeyServer.class);

    private static final MonkeyRunner RUNNER = MonkeyRunner.getInstance();

    /**
     * Add the monkeys that will be run.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addMonkeysToRun() {
        LOGGER.info("Adding Instance Group Chaos Monkey.");
        RUNNER.replaceMonkey(this.chaosClass, this.chaosContextClass);
    }

    /**
     * make the class of the client object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class chaosContextClass = InstanceGroupChaosMonkeyContext.class;

    /**
     * make the class of the chaos object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class chaosClass = InstanceGroupChaosMonkey.class;

    @SuppressWarnings("unchecked")
    @Override
    public void destroy() {
        RUNNER.stop();
        LOGGER.info("Stopping Vlabs Chaos Monkey.");
        RUNNER.removeMonkey(this.chaosClass);
    }
}
