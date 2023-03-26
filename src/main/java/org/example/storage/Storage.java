package org.example.storage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.menu.MenuAgent;
import org.example.menu.MenuItem;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;

import org.json.simple.parser.*;


public class Storage extends Agent {
    public Map<Long, Product> storage;

    public static final String AGENT_TYPE = "storage";

    public Storage() throws IOException, ParseException {
        File file = new File(Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "input/products.txt");

        JSONObject o = (JSONObject) new JSONParser().parse(new FileReader(file.getPath()));

        storage = new HashMap<>();

        for (Object item : (JSONArray) o.get("products")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("prod_item_id")) {
                long id = (Long) ((JSONObject) item).get("prod_item_id");
                storage.put(id, new Product((JSONObject) item));
            }
        }

    }

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AGENT_TYPE);
        sd.setName("Have food");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    Logger.getGlobal().info(myAgent.getAID().getLocalName() + " received request for new menu");
                    try {
                        JSONObject json = (JSONObject) msg.getContentObject();
                        if (msg.getPerformative() == ACLMessage.REQUEST) {
                            ACLMessage aclMessage = new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage.addReceiver(msg.getSender());
                            JSONObject message = new JSONObject();

                            message.put("menu",  getUpdatedMenu());
                            System.out.println(message);
                            aclMessage.setContentObject(message);
                            myAgent.send(aclMessage);
                        }
                    } catch (UnreadableException e) {
                        System.out.println(e);
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        System.out.println(e);
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }

    public Map<Long, MenuItem> getUpdatedMenu() {
        Map<Long, MenuItem> newMenu = new HashMap<>();
        for (var item : MenuAgent.menu.values()) {
            var card = MenuAgent.cards.get(item.getMenu_dish_card());
            for(var operations : card.getOper().values()) {
                boolean flag = true;
                for(var operProds : operations.getOper_products()) {
                    for (var p : storage.values()) {
                        if (Objects.equals(operProds.getProd_type(), p.prod_item_type) && operProds.getProd_q() > p.prod_item_quantity) {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    newMenu.put(item.getId(), item);
                }
            }
        }
        return newMenu;
    }
}
