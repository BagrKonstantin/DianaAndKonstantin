package org.example;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class AgentGenerator {
    static final Runtime rt = Runtime.instance();
    static final Profile p = new ProfileImpl();


    static ContainerController containerController;
    AgentGenerator() {

        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "8080");
        p.setParameter(Profile.GUI, "true");
        containerController = rt.createMainContainer(p);
    }

    public static void addAgent(String name, String className) throws StaleProxyException {
        containerController.createNewAgent(name, className, new String[]{}).start();
    }

    public static void addAgent(String name, String className, Object[] args) throws StaleProxyException {
        containerController.createNewAgent(name, className, args).start();
    }
}
