package org.example.customer;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class GetFood extends OneShotBehaviour {

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            String cell_id = msg.getContent();
            ACLMessage reply = msg.createReply();
            try {
                reply.setContent("Collected order at cell: " + cell_id);
                myAgent.send(reply);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        myAgent.doDelete();
    }
}
