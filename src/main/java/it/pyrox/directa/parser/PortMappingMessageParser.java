package it.pyrox.directa.parser;

import it.pyrox.directa.api.DirectaApi;
import it.pyrox.directa.enums.ApiEnum;
import it.pyrox.directa.model.PortMappingMessage;

import java.util.Optional;
import java.util.StringTokenizer;

public class PortMappingMessageParser {

    private static final int NUMBER_OF_TOKENS = 4;

    public PortMappingMessage parse(String messageLine) {
        PortMappingMessage portMappingMessage = new PortMappingMessage();
        StringTokenizer tokenizer = new StringTokenizer(messageLine, DirectaApi.DELIMITER_SEMICOLON);
        if (tokenizer.countTokens() != getTokenCount(portMappingMessage)) {
            throw new IllegalArgumentException("The message must contain " + getTokenCount(portMappingMessage) + " elements separated by " + DirectaApi.DELIMITER_SEMICOLON);
        }
        int tokenCounter = 0;
        while (tokenizer.hasMoreTokens()) {
            Optional<ApiEnum> apiEnum = Optional.empty();
            String token = tokenizer.nextToken();
            String trimmedToken = token.trim();
            switch (tokenCounter) {
                case 0:
                    portMappingMessage.setAccountId(trimmedToken);
                    break;
                case 1:
                    portMappingMessage.setDatafeedPort(Integer.parseInt(trimmedToken));
                    break;
                case 2:
                    portMappingMessage.setTradingPort(Integer.parseInt(trimmedToken));
                    break;
                case 3:
                    portMappingMessage.setHistoricalDataPort(Integer.parseInt(trimmedToken));
                    break;
            }
            tokenCounter++;
        }
        return portMappingMessage;
    }

    public int getTokenCount(PortMappingMessage message) {
        return NUMBER_OF_TOKENS;
    }
}
