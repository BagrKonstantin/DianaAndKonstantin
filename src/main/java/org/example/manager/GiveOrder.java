package org.example.manager;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class GiveOrder extends Behaviour {
    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            String items = msg.getContent();
            System.out.println("Order: " + items);
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
