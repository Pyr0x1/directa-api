package it.pyrox.directa.parser;

import it.pyrox.directa.api.DirectaApi;
import it.pyrox.directa.enums.ApiEnum;
import it.pyrox.directa.enums.ErrorEnum;
import it.pyrox.directa.enums.MessageTypeEnum;
import it.pyrox.directa.model.ErrorMessage;

import java.util.Optional;
import java.util.StringTokenizer;

public class ErrorMessageParser implements MessageParser {

    private static final int NUMBER_OF_TOKENS = 3;

    @Override
    public ErrorMessage parse(String messageLine) {
        ErrorMessage errorMessage = new ErrorMessage();
        StringTokenizer tokenizer = new StringTokenizer(messageLine, DirectaApi.DELIMITER_SEMICOLON);
        if (tokenizer.countTokens() != getTokenCount()) {
            throw new IllegalArgumentException("The message must contain " + getTokenCount() + " elements separated by " + DirectaApi.DELIMITER_SEMICOLON);
        }
        int tokenCounter = 0;
        while (tokenizer.hasMoreTokens()) {
            Optional<ApiEnum> apiEnum = Optional.empty();
            String token = tokenizer.nextToken();
            String trimmedToken = token.trim();
            switch (tokenCounter) {
                case 0:
                    Optional<MessageTypeEnum> optType = MessageTypeEnum.decode(trimmedToken);
                    errorMessage.setType(optType.orElse(null));
                    break;
                case 1:
                    errorMessage.setTicker(trimmedToken);
                    break;
                case 2:
                    Optional<ErrorEnum> optEnum = ErrorEnum.decode(Integer.parseInt(trimmedToken));
                    errorMessage.setError(optEnum.orElse(null));
                    break;
            }
            tokenCounter++;
        }
        return errorMessage;
    }

    @Override
    public int getTokenCount() {
        return NUMBER_OF_TOKENS;
    }
}
