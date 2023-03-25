package org.example.customer;

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
import org.json.simple.JSONObject;


public class CustomerAgent extends Agent {

    public static final String AGENT_TYPE = "customer";

    private boolean receivedMenu = false;

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AGENT_TYPE);
        sd.setName("Customer who came to a restaurant");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");
        // addBehaviour(new MakeOrder());


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
                sd.setType(ManagerAgent.AGENT_TYPE);
                template.addServices(sd);
                DFAgentDescription[] result;
                try {
                     result = DFService.search(myAgent, template);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(result[0].getName());
                try {
                    JSONObject message = new JSONObject();
                    message.put("request", "menu");
                    msg.setContentObject(message);
                    myAgent.send(msg);
                    System.out.println("Cumstomer asked for menu");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();

                if (msg != null) {
                    System.out.println("GOT");

                    try {
                        JSONObject message = (JSONObject) msg.getContentObject();
                        System.out.println(message);
                        receivedMenu = true;

                    } catch (UnreadableException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public boolean done() {
                return receivedMenu;
            }
        });


    }


    @Override
    protected void takeDown() {
        System.out.println(getAID().getLocalName() + " is shutting down");
        super.takeDown();
    }


}
