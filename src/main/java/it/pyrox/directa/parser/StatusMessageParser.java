package it.pyrox.directa.parser;

import it.pyrox.directa.api.DirectaApi;
import it.pyrox.directa.enums.ApiEnum;
import it.pyrox.directa.enums.ConnectionStatusEnum;
import it.pyrox.directa.model.StatusMessage;

import java.util.Optional;
import java.util.StringTokenizer;

public class StatusMessageParser implements MessageParser<StatusMessage> {

    @Override
    public StatusMessage parse(String messageLine) {
        StatusMessage statusMessage = new StatusMessage();
        StringTokenizer tokenizer = new StringTokenizer(messageLine, DirectaApi.DELIMITER);
        if (tokenizer.countTokens() != getTokenCount(statusMessage)) {
            throw new IllegalArgumentException("The message must contain " + getTokenCount(statusMessage) + " elements separated by " + DirectaApi.DELIMITER);
        }
        int tokenCounter = 0;
        while (tokenizer.hasMoreTokens()) {
            Optional<ApiEnum> apiEnum = Optional.empty();
            String token = tokenizer.nextToken();
            String trimmedToken = token.trim();
            switch (tokenCounter) {
                case 0:
                    statusMessage.setType(trimmedToken);
                    break;
                case 1:
                    Optional<ConnectionStatusEnum> optEnum = ConnectionStatusEnum.decode(trimmedToken);
                    statusMessage.setConnectionStatus(optEnum.orElse(null));
                    break;
                case 2:
                    statusMessage.setDatafeedEnabled("TRUE".equals(trimmedToken));
                    break;
                case 3:
                    statusMessage.setRelease(trimmedToken);
                    break;
            }
            tokenCounter++;
        }
        return statusMessage;
    }

    @Override
    public int getTokenCount(StatusMessage message) {
        return message.getClass().getDeclaredFields().length;
    }
}
