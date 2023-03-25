package org.example.customer;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.manager.ManagerAgent;
import org.example.menu.Menu;
import org.example.menu.MenuItem;
import org.example.message.Message;
import org.example.temporal_agent.Controller;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashSet;


public class CustomerAgent extends Agent {
    Menu menu;

    public boolean done() {
        return true;
    }

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("customer");
        sd.setName(this.getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");
        // addBehaviour(new MakeOrder());
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    try {
                        Message message = (Message) msg.getContentObject();
                        System.out.println(message);
                        done();

                    } catch (UnreadableException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public boolean done() {
                return true;
            }
        });

//        addBehaviour(new OneShotBehaviour() {
//            @Override
//            public void action() {
//                ACLMessage msg = myAgent.receive();
//                if (msg != null) {
//                    String cell_id = msg.getContent();
//                    ACLMessage reply = msg.createReply();
//                    try {
//                        reply.setContent("Collected order at cell: " + cell_id);
//                        myAgent.send(reply);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                //myAgent.doDelete();
//            }
//        });
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                //HashSet<MenuItem> items = menu.getMenu();
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("manager");
                template.addServices(sd);
                DFAgentDescription[] result;
                try {
                     result = DFService.search(myAgent, template);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(result[0].getName());
                try {
                    Message message = new Message(myAgent.getLocalName(), "Хачу меню", myAgent.getName());
                    msg.setContentObject(message);
                    myAgent.send(msg);
                    System.out.println("Cumstomer asked for menu");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println(getAID().getLocalName() + " is shutting down");
        super.takeDown();
    }


}
