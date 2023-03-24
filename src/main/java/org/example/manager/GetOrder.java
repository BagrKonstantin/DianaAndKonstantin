package org.example.manager;

import org.example.message.Message;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class GetOrder extends Behaviour {

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            try {
                Message message = (Message) msg.getContentObject();
                System.out.println("Order by " + message.localName + ": " + message.content);
//              myAgent.addBehaviour(new CreateOrder());
            } catch (UnreadableException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
