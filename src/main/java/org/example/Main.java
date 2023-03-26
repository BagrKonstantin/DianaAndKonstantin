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
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {


    public static void main(String[] args) throws ControllerException, IOException, ParseException {

        String path = Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(path + "logging.properties");
        LogManager.getLogManager().readConfiguration(file.toURL().openStream());

        Logger.getGlobal().info("HAHAHAHAHAHA");


        // Всё на соплях, но вроде даже работает:З

//        Controller controller = new Controller();



        //JSONObject cookieJO = new JSONObject();

//        JSONParser parser = new JSONParser();
//
//
//        JSONArray a = (JSONArray) parser.parse(new FileReader("c:\\exer4-courses.json"));
//
//        for (Object o : a)
//        {
//            JSONObject person = (JSONObject) o;
//
//            String name = (String) person.get("name");
//            System.out.println(name);
//
//            String city = (String) person.get("city");
//            System.out.println(city);
//
//            String job = (String) person.get("job");
//            System.out.println(job);
//
//            JSONArray cars = (JSONArray) person.get("cars");
//
//            for (Object c : cars)
//            {
//                System.out.println(c+"");
//            }
//        }





//        containerController.createNewAgent(
//                "Menu",
//                MenuAgent.class.getName(),
//                new String[]{}).start();
//
//        containerController.createNewAgent(
//                "Michael Scott",
//                ManagerAgent.class.getName(),
//                new String[]{}).start();
//
//        containerController.createNewAgent(
//                "Cumstomer",
//                CustomerAgent.class.getName(),
//                new String[]{}).start();

        AgentGenerator agentGenerator = new AgentGenerator();
        AgentGenerator.addAgent("Menu", MenuAgent.class.getName());
        AgentGenerator.addAgent("Storage", Storage.class.getName());
        AgentGenerator.addAgent("Michael Scott", ManagerAgent.class.getName());


        File f = new File(path + "input/visitors_orders.txt");

        JSONObject o = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

        for (Object item : (JSONArray) o.get("visitors_orders")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("vis_name")) {
                AgentGenerator.addAgent((String) ((JSONObject) item).get("vis_name"), CustomerAgent.class.getName(), new  Object[] {item});
            }
        }

        File file1 = new File(path + "input/equipment.txt");

        JSONObject j = (JSONObject) new JSONParser().parse(new FileReader(file1.getPath()));

        for (Object item : (JSONArray) j.get("equipment")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("equip_type")) {
                AgentGenerator.addAgent((String) ((JSONObject) item).get("equip_name"), EquipmentAgent.class.getName(), new  Object[] {item});
            }
        }
        // AgentGenerator.addAgent("Cumstomer", CustomerAgent.class.getName());
        // AgentGenerator.addAgent("Cumstomer2", CustomerAgent.class.getName());
        //AgentGenerator.addAgent("Cumstomer", CustomerAgent.class.getName());
        // AgentGenerator.addAgent("Cumstomer2", CustomerAgent.class.getName());

        File file2 = new File(path + "input/cookers.txt");

        JSONObject k = (JSONObject) new JSONParser().parse(new FileReader(file2.getPath()));

        for (Object item : (JSONArray) k.get("cookers")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("cook_id")) {
                AgentGenerator.addAgent((String) ((JSONObject) item).get("cook_name"), CookerAgent.class.getName(), new  Object[] {item});
            }
        }

        // AgentGenerator.addAgent("Cook1", CookerAgent.class.getName());
        // AgentGenerator.addAgent("Cook2", CookerAgent.class.getName());

        // AgentGenerator.addAgent("Equipment", EquipmentAgent.class.getName(), new Object[] {2L});
        // AgentGenerator.addAgent("Equipment2", EquipmentAgent.class.getName(), new Object[] {25L});


    }


}