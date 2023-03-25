package org.example.customer;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
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
    String name;
    public CustomerAgent(String name) {
        this.name = name;
        menu = new Menu();
    }

    public boolean done() {
        return true;
    }
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("customer");
        sd.setName(name);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");
        // addBehaviour(new MakeOrder());

        addBehaviour(new OneShotBehaviour() {
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
        });
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                HashSet<MenuItem> items = menu.getMenu();
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("manager", AID.ISLOCALNAME));
                try {
                    Message message = new Message(myAgent.getLocalName(), items.toString(), sd.getType());
                    msg.setContentObject(message);
                    myAgent.send(msg);
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
