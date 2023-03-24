package org.example.order;

import jade.core.AID;
import jade.core.Agent;

public class OrderAgent extends Agent {
    AID customer_id;
    int cell_id = 0;
    String order;

    protected void setup() {
        System.out.println("Manager created new order " + this.getLocalName() + ", now it's ready to go!");
        addBehaviour(new Ready());
    }
}
