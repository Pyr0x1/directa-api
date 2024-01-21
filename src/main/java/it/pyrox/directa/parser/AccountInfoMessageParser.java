package it.pyrox.directa.parser;

import it.pyrox.directa.api.DirectaApi;
import it.pyrox.directa.enums.ApiEnum;
import it.pyrox.directa.model.AccountInfoMessage;

import java.util.Optional;
import java.util.StringTokenizer;

public class AccountInfoMessageParser implements MessageParser {

    private static final int NUMBER_OF_TOKENS = 8;

    @Override
    public AccountInfoMessage parse(String messageLine) {
        AccountInfoMessage accountInfoMessage = new AccountInfoMessage();
        StringTokenizer tokenizer = new StringTokenizer(messageLine, DirectaApi.DELIMITER);
        if (tokenizer.countTokens() != getTokenCount()) {
            throw new IllegalArgumentException("The message must contain " + getTokenCount() + " elements separated by " + DirectaApi.DELIMITER);
        }
        int tokenCounter = 0;
        while (tokenizer.hasMoreTokens()) {
            Optional<ApiEnum> apiEnum = Optional.empty();
            String token = tokenizer.nextToken();
            String trimmedToken = token.trim();
            switch (tokenCounter) {
                case 0:
                    accountInfoMessage.setType(trimmedToken);
                    break;
                case 1:
                    accountInfoMessage.setTime(trimmedToken);
                    break;
                case 2:
                    accountInfoMessage.setAccountId(trimmedToken);
                    break;
                case 3:
                    accountInfoMessage.setLiquidity(Double.parseDouble(trimmedToken));
                    break;
                case 4:
                    accountInfoMessage.setGainEuro(Double.parseDouble(trimmedToken));
                    break;
                case 5:
                    accountInfoMessage.setOpenProfitLoss(Double.parseDouble(trimmedToken));
                    break;
                case 6:
                    accountInfoMessage.setEquity(Double.parseDouble(trimmedToken));
                    break;
                case 7:
                    accountInfoMessage.setEnvironment(trimmedToken);
                    break;
            }
            tokenCounter++;
        }
        return accountInfoMessage;
    }

    @Override
    public int getTokenCount() {
        return NUMBER_OF_TOKENS;
    }
}
