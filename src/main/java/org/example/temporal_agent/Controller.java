package org.example.temporal_agent;


import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import org.example.customer.CustomerAgent;
import org.example.manager.ManagerAgent;

public class Controller extends Agent {
    public void addAgent(String name, String agent) {
        CreateAgent ca = new CreateAgent();
        ca.setAgentName(name);
        ca.setClassName(agent);
        ca.setContainer(new ContainerID("Main-Container", null));
        Action actExpr = new Action(getAMS(), ca);
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(getAMS());
        request.setOntology(JADEManagementOntology.getInstance().getName());

        getContentManager().registerLanguage(new SLCodec(),
                FIPANames.ContentLanguage.FIPA_SL);

        getContentManager().registerOntology(JADEManagementOntology.getInstance());

        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        try {
            getContentManager().fillContent(request, actExpr);
            addBehaviour(new AchieveREInitiator(this, request) {
                protected void handleInform(ACLMessage inform) {
//                    System.out.println("Agent successfully created");
                }

                protected void handleFailure(ACLMessage failure) {
                    System.out.println("Error creating agent.");
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setup() {

        System.out.println("Hello from " + getAID().getLocalName() + " agent, now it's ready to go!");
        addBehaviour(new TickerBehaviour(this, 3000) {
            int customer_id = 1;

            @Override
            protected void onTick() {
                ((Controller)myAgent).addAgent("Customer" + customer_id, CustomerAgent.class.getName());
                ++customer_id;
            }
        });
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ((Controller)myAgent).addAgent("Manager", ManagerAgent.class.getName());
            }
        });
    }
}
