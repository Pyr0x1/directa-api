package it.pyrox.directa.model;

import it.pyrox.directa.enums.MessageTypeEnum;

public class Message {

    protected MessageTypeEnum type;

    public MessageTypeEnum getType() {
        return type;
    }

    public void setType(MessageTypeEnum type) {
        this.type = type;
    }
}
