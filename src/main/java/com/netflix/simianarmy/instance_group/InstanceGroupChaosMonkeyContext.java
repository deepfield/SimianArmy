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

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.netflix.simianarmy.MonkeyConfiguration;
import com.netflix.simianarmy.basic.BasicSimianArmyContext;
import com.netflix.simianarmy.basic.chaos.BasicChaosEmailNotifier;
import com.netflix.simianarmy.basic.chaos.BasicChaosInstanceSelector;
import com.netflix.simianarmy.chaos.ChaosCrawler;
import com.netflix.simianarmy.chaos.ChaosEmailNotifier;
import com.netflix.simianarmy.chaos.ChaosInstanceSelector;
import com.netflix.simianarmy.chaos.ChaosMonkey;
import com.netflix.simianarmy.client.aws.chaos.InstanceGroupChaosCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class BasicContext. This provide the basic context needed for the Chaos Monkey to run. It will configure
 * the Chaos Monkey based on a simianarmy.properties file and chaos.properties. The properties file can be
 * overridden with -Dsimianarmy.properties=/path/to/my.properties
 */
public class InstanceGroupChaosMonkeyContext extends BasicSimianArmyContext implements ChaosMonkey.Context {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceGroupChaosMonkeyContext.class);

    /** The crawler. */
    private ChaosCrawler crawler;

    /** The selector. */
    private ChaosInstanceSelector selector;

    /** The chaos email notifier. */
    private ChaosEmailNotifier chaosEmailNotifier;

    private static final String DEFAULT_REGION = "us-east-2";

    /**
     * Instantiates a new basic context.
     */
    public InstanceGroupChaosMonkeyContext() throws RuntimeException {
        super("simianarmy.properties", "client.properties", "chaos.properties");
        MonkeyConfiguration cfg = configuration();

        InstanceGroupChaosCrawler chaosCrawler = new InstanceGroupChaosCrawler(awsClient());
        setChaosCrawler(chaosCrawler);
        setChaosInstanceSelector(new BasicChaosInstanceSelector());
        AmazonSimpleEmailServiceClientBuilder sesClientBuilder = AmazonSimpleEmailServiceClientBuilder
            .standard()
            .withClientConfiguration(awsClientConfig);
        if (cfg.getStr("simianarmy.client.aws.region") == null) {
            LOGGER.info("Using default region " + DEFAULT_REGION);
            sesClientBuilder.withRegion(DEFAULT_REGION);
        } else if (cfg.getStr("simianarmy.client.aws.region").equals("us-east-1")) {
            String msg = "Region property simianarmy.client.aws.region cannot be set to us-east-1.\n" +
                "Change this property in client.properties";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        } else {
            sesClientBuilder.withRegion(configuration().getStr("simianarmy.aws.email.region"));
        }

        setChaosEmailNotifier(new BasicChaosEmailNotifier(cfg, sesClientBuilder.build(), null));
    }

    /** {@inheritDoc} */
    @Override
    public ChaosCrawler chaosCrawler() {
        return crawler;
    }

    /**
     * Sets the chaos crawler.
     *
     * @param chaosCrawler
     *            the new chaos crawler
     */
    private void setChaosCrawler(ChaosCrawler chaosCrawler) {
        this.crawler = chaosCrawler;
    }

    /** {@inheritDoc} */
    @Override
    public ChaosInstanceSelector chaosInstanceSelector() {
        return selector;
    }

    /**
     * Sets the chaos instance selector.
     *
     * @param chaosInstanceSelector
     *            the new chaos instance selector
     */
    private void setChaosInstanceSelector(ChaosInstanceSelector chaosInstanceSelector) {
        this.selector = chaosInstanceSelector;
    }

    @Override
    public ChaosEmailNotifier chaosEmailNotifier() {
        return chaosEmailNotifier;
    }

    /**
     * Sets the chaos email notifier.
     *
     * @param notifier
     *            the chaos email notifier
     */
    private void setChaosEmailNotifier(ChaosEmailNotifier notifier) {
        this.chaosEmailNotifier = notifier;
    }
}
