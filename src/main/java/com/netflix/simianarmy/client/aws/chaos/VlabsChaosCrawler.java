package com.netflix.simianarmy.client.aws.chaos;


import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.TagDescription;
import com.netflix.simianarmy.GroupType;
import com.netflix.simianarmy.chaos.ChaosCrawler;
import com.netflix.simianarmy.client.aws.AWSClient;
import com.netflix.simianarmy.basic.chaos.BasicInstanceGroup;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;


/**
 * The Class VlabsChaosCrawler. This will crawl for all available EC2 Instance associated with a DeepField VLab name.
 */
public class VlabsChaosCrawler implements ChaosCrawler {

    /**
     * The group types Types.
     */
    public enum Types implements GroupType {

        /** only crawls EC2 Instances. */
        INSTANCE
    }

    /** The aws client. */
    private AWSClient awsClient;

    /**
     * Instantiates a new instance_group chaos crawler.
     *
     * @param awsClient the aws client
     * @return
     */
    public VlabsChaosCrawler(AWSClient awsClient) {
        this.awsClient = awsClient;
    }

    /** {@inheritDoc} */
    @Override
    public EnumSet<?> groupTypes() {
        return EnumSet.allOf(Types.class);
    }

    /** {@inheritDoc} */
    @Override
    public List<InstanceGroup> groups() {
        return groups((String[]) null);
    }

    @Override
    public List<InstanceGroup> groups(String... names) {
        List<InstanceGroup> list = new LinkedList<InstanceGroup>();
        if (names != null) {
            for (String name : names) {
                List<Instance> instances = awsClient.describeVlabsCluster(name);

                list.add(getInstanceGroup(name, instances));

            }
        } else {
            List<Instance> instances = awsClient.describeVlabsCluster("*");
            list.add(getInstanceGroup("*", instances));
        }

        return list;
    }


    /**
     * Returns the desired InstanceGroup.
     * @param name the name of the instance group
     * @param vlabsInstances The vlab instances in the group
     * @return The appropriate {@link InstanceGroup}
     */
    private InstanceGroup getInstanceGroup(String name, List<Instance> vlabsInstances) {
        InstanceGroup instanceGroup;

        TagDescription tag = new TagDescription().withKey("vlabs-name").withValue(name);
        List<TagDescription> tags = new LinkedList<>();
        tags.add(tag);

        instanceGroup = new BasicInstanceGroup(name, Types.INSTANCE, awsClient.region(), tags);

        for (Instance instance : vlabsInstances) {
            instanceGroup.addInstance(instance.getInstanceId());
        }

        return instanceGroup;
    }
}
