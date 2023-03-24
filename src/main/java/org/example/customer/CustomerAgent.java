package org.example.customer;

import jade.core.Agent;

public class CustomerAgent extends Agent {
    protected void setup() {
        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");
        addBehaviour(new MakeOrder());
    }

    @Override
    protected void takeDown() {
        System.out.println(getAID().getLocalName() + " is shutting down");
        super.takeDown();
    }
}
