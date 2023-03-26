package org.example.menu;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.example.storage.Product;
import org.example.storage.Storage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class MenuAgent extends Agent {
    public static Map<Long, MenuItem> menu;
    public static Map<Long, Card> cards;

    AID storage;


    public MenuAgent() throws IOException, ParseException {
        menu = new HashMap<>();
        cards = new HashMap<>();
        File file = new File(Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "input/menu_dishes.txt");
        File file2 = new File(Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "input/dish_cards.txt");


        JSONObject o = (JSONObject) new JSONParser().parse(new FileReader(file.getPath()));
        JSONObject j = (JSONObject) new JSONParser().parse(new FileReader(file2.getPath()));
        for (Object item : (JSONArray) o.get("menu_dishes")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("menu_dish_id")) {
                Long id = (Long) ((JSONObject) item).get("menu_dish_id");
                menu.put(id, new MenuItem((JSONObject) item));
            }
        }

        for (Object item : (JSONArray) j.get("dish_cards")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("card_id")) {
                Long id = (Long) ((JSONObject) item).get("card_id");
                cards.put(id, new Card((JSONObject) item));
            }
        }
    }

    public void findStorage() {
        //menuAgent = this.getArguments()[0];
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(Storage.AGENT_TYPE);
        template.addServices(sd);
        DFAgentDescription[] result;
        try {
            result = DFService.search(this, template);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        storage = result[0].getName();


    }

    public static final String AGENT_TYPE = "menu";

    Queue<AID> requests;

    protected void setup() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(AGENT_TYPE);
        sd.setName("Have all information about dishes");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        requests = new LinkedList<>();
        findStorage();

        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    try {
                        JSONObject json = (JSONObject) msg.getContentObject();
                        System.out.println("Menu recieved: " + json);

                        if (msg.getPerformative() == ACLMessage.REQUEST) {
                            Logger.getGlobal().info(myAgent.getAID().getLocalName() + " request new menu from storage");

                            requests.add(msg.getSender());
                            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                            aclMessage.addReceiver(storage);
                            send(aclMessage);
                        }


                        if (msg.getPerformative() == ACLMessage.CONFIRM) {
                            Logger.getGlobal().info(myAgent.getAID().getLocalName() + " sent new menu to manager");

                            ACLMessage aclMessage = new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage.addReceiver(requests.poll());
                            System.out.println(json);
                            aclMessage.setContentObject(json);
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


}
