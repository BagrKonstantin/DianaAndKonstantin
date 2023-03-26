package org.example.equipment;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.cooker.CookerAgent;
import org.example.order.OrderAgent;
import org.example.process.ProcessAgent;
import org.json.simple.JSONObject;

import java.io.IOException;

public class EquipmentAgent extends Agent {

    boolean isBusy;

    Long equipmentTypeId;

    String equip_name;
    Boolean equip_active;

    public static final String AGENT_TYPE = "equipment";

    protected void notifyAllProcesses() throws IOException {

        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(ProcessAgent.AGENT_TYPE);
        template.addServices(sd);
        DFAgentDescription[] result;
        try {
            result = DFService.search(this, template);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < result.length; i++) {
            msg.addReceiver(result[i].getName());
        }
        JSONObject message = new JSONObject();
        message.put("propose", AGENT_TYPE);
        message.put(AGENT_TYPE, equipmentTypeId);
        msg.setContentObject(message);
        send(msg);
    }

    protected void setup() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AGENT_TYPE);
        sd.setName("Equipment in restaurant");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");

        Object[] args = getArguments();
        this.equipmentTypeId = (Long) ((JSONObject) args[0]).get("equip_type");
        //equipmentTypeId = (Long) args[0];
        this.equip_name = (String) ((JSONObject) args[0]).get("equip_name");
        this.equip_active = (Boolean) ((JSONObject) args[0]).get("equip_active");
        addBehaviour(new TickerBehaviour(this, 1000) {

            @Override
            protected void onTick() {
                try {
                    if (!isBusy) {
                        ((EquipmentAgent)myAgent).notifyAllProcesses();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        addBehaviour(new waitForProposal());
    }

    private class waitForProposal extends Behaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                try {
                    JSONObject message = (JSONObject) msg.getContentObject();
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        if (message.get("propose").equals("work")) {
                            if (!isBusy) {
                                isBusy = true;
                                sendProposeConfirmMessage(msg.getSender());
                            }
                        }

                    }
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        System.out.println("Equipment working");
                    }
                    if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                        isBusy = false;
                    }
                    if (msg.getPerformative() == ACLMessage.INFORM) {
                        isBusy = false;
                    }
                } catch (UnreadableException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public boolean done() {
            return false;
        }
    }

    private void sendProposeConfirmMessage(AID aid) {
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msg.addReceiver(aid);
        try {
            JSONObject message = new JSONObject();
            message.put("propose", AGENT_TYPE);
            msg.setContentObject(message);
            send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
