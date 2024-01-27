package it.pyrox.directa.parser;

import it.pyrox.directa.api.DirectaApi;
import it.pyrox.directa.enums.OrderActionEnum;
import it.pyrox.directa.enums.TradingMessageCodeEnum;
import it.pyrox.directa.enums.MessageTypeEnum;
import it.pyrox.directa.model.TradingMessage;

import java.util.Optional;
import java.util.StringTokenizer;

public class TradingMessageParser implements MessageParser {

    private static final int NUMBER_OF_TOKENS = 8;

    @Override
    public TradingMessage parse(String messageLine) {
        TradingMessage tradingMessage = new TradingMessage();
        StringTokenizer tokenizer = new StringTokenizer(messageLine, DirectaApi.DELIMITER_SEMICOLON);
        if (tokenizer.countTokens() != getTokenCount()) {
            throw new IllegalArgumentException("The message must contain " + getTokenCount() + " elements separated by " + DirectaApi.DELIMITER_SEMICOLON);
        }
        int tokenCounter = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String trimmedToken = token.trim();
            switch (tokenCounter) {
                case 0:
                    Optional<MessageTypeEnum> optType = MessageTypeEnum.decode(trimmedToken);
                    tradingMessage.setType(optType.orElse(null));
                    break;
                case 1:
                    tradingMessage.setTicker(trimmedToken);
                    break;
                case 2:
                    tradingMessage.setOrderId(trimmedToken);
                    break;
                case 3:
                    Optional<TradingMessageCodeEnum> optionalCode = TradingMessageCodeEnum.decode(Integer.parseInt(trimmedToken));
                    tradingMessage.setCode(optionalCode.orElse(null));
                    break;
                case 4:
                    Optional<OrderActionEnum> optActionEnum = OrderActionEnum.decode(trimmedToken);
                    tradingMessage.setSentCommand(optActionEnum.orElse(null));
                    break;
                case 5:
                    tradingMessage.setAmount(Integer.parseInt(trimmedToken));
                    break;
                case 6:
                    tradingMessage.setPrice(Double.parseDouble(trimmedToken));
                    break;
                case 7:
                    tradingMessage.setErrorDescription(trimmedToken);
                    break;
            }
            tokenCounter++;
        }
        return tradingMessage;
    }

    @Override
    public int getTokenCount() {
        return NUMBER_OF_TOKENS;
    }
}
