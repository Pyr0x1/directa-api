package it.pyrox.directa.parser;

import it.pyrox.directa.model.Message;

import java.util.Optional;

public interface MessageParser {

    public Message parse(String messageLine);

    public int getTokenCount();
}
