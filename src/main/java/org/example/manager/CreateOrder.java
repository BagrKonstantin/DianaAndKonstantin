package org.example.manager;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.ContainerID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

public class CreateOrder extends OneShotBehaviour {

    static int num = 1;

    @Override
    public void action() {
        CreateAgent ca = new CreateAgent();
        ca.setAgentName("Order" + num);
        ++num;
        ca.setClassName(org.example.order.OrderAgent.class.getName());
        ca.setContainer(new ContainerID("Main-Container", null));
        Action actExpr = new Action(myAgent.getAMS(), ca);
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(myAgent.getAMS());
        request.setOntology(JADEManagementOntology.getInstance().getName());

        myAgent.getContentManager().registerLanguage(new SLCodec(),
                FIPANames.ContentLanguage.FIPA_SL);

        myAgent.getContentManager().registerOntology(JADEManagementOntology.getInstance());

        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        try {
            myAgent.getContentManager().fillContent(request, actExpr);
            myAgent.addBehaviour(new AchieveREInitiator(myAgent, request) {
                protected void handleInform(ACLMessage inform) {
                    System.out.println("Agent successfully created");
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
}
