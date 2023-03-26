package org.example.cooker;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.equipment.EquipmentAgent;
import org.example.manager.ManagerAgent;
import org.example.menu.Card;
import org.example.menu.MenuAgent;
import org.example.menu.Operations;
import org.example.order.OrderAgent;
import org.example.process.ProcessAgent;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class CookerAgent extends Agent {
    Long cook_id;
    String cook_name;
    boolean isBusy = false;
    Card card;


    public static final String AGENT_TYPE = "cooker";


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
        msg.setContentObject(message);

        send(msg);
    }

    protected void setup() {


        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AGENT_TYPE);
        sd.setName("Cooker who works in restaurant");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        Object[] args = getArguments();
        this.cook_id = (Long) ((JSONObject) args[0]).get("cook_id");
        //equipmentTypeId = (Long) args[0];
        this.cook_name = (String) ((JSONObject) args[0]).get("cook_name");
        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");

        addBehaviour(new TickerBehaviour(this, 1000) {

            @Override
            protected void onTick() {
                try {
                    if (!isBusy) {
                        ((CookerAgent)myAgent).notifyAllProcesses();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        addBehaviour(new waitForProposal());
    }

    class MyThread extends Thread {
        double seconds;
        MyThread(double seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((int)(seconds * 1000 * 60));
                System.out.println("THREAD FINISHED");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class waitForProposal extends Behaviour{
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                try {
                    JSONObject message = (JSONObject) msg.getContentObject();
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        Logger.getGlobal().info(myAgent.getAID().getLocalName() + " got propose to work");
                        if (message.get("propose").equals("work")) {
                            if (!isBusy) {
                                sendProposeConfirmMessage(msg.getSender());
                            }
                        }
//                        if (message.get("propose").equals(EquipmentAgent.AGENT_TYPE)) {
//                            if (message.get(EquipmentAgent.AGENT_TYPE) == card.getEquip_type()) {
//                                sendProposeMessage(msg.getSender());
//                            }
//                        }
                    }
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        Logger.getGlobal().info(myAgent.getAID().getLocalName() + " started to work");

                        isBusy = true;

                        card = MenuAgent.cards.get(message.get("card"));
                        System.out.println(card);
                        System.out.println("Cooker working");

                        //
                        List<Operations> operations = new ArrayList<>();
                        Set<Long> points = new HashSet<>();
                        points.add(0L);
                        operations.addAll(card.getOper().values());
                        for (var operation: operations) {
                            if (operation.getOper_async_point().equals(0L)) {
                                Thread.sleep((int)(operation.getOper_time() * 1000 * 60));
                            }
                        }
                        // асинхронщина
                        for (int i = 0; i < operations.size(); i++) {
                            List<MyThread> array = new ArrayList<>();
                            List<Double> times = new ArrayList<>();
                            for (int j = i + 1; j < operations.size(); j++) {
                                if (!points.contains(operations.get(i).getOper_async_point())) {
                                    if (operations.get(i).getOper_async_point().equals(operations.get(j).getOper_async_point())) {
                                        times.add(operations.get(j).getOper_time());
                                        System.out.println(i + " " + j);
                                    }
                                }
                            }
                            if (!times.isEmpty()) {
                                Logger.getGlobal().info(myAgent.getAID().getLocalName() + " found async operations and decided to cut some time");
                                times.add(operations.get(i).getOper_time());
                            }
                            points.add(operations.get(i).getOper_async_point());

                            for (double time: times) {
                                array.add(new MyThread(time));
                            }
                            for (MyThread thread: array) {
                                thread.start();

                            }
                            if (!times.isEmpty()) {
                                System.out.println("ASYNC STARTED");

                            }
                            for (MyThread thread: array) {
                                thread.join();
                            }

                        }

                        //System.out.println("BOTH FINISHED");


                        sendFinished(msg.getSender());
                        isBusy = false;
                        Logger.getGlobal().info(myAgent.getAID().getLocalName() + " finished work");

                    }
                    if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                        Logger.getGlobal().info(myAgent.getAID().getLocalName() + " got reject to work");
                        isBusy = false;
                    }
                } catch (UnreadableException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
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

    private void sendFinished(AID aid) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(aid);
        try {
            JSONObject message = new JSONObject();
            message.put("inform", AGENT_TYPE);
            msg.setContentObject(message);
            send(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
