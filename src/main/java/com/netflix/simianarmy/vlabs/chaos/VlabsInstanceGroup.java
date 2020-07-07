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

import com.amazonaws.services.autoscaling.model.TagDescription;
import com.amazonaws.services.ec2.model.Instance;
import com.netflix.simianarmy.GroupType;
import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class VlabsInstanceGroup.
 */
public class VlabsInstanceGroup implements InstanceGroup {

    /** The name. */
    private final String name;

    /** The type. */
    private final GroupType type;

    /** The region. */
    private final String region;

    /** list of the EC2 instance IDs */
    private final List<String> instances;

    /**
     * Instantiates a new basic instance group.
     *
     * @param name
     *            the name
     * @param type
     *            the type
     * @param instanceIds
     *            the EC2 Instance IDs
     */
    public VlabsInstanceGroup(String name, GroupType type, String region, List<String> instanceIds) {
        this.name = name;
        this.type = type;
        this.region = region;
        this.instances = instanceIds;
    }



    /** {@inheritDoc} */
    public GroupType type() {
        return type;
    }

    /** {@inheritDoc} */
    public String name() {
        return name;
    }

    /** {@inheritDoc} */
    public String region() {
        return region;
    }

    @Override
    public List<TagDescription> tags() {
        // Not implementing this because it has to do with ASGs
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> instances() {
        return this.instances;
    }

    /** {@inheritDoc} */
    @Override
    public void addInstance(String instance) {
        this.instances.add(instance);
    }

    /** {@inheritDoc} */
    @Override
    public VlabsInstanceGroup copyAs(String newName) {
        VlabsInstanceGroup newGroup = new VlabsInstanceGroup(newName, this.type(), this.region(), this.instances());
        for (String instance: this.instances()) {
            newGroup.addInstance(instance);
        }
        return newGroup;
    }
}
