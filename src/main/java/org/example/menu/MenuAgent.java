package org.example.menu;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashSet;

public class MenuAgent extends Agent {

    HashSet<MenuItem> menu;
    protected void setup() {

        menu = new HashSet<MenuItem>();
        menu.add(new MenuItem(0, 0, true));
        menu.add(new MenuItem(1, 2, true));
        menu.add(new MenuItem(2, 1, true));
        menu.add(new MenuItem(3, 3, true));

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("menu");
        sd.setName("Menu");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");

        this.addBehaviour(new Behaviour() {
            @Override
            public void action() {

            }

            @Override
            public boolean done() {
                return false;
            }
        });



    }


}
