package it.pyrox.directa.parser;

import it.pyrox.directa.api.DirectaApi;
import it.pyrox.directa.model.TableMessage;

import java.util.StringTokenizer;

public class TableMessageParser {

    private static final int NUMBER_OF_TOKENS = 2;

    public TableMessage parse(String messageLine) {
        TableMessage tableMessage = new TableMessage();
        StringTokenizer tokenizer = new StringTokenizer(messageLine, DirectaApi.DELIMITER);
        if (tokenizer.countTokens() != getTokenCount()) {
            throw new IllegalArgumentException("The message must contain " + getTokenCount() + " elements separated by " + DirectaApi.DELIMITER);
        }
        int tokenCounter = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String trimmedToken = token.trim();
            switch (tokenCounter) {
                case 0:
                    tableMessage.setCode(trimmedToken);
                    break;
                case 1:
                    tableMessage.setDescription(trimmedToken);
                    break;
            }
            tokenCounter++;
        }
        return tableMessage;
    }

    public int getTokenCount() {
        return NUMBER_OF_TOKENS;
    }
}
