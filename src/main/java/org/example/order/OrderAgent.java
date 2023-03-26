package org.example.order;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
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

    private int orderNumber;

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
                AgentGenerator.addAgent("Process " + processNumber, ProcessAgent.class.getName(), new Object[]{MenuAgent.menu.get(item).getMenu_dish_card()});
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        }
        addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            public void onTick() {
                ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                aclMessage.addReceiver(customerAID);
                JSONObject message = new JSONObject();
                message.put("order", "approximate_time");
                message.put("approximate_time", 100);
                message.put("order_number", orderNumber);
                message.put("status", status);
                try {
                    aclMessage.setContentObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                myAgent.send(aclMessage);
            }
        });
    }

    public static int getOrderNumbers() {
        return ++orderNumbers;
    }
}
