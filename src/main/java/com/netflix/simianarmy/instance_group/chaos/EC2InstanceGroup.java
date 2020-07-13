package com.netflix.simianarmy.instance_group.chaos;

import com.amazonaws.services.ec2.model.TagDescription;
import com.netflix.simianarmy.GroupType;
import com.netflix.simianarmy.chaos.ChaosCrawler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EC2InstanceGroup implements ChaosCrawler.InstanceGroup {

    /** The name. */
    private final String name;

    /** The type. */
    private final GroupType type;

    /** The region. */
    private final String region;

    /** list of the tags of the ASG */
    private final List<TagDescription> ec2Tags;


    /**
     * Instantiates a new basic instance group.
     *
     * @param name
     *            the name
     * @param type
     *            the type
     * @param ec2Tags
     *            the EC2 tags
     */
    public EC2InstanceGroup(String name, GroupType type, String region, List<TagDescription> ec2Tags) {
        this.name = name;
        this.type = type;
        this.region = region;
        this.ec2Tags = ec2Tags;
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

    /** {@inheritDoc} */
    public List<com.amazonaws.services.autoscaling.model.TagDescription> tags() {
        return null;
    }

    private List<TagDescription> ec2Tags() {
        return ec2Tags;
    }

    /** The list. */
    private List<String> list = new LinkedList<>();

    /** {@inheritDoc} */
    @Override
    public List<String> instances() {
        return Collections.unmodifiableList(list);
    }

    /** {@inheritDoc} */
    @Override
    public void addInstance(String instance) {
        list.add(instance);
    }

    /** {@inheritDoc} */
    @Override
    public EC2InstanceGroup copyAs(String newName) {
        EC2InstanceGroup newGroup = new EC2InstanceGroup(newName, this.type(), this.region(), this.ec2Tags());
        for (String instance: this.instances()) {
            newGroup.addInstance(instance);
        }
        return newGroup;
    }
}
