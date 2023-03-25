package org.example.customer;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.example.manager.ManagerAgent;
import org.example.temporal_agent.Controller;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


public class CustomerAgent extends Agent {
    String name;
    public CustomerAgent(String name) {
        this.name = name;
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
    }

    @Override
    protected void takeDown() {
        System.out.println(getAID().getLocalName() + " is shutting down");
        super.takeDown();
    }


}
