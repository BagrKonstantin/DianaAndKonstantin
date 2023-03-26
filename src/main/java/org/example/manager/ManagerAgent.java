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
import jade.wrapper.StaleProxyException;
import org.example.AgentGenerator;
import org.example.menu.MenuAgent;
import org.example.order.OrderAgent;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

public class ManagerAgent extends Agent {

    private Queue<AID> menuRequest;

    public static final String AGENT_TYPE = "manager";
    public static final String REQUEST = "request";


    private List<AID> chefAgents;
    private AID menuAgent;

    public ManagerAgent() {
        menuRequest = new LinkedList<>();
    }

    public void findMenu() {
        //menuAgent = this.getArguments()[0];
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(MenuAgent.AGENT_TYPE);
        template.addServices(sd);
        DFAgentDescription[] result;
        try {
            result = DFService.search(this, template);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        menuAgent = result[0].getName();


    }

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AGENT_TYPE);
        sd.setName("Work with customers");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        findMenu();

        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    try {
                        JSONObject json = (JSONObject) msg.getContentObject();
                        System.out.println("Manager recieved: " + json);
                        if (msg.getPerformative() == ACLMessage.REQUEST) {
                            if (json.get(REQUEST).equals("menu")) {
                                Logger.getGlobal().info(myAgent.getAID().getLocalName() + " request menu from menu");


                                ((ManagerAgent) myAgent).menuRequest.add(msg.getSender());

                                ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                                aclMessage.addReceiver(menuAgent);
                                JSONObject message = new JSONObject();
                                message.put(REQUEST, "menu");
                                aclMessage.setContentObject(message);
                                myAgent.send(aclMessage);
                            } else if (json.get(REQUEST).equals("order")) {
                                Logger.getGlobal().info(myAgent.getAID().getLocalName() + " created order");

                                int orderNumber = OrderAgent.getOrderNumbers();
                                AgentGenerator.addAgent("Order " + orderNumber, OrderAgent.class.getName(), new Object[]{msg.getSender(), json.get("order"), orderNumber});
                            }
                        } else if (msg.getPerformative() == ACLMessage.CONFIRM) {
                            Logger.getGlobal().info(myAgent.getAID().getLocalName() + " sended menu to customer");

                            System.out.println("Got answer from menu");
                            ACLMessage aclMessage = new ACLMessage();
                            aclMessage.addReceiver(((ManagerAgent) myAgent).menuRequest.poll());
                            aclMessage.setContentObject(json);
                            myAgent.send(aclMessage);
                        }
                    } catch (UnreadableException e) {
                        System.out.println(e);
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (StaleProxyException e) {
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
