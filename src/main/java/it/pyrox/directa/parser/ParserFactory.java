package it.pyrox.directa.parser;

import it.pyrox.directa.enums.MessageTypeEnum;

public class ParserFactory {

    public static MessageParser create(String messageLine) {
        MessageParser parser = null;
        if (messageLine == null || messageLine.isEmpty()) {
            return parser;
        }
        if (messageLine.startsWith(MessageTypeEnum.AVAILABILITY.name())) {
            parser = new AvailabilityMessageParser();
        }
        else if (messageLine.startsWith(MessageTypeEnum.ERR.name())) {
            parser = new ErrorMessageParser();
        }
        else if (messageLine.startsWith(MessageTypeEnum.ORDER.name())) {
            parser = new OrderMessageParser();
        }
        else if (messageLine.startsWith(MessageTypeEnum.DARWIN_STATUS.name())) {
            parser = new StatusMessageParser();
        }
        else if (messageLine.startsWith(MessageTypeEnum.STOCK.name())) {
            parser = new StockMessageParser();
        }
        else if (messageLine.startsWith(MessageTypeEnum.INFOACCOUNT.name())) {
            parser = new AccountInfoMessageParser();
        }
        else if (messageLine.startsWith(MessageTypeEnum.TRADOK.name()) ||
                 messageLine.startsWith(MessageTypeEnum.TRADERR.name()) ||
                 messageLine.startsWith(MessageTypeEnum.TRADCONFIRM.name())) {
            parser = new TradingMessageParser();
        }

        return parser;
    }
}
