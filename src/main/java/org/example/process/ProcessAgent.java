package org.example.process;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.cooker.CookerAgent;
import org.example.equipment.EquipmentAgent;
import org.example.menu.Card;
import org.example.menu.MenuAgent;
import org.json.simple.JSONObject;

public class ProcessAgent extends Agent {

    public static final String AGENT_TYPE = "process";


    private static int processNumbers = 0;


    private Long dishCardId;

    private Card card;

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AGENT_TYPE);
        sd.setName("Process of cooking");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Process started");
        Object[] args = getArguments();
        dishCardId = (Long) args[0];
        card = MenuAgent.cards.get(dishCardId);
        System.out.println(dishCardId);

        addBehaviour(new waitForFreeStuff());
    }


    class waitForFreeStuff extends Behaviour {
        boolean hasCooker = false;
        boolean hasEquipment = false;

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                try {
                    JSONObject message = (JSONObject) msg.getContentObject();
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        if (message.get("propose").equals(CookerAgent.AGENT_TYPE)) {
                            if (!hasCooker) {
                                sendProposeMessage(msg.getSender());
                            }
                        }
                        if (message.get("propose").equals(EquipmentAgent.AGENT_TYPE)) {
                            if (!hasEquipment && message.get(EquipmentAgent.AGENT_TYPE).equals(card.getEquip_type())) {
                                System.out.println("PROPOSITION");
                                sendProposeMessage(msg.getSender());
                            }
                        }
                    }
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        if (message.get("propose").equals(CookerAgent.AGENT_TYPE)) {
                            if (hasCooker) {
                                //TODO deny
                            } else {
                                hasCooker = true;
                                System.out.println("Cooker was found");
                                // TODO confirm
                            }
                        }
                        if (message.get("propose").equals(EquipmentAgent.AGENT_TYPE)) {
                            if (hasEquipment) {
                                //TODO deny
                            } else {
                                hasEquipment = true;
                                System.out.println("Equipment was found");
                                // TODO confirm
                            }
                        }
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
