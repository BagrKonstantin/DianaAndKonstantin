package org.example;


import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;
import org.example.cooker.CookerAgent;
import org.example.customer.CustomerAgent;
import org.example.equipment.EquipmentAgent;
import org.example.manager.ManagerAgent;
import org.example.menu.MenuAgent;
import org.example.storage.Storage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {


    public static void main(String[] args) throws ControllerException, IOException, ParseException {
        System.out.println("Hello world!");



        AgentGenerator agentGenerator = new AgentGenerator();
        AgentGenerator.addAgent("Menu", MenuAgent.class.getName());
        AgentGenerator.addAgent("Michael Scott", ManagerAgent.class.getName());

        File file = new File(Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "input/visitors_orders.txt");

        JSONObject o = (JSONObject) new JSONParser().parse(new FileReader(file.getPath()));

        for (Object item : (JSONArray) o.get("visitors_orders")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("vis_name")) {
                AgentGenerator.addAgent((String) ((JSONObject) item).get("vis_name"), CustomerAgent.class.getName(), new  Object[] {item});
            }
        }
        // AgentGenerator.addAgent("Cumstomer", CustomerAgent.class.getName());
        // AgentGenerator.addAgent("Cumstomer2", CustomerAgent.class.getName());
//        AgentGenerator.addAgent("Cumstomer", CustomerAgent.class.getName());
//        AgentGenerator.addAgent("Cumstomer2", CustomerAgent.class.getName());



        AgentGenerator.addAgent("Cook1", CookerAgent.class.getName());
        AgentGenerator.addAgent("Cook2", CookerAgent.class.getName());

        AgentGenerator.addAgent("Equipment", EquipmentAgent.class.getName(), new Object[] {2L});
        AgentGenerator.addAgent("Equipment2", EquipmentAgent.class.getName(), new Object[] {25L});


    }


}