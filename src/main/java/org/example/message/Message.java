package org.example.message;

import jade.content.AgentAction;
import jade.core.AID;

import java.lang.reflect.Type;

public class Message implements AgentAction {
    public String localName;
    public String content;

    public String type;

    public AID senderIid;

    public Message(String localName, String content, String type) {
        this.localName = localName;
        this.content = content;
        this.type = type;
    }
}
