package it.pyrox.directa.parser;

import it.pyrox.directa.api.DirectaApi;
import it.pyrox.directa.enums.ApiEnum;
import it.pyrox.directa.model.AvailabilityMessage;

import java.util.Optional;
import java.util.StringTokenizer;

public class AvailabilityMessageParser implements MessageParser {

    private static final int NUMBER_OF_TOKENS = 7;

    @Override
    public AvailabilityMessage parse(String messageLine) {
        AvailabilityMessage availabilityMessage = new AvailabilityMessage();
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
                    availabilityMessage.setType(trimmedToken);
                    break;
                case 1:
                    availabilityMessage.setTime(trimmedToken);
                    break;
                case 2:
                    availabilityMessage.setStocksAvailability(Integer.parseInt(trimmedToken));
                    break;
                case 3:
                    availabilityMessage.setStocksAvailabilityWithLeverage(Integer.parseInt(trimmedToken));
                    break;
                case 4:
                    availabilityMessage.setDerivativesAvailability(Integer.parseInt(trimmedToken));
                    break;
                case 5:
                    availabilityMessage.setDerivativesAvailabilityWithLeverage(Integer.parseInt(trimmedToken));
                    break;
                case 6:
                    availabilityMessage.setTotalLiquidity(Double.parseDouble(trimmedToken));
                    break;
            }
            tokenCounter++;
        }
        return availabilityMessage;
    }

    @Override
    public int getTokenCount() {
        return NUMBER_OF_TOKENS;
    }
}
