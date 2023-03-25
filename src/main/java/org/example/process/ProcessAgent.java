package org.example.process;

import jade.core.AID;
import jade.core.Agent;
import org.json.simple.JSONObject;

public class ProcessAgent extends Agent {

    private static int processNumbers = 0;

    private JSONObject dish;

    protected void setup() {
        System.out.println("Process started");
        Object[] args = getArguments();
        dish = (JSONObject) args[0];
        System.out.println(dish);
    }


    public static int getProcessNumbers() {
        return ++processNumbers;
    }
}
