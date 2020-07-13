package com.netflix.simianarmy;

import javax.servlet.ServletException;

import com.netflix.simianarmy.instance_group.InstanceGroupMonkeyServer;

public class Main {
    public static void main(String[] args) throws ServletException {
        InstanceGroupMonkeyServer server = new InstanceGroupMonkeyServer();
        server.init();
    }
}
