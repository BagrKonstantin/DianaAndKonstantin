package org.example;

import org.example.temporal_agent.Controller;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws StaleProxyException, IOException, ParseException {
        System.out.println("Hello world!");
        Controller controller = new Controller();

        final Runtime rt = Runtime.instance();
        final Profile p = new ProfileImpl();

        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "8080");
        p.setParameter(Profile.GUI, "true");

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



        ContainerController containerController = rt.createMainContainer(p);

        containerController.createNewAgent(
                "hahah",
                Controller.class.getName(),
                new String[]{"Тест"}).start();
    }


}