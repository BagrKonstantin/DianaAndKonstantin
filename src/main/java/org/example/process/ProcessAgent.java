package org.example.process;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.cooker.CookerAgent;
import org.example.customer.CustomerAgent;
import org.example.equipment.EquipmentAgent;
import org.example.menu.MenuItem;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class ProcessAgent extends Agent {

    public static final String AGENT_TYPE = "process";


    private static int processNumbers = 0;

    private Long eqId = 1L;

    private Long dish;

    protected void setup() {
        System.out.println("Process started");
        Object[] args = getArguments();
        dish = (Long) args[0];
        System.out.println(dish);
    }


    class waitForFreeStuff extends Behaviour {
        boolean hasCooker;
        boolean hasEquipment;

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                try {
                    JSONObject message = (JSONObject) msg.getContentObject();
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        if (message.get("propose").equals(CookerAgent.AGENT_TYPE)) {
                            sendProposeMessage(msg.getSender());
                        }
                        if (message.get("propose").equals(EquipmentAgent.AGENT_TYPE)) {
                            if (message.get(EquipmentAgent.AGENT_TYPE) == eqId) {
                                sendProposeMessage(msg.getSender());
                            }
                        }
                    }
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        if (message.get("propose").equals(CookerAgent.AGENT_TYPE)) {
                            hasCooker = true;
                        }
                        if (message.get("propose").equals(EquipmentAgent.AGENT_TYPE)) {
                            hasEquipment = true;
                        }

                    }
                } catch (UnreadableException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public boolean done() {
            return hasEquipment && hasCooker;
        }
    }

    private void sendProposeMessage(AID aid) {
        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.addReceiver(aid);
        try {
            JSONObject message = new JSONObject();
            message.put("propose", "work");
            msg.setContentObject(message);
            send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static int getProcessNumbers() {
        return ++processNumbers;
    }
}
