package org.example.equipment;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.example.cooker.CookerAgent;
import org.example.order.OrderAgent;
import org.json.simple.JSONObject;

import java.io.IOException;

public class EquipmentAgent extends Agent {

    boolean isBusy;

    Long equipmentTypeId;

    public static final String AGENT_TYPE = "equipment";

    protected void notifyAllProcesses() throws IOException {

        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(OrderAgent.AGENT_TYPE);
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
        addBehaviour(new TickerBehaviour(this, 1000) {

            @Override
            protected void onTick() {
                try {
                    ((EquipmentAgent)myAgent).notifyAllProcesses();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }



}
