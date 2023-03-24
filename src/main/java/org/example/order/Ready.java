package org.example.order;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class Ready extends Behaviour {
    @Override
    public void action() {
        myAgent.doWait(1000);
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Manager", AID.ISLOCALNAME));
        try {
            //msg.setContent(items.toString());
            myAgent.send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
