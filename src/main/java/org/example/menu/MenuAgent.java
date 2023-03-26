package org.example.menu;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MenuAgent extends Agent {
    public static Map<Long, MenuItem> menu;
    public static Map<Long, Card> cards;
    public MenuAgent() throws IOException, ParseException {
        menu = new HashMap<>();
        cards = new HashMap<>();
        File file = new File(Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "input/menu_dishes.txt");
        File file2 = new File(Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "input/dish_cards.txt");

        System.out.println(file.getPath());
        JSONObject o = (JSONObject) new JSONParser().parse(new FileReader(file.getPath()));
        JSONObject j = (JSONObject) new JSONParser().parse(new FileReader(file2.getPath()));
        for (Object item : (JSONArray) o.get("menu_dishes")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("menu_dish_id")) {
                Long id = (Long) ((JSONObject) item).get("menu_dish_id");
                menu.put(id, new MenuItem((JSONObject) item));
                System.out.println("get");
            }
        }

        for (Object item : (JSONArray) j.get("dish_cards")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("card_id")) {
                Long id = (Long) ((JSONObject) item).get("card_id");
                cards.put(id, new Card((JSONObject) item));
                System.out.println("get");
            }
        }
    }

    public static final String AGENT_TYPE = "menu";

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

        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    try {
                        JSONObject json = (JSONObject) msg.getContentObject();


                        System.out.println("Menu recieved: " + json);

                        ACLMessage aclMessage = new ACLMessage(ACLMessage.CONFIRM);
                        aclMessage.addReceiver(msg.getSender());
                        JSONObject message = new JSONObject();
                        message.put("menu", menu);

                        aclMessage.setContentObject(message);
                        myAgent.send(aclMessage);
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
