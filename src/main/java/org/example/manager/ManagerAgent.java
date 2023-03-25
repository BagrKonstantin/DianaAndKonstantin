package org.example.manager;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.message.Message;

import java.util.LinkedList;
import java.util.Queue;

public class ManagerAgent extends Agent {

    private Queue<AID> menuRequest;

    protected void setup() {

        menuRequest = new LinkedList<>();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("manager");
        sd.setName("Manager");
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
                        if (message.type.equals("customer")) {
                            // ask menu
                            ((ManagerAgent) myAgent).menuRequest.add(message.senderIid);
                        } else if (message.type.equals("menu")) {
                            //Message menuMessage = new Message(myAgent.getLocalName(), "this is json menu", myAgent.getName());
                            //myAgent.send();
                            ACLMessage aclMessage = new ACLMessage();

                            aclMessage.addReceiver(((ManagerAgent) myAgent).menuRequest.poll());
                            aclMessage.setContent("menu");
                            myAgent.send(aclMessage);
                        }
                        System.out.println("Order by " + message.localName + ": " + message.content);
                    } catch (UnreadableException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
//        addBehaviour(new GiveOrder());
    }

}
