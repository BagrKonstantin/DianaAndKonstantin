package org.example.manager;

import jade.core.Agent;

public class ManagerAgent extends Agent {

    protected void setup() {

        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");
        addBehaviour(new GetOrder());
//        addBehaviour(new GiveOrder());
    }

}
