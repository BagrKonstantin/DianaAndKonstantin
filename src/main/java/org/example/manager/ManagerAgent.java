package org.example.manager;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.menu.MenuAgent;
import org.example.message.Message;

import java.io.IOException;
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
                        System.out.println(msg.getSender());
                        if (message.type.equals("customer")) {
                            DFAgentDescription template = new DFAgentDescription();
                            ServiceDescription sd = new ServiceDescription();
                            sd.setType("menu");
                            template.addServices(sd);
                            DFAgentDescription[] result;
                            try {
                                result = DFService.search(myAgent, template);
                            } catch (FIPAException e) {
                                throw new RuntimeException(e);
                            }

                            // ask menu
                            System.out.println("Manager recieved: " + message.content);
                            ((ManagerAgent) myAgent).menuRequest.add(msg.getSender());

                            ACLMessage aclMessage = new ACLMessage();

                            aclMessage.addReceiver(result[0].getName());

                            Message message1 = new Message(myAgent.getLocalName(), "hachu", "manager");

                            aclMessage.setContentObject(message1);
                            myAgent.send(aclMessage);
                        } else if (message.type.equals("menu")) {
                            System.out.println("Got answer from menu ффф");


                            ACLMessage aclMessage = new ACLMessage();
                            Message message1 = new Message(myAgent.getLocalName(), "a", "manager");
                            var sender = ((ManagerAgent) myAgent).menuRequest.poll();
                            aclMessage.addReceiver(sender);
                            System.out.println(sender);
                            aclMessage.setContentObject(message1);
                            myAgent.send(aclMessage);

                        }
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
//        addBehaviour(new GiveOrder());
    }

}
