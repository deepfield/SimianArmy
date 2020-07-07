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
package com.netflix.simianarmy.vlabs;

import com.netflix.simianarmy.MonkeyRunner;
import com.netflix.simianarmy.aws.janitor.VolumeTaggingMonkey;
import com.netflix.simianarmy.basic.BasicMonkeyServer;
import com.netflix.simianarmy.basic.conformity.BasicConformityMonkey;
import com.netflix.simianarmy.basic.conformity.BasicConformityMonkeyContext;
import com.netflix.simianarmy.basic.janitor.BasicJanitorMonkey;
import com.netflix.simianarmy.basic.janitor.BasicJanitorMonkeyContext;
import com.netflix.simianarmy.basic.janitor.BasicVolumeTaggingMonkeyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Will periodically run the configured monkeys.
 */
@SuppressWarnings("serial")
public class VlabsMonkeyServer extends BasicMonkeyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VlabsMonkeyServer.class);

    private static final MonkeyRunner RUNNER = MonkeyRunner.getInstance();

    /**
     * Add the monkeys that will be run.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addMonkeysToRun() {
        LOGGER.info("Adding Vlabs Chaos Monkey.");
        RUNNER.replaceMonkey(this.chaosClass, this.chaosContextClass);
    }

    /**
     * make the class of the client object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class chaosContextClass = com.netflix.simianarmy.vlabs.VlabsChaosMonkeyContext.class;

    /**
     * make the class of the chaos object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class chaosClass = com.netflix.simianarmy.vlabs.chaos.VlabsChaosMonkey.class;

    @SuppressWarnings("unchecked")
    @Override
    public void destroy() {
        RUNNER.stop();
        LOGGER.info("Stopping Vlabs Chaos Monkey.");
        RUNNER.removeMonkey(this.chaosClass);
    }
}
