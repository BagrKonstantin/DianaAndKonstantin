package org.example.customer;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.manager.ManagerAgent;
import org.example.menu.MenuItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CustomerAgent extends Agent {

    String vis_name;
    LocalDateTime vis_ord_started;
    LocalDateTime vis_ord_ended;
    Long vis_ord_total;
    List<CusOrdDish> vis_ord_dishes;

    public static final String AGENT_TYPE = "customer";
    HashMap<Long, MenuItem> menu;


    private AID managerId;

    private void findManager() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(ManagerAgent.AGENT_TYPE);
        template.addServices(sd);
        DFAgentDescription[] result;
        try {
            result = DFService.search(this, template);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        managerId = result[0].getName();
    }

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AGENT_TYPE);
        sd.setName("Customer who came to a restaurant");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
            Object[] args = getArguments();
            this.vis_name = (String) ((JSONObject) args[0]).get("vis_name");
            this.vis_ord_started =  LocalDateTime.parse((String) ((JSONObject) args[0]).get("vis_ord_started"));
            this.vis_ord_ended = LocalDateTime.parse((String) ((JSONObject) args[0]).get("vis_ord_ended"));
            this.vis_ord_total = (Long) ((JSONObject) args[0]).get("vis_ord_total");
            vis_ord_dishes = new ArrayList<>();
            for (Object item : (JSONArray) ((JSONObject) args[0]).get("vis_ord_dishes")) {
                vis_ord_dishes.add(new CusOrdDish((JSONObject)item));
            }
        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");
        findManager();
        // addBehaviour(new MakeOrder());


//        addBehaviour(new OneShotBehaviour() {
//            @Override
//            public void action() {
//                ACLMessage msg = myAgent.receive();
//                if (msg != null) {
//                    String cell_id = msg.getContent();
//                    ACLMessage reply = msg.createReply();
//                    try {
//                        reply.setContent("Collected order at cell: " + cell_id);
//                        myAgent.send(reply);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                //myAgent.doDelete();
//            }
//        });

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                //HashSet<MenuItem> items = menu.getMenu();
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(managerId);
                try {
                    JSONObject message = new JSONObject();
                    message.put("request", "menu");
                    msg.setContentObject(message);
                    myAgent.send(msg);
                    System.out.println("Cumstomer asked for menu");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        addBehaviour(new Behaviour() {

            private boolean receivedMenu = false;

            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();

                if (msg != null) {
                    System.out.println("GOT");

                    try {
                        JSONObject message = (JSONObject) msg.getContentObject();
                        menu = (HashMap<Long, MenuItem>) message.get("menu");

                        System.out.println(message);
                        receivedMenu = true;


                        myAgent.addBehaviour(new MakeOrder());

                    } catch (UnreadableException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public boolean done() {
                return receivedMenu;
            }
        });


    }


    @Override
    protected void takeDown() {
        System.out.println(getAID().getLocalName() + " is shutting down");
        super.takeDown();
    }


    private class MakeOrder extends OneShotBehaviour {
        @Override
        public void action() {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(managerId);
            try {
                JSONObject message = new JSONObject();
                message.put("request", "order");
                JSONArray jsonArray = new JSONArray();
                for (var item: vis_ord_dishes) {
                    for (var key : menu.values()) {
                        if (key.getId().equals(item.getOrd_dish_id())) {
                            jsonArray.add(item.getOrd_dish_id());
                        }
                    }
                }
                System.out.println(jsonArray);
                message.put("order", jsonArray);
                msg.setContentObject(message);
                myAgent.send(msg);
                System.out.println("Cumstomer asked food");
                myAgent.addBehaviour(new WaitForOrder());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }


    private class WaitForOrder extends Behaviour {

        private boolean receivedOrder = false;

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                try {
                    JSONObject message = (JSONObject) msg.getContentObject();
                    System.out.println(message);
                } catch (UnreadableException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        @Override
        public boolean done() {
            return receivedOrder;
        }
    }


}
