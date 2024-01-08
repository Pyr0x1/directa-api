package it.pyrox.directa.parser;

import java.util.Optional;

public interface MessageParser<T> {

    public int getTokenCount(T message);

    public T parse(String messageLine);
}
