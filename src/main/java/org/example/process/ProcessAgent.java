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

import java.util.logging.Logger;

public class ProcessAgent extends Agent {

    public static final String AGENT_TYPE = "process";


    private static int processNumbers = 0;

    AID order;

    private Long dishCardId;

    private Card card;

    private DFAgentDescription dfd;
    private ServiceDescription sd;

    protected void setup() {
        dfd = new DFAgentDescription();
        dfd.setName(getAID());
        sd = new ServiceDescription();
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
        order = (AID) args[1];

        card = MenuAgent.cards.get(dishCardId);
        System.out.println(dishCardId);

        addBehaviour(new waitForFreeStuff());
    }


    class waitForFreeStuff extends Behaviour {
        boolean hasCooker = false;
        boolean hasEquipment = false;
        boolean started = false;

        AID equipment;
        AID cook;


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
                                sendProposeDenyMessage(msg.getSender());
                            } else {
                                hasCooker = true;
                                cook = msg.getSender();
                                System.out.println("Cooker was found");

                            }
                        }
                        if (message.get("propose").equals(EquipmentAgent.AGENT_TYPE)) {
                            if (hasEquipment) {
                                sendProposeDenyMessage(msg.getSender());
                            } else {
                                equipment = msg.getSender();
                                hasEquipment = true;
                                System.out.println("Equipment was found");
                            }
                        }
                        if (!started && hasCooker && hasEquipment) {
                            Logger.getGlobal().info(getAID().getLocalName() + " found cooker and equipment");

                            sendProposeConfirmMessage(cook);
                            sendProposeConfirmMessage(equipment);
                            started = true;
                        }
                    }
                    if (msg.getPerformative() == ACLMessage.INFORM) {
                        System.out.println(getAID().getLocalName() + " is ready!");
                        sendDoneMessage(equipment);
                        sendConfirmMessage(order, card.getCard_time());

                        try {
                            DFService.deregister(myAgent, dfd);
                        } catch (FIPAException fe) {
                            fe.printStackTrace();
                        }
                        Logger.getGlobal().info(getAID().getLocalName() + " was killed");

                        doDelete();
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

    @Override
    protected void takeDown() {
        System.out.println(getAID().getLocalName() + " is shutting down");

        super.takeDown();
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

    private void sendProposeConfirmMessage(AID aid) {
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msg.addReceiver(aid);
        try {
            JSONObject message = new JSONObject();
            message.put("propose", "confirm");
            message.put("card", dishCardId);
            msg.setContentObject(message);
            send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendProposeDenyMessage(AID aid) {
        ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
        msg.addReceiver(aid);
        try {
            JSONObject message = new JSONObject();
            message.put("propose", "rejected");
            msg.setContentObject(message);
            send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDoneMessage(AID aid) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(aid);
        try {
            JSONObject message = new JSONObject();
            message.put("inform", "finished");
            msg.setContentObject(message);
            send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void sendConfirmMessage(AID aid, double time) {
        ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
        msg.addReceiver(aid);
        try {
            JSONObject message = new JSONObject();
            message.put("confirm", "finished");
            message.put("time", time);
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
