package org.example.order;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderAgent extends Agent {
    AID customerAID;
    List order;

    private static int orderNumbers = 0;

    private int orderNumber;

    private enum statuses {
        CREATED,

    }
    statuses status;


    protected void setup() {
        System.out.println("Manager created new order " + this.getLocalName() + ", now it's ready to go!");
        Object[] args = getArguments();
        customerAID = (AID) args[0];
        order = (ArrayList) args[1];
        orderNumber = (Integer) args[2];
        status = statuses.CREATED;
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
