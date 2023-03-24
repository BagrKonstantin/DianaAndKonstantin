package org.example.message;

import jade.content.AgentAction;

public class Message implements AgentAction {
    public String localName;
    public String content;

    public Message(String localName, String content) {
        this.localName = localName;
        this.content = content;
    }
}
