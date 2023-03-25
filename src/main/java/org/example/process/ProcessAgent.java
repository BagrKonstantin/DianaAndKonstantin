package org.example.process;

import jade.core.Agent;

public class ProcessAgent extends Agent {

    private static int processNumbers = 0;




    public static int getProcessNumbers() {
        return ++processNumbers;
    }
}
