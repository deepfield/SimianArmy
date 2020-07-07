package com.netflix.simianarmy.client.aws.chaos;


import com.amazonaws.services.ec2.model.Instance;
import com.netflix.simianarmy.GroupType;
import com.netflix.simianarmy.chaos.ChaosCrawler;
import com.netflix.simianarmy.client.aws.AWSClient;
import com.netflix.simianarmy.vlabs.chaos.VlabsInstanceGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;


/**
 * The Class VlabChaosCrawler. This will crawl for all available EC2 Instance associated with a DeepField VLab name.
 */
public class VlabChaosCrawler implements ChaosCrawler {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VlabChaosCrawler.class);

    /**
     * The key of the tag that set the aggression coefficient
     */
    private static final String CHAOS_MONKEY_AGGRESSION_COEFFICIENT_KEY = "chaosMonkey.aggressionCoefficient";

    /**
     * The group types Types.
     */
    public enum Types implements GroupType {

        /** only crawls EC2 Instances. */
        INSTANCE
    }

    /** The aws client. */
    private final AWSClient awsClient;

    /**
     * Instantiates a new vlabs chaos crawler.
     *
     * @param awsClient
     *            the aws client
     */
    public VlabChaosCrawler(AWSClient awsClient) {
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

                list.add(getVlabInstanceGroup(name, instances));

            }
        } else {
            LOGGER.debug("No vlab name specified in config.");
        }

        return list;
    }


    /**
     * Returns the desired InstanceGroup.
     * @param name the name of the instance group
     * @param vlabsInstances The vlab instances in the group
     * @return The appropriate {@link InstanceGroup}
     */
    protected InstanceGroup getVlabInstanceGroup(String name, List<Instance> vlabsInstances) {
        InstanceGroup instanceGroup;

        List<String> instanceIds = new LinkedList<>();

        for (Instance i : vlabsInstances) {
            instanceIds.add(i.getInstanceId());
        }

        instanceGroup = new VlabsInstanceGroup(name, Types.INSTANCE, awsClient.region(), instanceIds);

        return instanceGroup;
    }
}
