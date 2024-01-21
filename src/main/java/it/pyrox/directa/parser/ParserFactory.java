package it.pyrox.directa.parser;

import it.pyrox.directa.model.*;

public class ParserFactory {

    public static MessageParser create(String messageLine) {
        MessageParser parser = null;
        if (messageLine == null || messageLine.isEmpty()) {
            return parser;
        }
        if (messageLine.startsWith(AvailabilityMessage.PREFIX)) {
            parser = new AvailabilityMessageParser();
        }
        else if (messageLine.startsWith(ErrorMessage.PREFIX)) {
            parser = new ErrorMessageParser();
        }
        else if (messageLine.startsWith(OrderMessage.PREFIX)) {
            parser = new OrderMessageParser();
        }
        else if (messageLine.startsWith(StatusMessage.PREFIX)) {
            parser = new StatusMessageParser();
        }
        else if (messageLine.startsWith(StockMessage.PREFIX)) {
            parser = new StockMessageParser();
        }
        else if (messageLine.startsWith(AccountInfoMessage.PREFIX)) {
            parser = new AccountInfoMessageParser();
        }

        return parser;
    }
}
