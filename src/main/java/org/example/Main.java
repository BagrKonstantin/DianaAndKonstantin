package org.example;


import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;
import org.example.customer.CustomerAgent;
import org.example.manager.ManagerAgent;
import org.example.menu.MenuAgent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws ControllerException, IOException, ParseException {
        System.out.println("Hello world!");
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
        AgentGenerator.addAgent("Michael Scott", ManagerAgent.class.getName());
        AgentGenerator.addAgent("Cumstomer", CustomerAgent.class.getName());
        AgentGenerator.addAgent("Cumstomer2", CustomerAgent.class.getName());
    }


}