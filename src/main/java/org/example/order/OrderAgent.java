package org.example.order;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.persistence.DeleteAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.StaleProxyException;
import org.example.AgentGenerator;
import org.example.menu.MenuAgent;
import org.example.process.ProcessAgent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderAgent extends Agent {
    AID customerAID;
    List order;

    public static final String AGENT_TYPE = "order";


    private static int orderNumbers = 0;

    int ready = 0;

    TickerBehaviour behaviour;

    private int orderNumber;

    private double approximateTime = 0;

    private enum statuses {
        CREATED,
        IN_PROCESS,
        FINISHED,

    }

    statuses status;


    protected void setup() {
        System.out.println("Manager created new order " + this.getLocalName() + ", now it's ready to go!");
        Object[] args = getArguments();
        customerAID = (AID) args[0];
        order = (JSONArray) args[1];
        orderNumber = (Integer) args[2];
        status = statuses.CREATED;

        for (var item : order) {
            int processNumber = ProcessAgent.getProcessNumbers();
            try {
                approximateTime += MenuAgent.cards.get(MenuAgent.menu.get(item).getMenu_dish_card()).getCard_time();
                AgentGenerator.addAgent("Process " + processNumber, ProcessAgent.class.getName(), new Object[]{MenuAgent.menu.get(item).getMenu_dish_card(), getAID()});
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        }
        status = statuses.IN_PROCESS;


        behaviour = new Notify(this, 5000);
        addBehaviour(behaviour);

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();

                if (msg != null) {
                    try {
                        System.out.println("ORDER GOT MESSAGE");
                        JSONObject message = (JSONObject) msg.getContentObject();
                        if (msg.getPerformative() == ACLMessage.INFORM) {

                        }
                        if (msg.getPerformative() == ACLMessage.CONFIRM) {
                            System.out.println("ORDER DIED");
                            approximateTime -= (Double) message.get("time");
                            ++ready;
                            if (ready == order.size()) {
                                (myAgent).removeBehaviour(((OrderAgent)myAgent).behaviour);

                                sendDoneMessage(customerAID);

                                takeDown();
                                myAgent.doDelete();
                            }
                        }
                    } catch (UnreadableException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public boolean done() {
                return ready == order.size();
            }
        });
    }

    private class Notify extends TickerBehaviour {
        public Notify(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onTick() {

            ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
            aclMessage.addReceiver(customerAID);
            JSONObject message = new JSONObject();
            message.put("order", "approximate_time");
            message.put("approximate_time", approximateTime * 60);
            message.put("order_number", orderNumber);
            message.put("status", status);
            try {
                aclMessage.setContentObject(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            myAgent.send(aclMessage);
        }
    }

    private void sendDoneMessage(AID aid) {
        ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
        msg.addReceiver(aid);
        try {
            JSONObject message = new JSONObject();
            message.put("confirm", "order is ready");
            msg.setContentObject(message);
            send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void takeDown() {
        System.out.println(getAID().getLocalName() + " is shutting down");
        super.takeDown();
    }
    public static int getOrderNumbers() {
        return ++orderNumbers;
    }
}
