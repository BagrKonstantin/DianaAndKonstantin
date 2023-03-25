package org.example.menu;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashSet;

public class MenuAgent extends Agent {

    HashSet<MenuItem> menu;

    public static final String AGENT_TYPE = "menu";

    protected void setup() {

        menu = new HashSet<MenuItem>();
        menu.add(new MenuItem(0, 0, true));
        menu.add(new MenuItem(1, 2, true));
        menu.add(new MenuItem(2, 1, true));
        menu.add(new MenuItem(3, 3, true));

//        this.getContainerController().getName();

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
                        message.put("menu", new HashSet<>());

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
