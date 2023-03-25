package org.example.customer;

import org.example.menu.Menu;
import org.example.menu.MenuItem;
import org.example.message.Message;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashSet;

public class MakeOrder extends Behaviour {
    Menu menu;

    public MakeOrder() {
        menu = new Menu();
    }

    @Override
    public void action() {
        HashSet<MenuItem> items = menu.getMenu();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Manager", AID.ISLOCALNAME));
        try {
            //Message message = new Message(myAgent.getLocalName(), items.toString());
            //msg.setContentObject(message);
            //myAgent.send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean done() {
        return true;
    }
}
