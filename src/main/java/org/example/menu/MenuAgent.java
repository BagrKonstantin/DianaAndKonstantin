package org.example.menu;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.manager.ManagerAgent;
import org.example.message.Message;

import java.io.IOException;
import java.util.HashSet;

public class MenuAgent extends Agent {

    HashSet<MenuItem> menu;

    protected void setup() {

        menu = new HashSet<MenuItem>();
        menu.add(new MenuItem(0, 0, true));
        menu.add(new MenuItem(1, 2, true));
        menu.add(new MenuItem(2, 1, true));
        menu.add(new MenuItem(3, 3, true));

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("menu");
        sd.setName("Menu");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    try {
                        Message message = (Message) msg.getContentObject();
                        System.out.println(message);


                        System.out.println("Menu recieved: " + message.content);

                        ACLMessage aclMessage = new ACLMessage();

                        aclMessage.addReceiver(msg.getSender());
                        Message m = new Message(myAgent.getLocalName(), "here is menu", "menu");

                        aclMessage.setContentObject(m);
                        myAgent.send(aclMessage);
                    } catch (UnreadableException e) {
                        System.out.println(e);
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        System.out.println(e);
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });


    }


}
