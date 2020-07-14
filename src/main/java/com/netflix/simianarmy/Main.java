package com.netflix.simianarmy;

import javax.servlet.ServletException;

import com.netflix.simianarmy.basic.BasicMonkeyServer;

public class Main {
    public static void main(String[] args) throws ServletException {
        BasicMonkeyServer server = new BasicMonkeyServer();
        server.init();
    }
}
