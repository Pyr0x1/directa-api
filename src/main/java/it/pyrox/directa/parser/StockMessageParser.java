package it.pyrox.directa.parser;

import it.pyrox.directa.api.DirectaApi;
import it.pyrox.directa.enums.ApiEnum;
import it.pyrox.directa.model.StockMessage;

import java.util.Optional;
import java.util.StringTokenizer;

public class StockMessageParser implements MessageParser<StockMessage> {

    @Override
    public StockMessage parse(String messageLine) {
        StockMessage stockMessage = new StockMessage();
        StringTokenizer tokenizer = new StringTokenizer(messageLine, DirectaApi.DELIMITER);
        if (tokenizer.countTokens() != getTokenCount(stockMessage)) {
            throw new IllegalArgumentException("The message must contain " + getTokenCount(stockMessage) + " elements separated by " + DirectaApi.DELIMITER);
        }
        int tokenCounter = 0;
        while (tokenizer.hasMoreTokens()) {
            Optional<ApiEnum> apiEnum = Optional.empty();
            String token = tokenizer.nextToken();
            String trimmedToken = token.trim();
            switch (tokenCounter) {
                case 0:
                    stockMessage.setType(trimmedToken);
                    break;
                case 1:
                    stockMessage.setTicker(trimmedToken);
                    break;
                case 2:
                    stockMessage.setTime(trimmedToken);
                    break;
                case 3:
                    stockMessage.setPortfolioAmount(Integer.parseInt(trimmedToken));
                    break;
                case 4:
                    stockMessage.setBrokerAmount(trimmedToken);
                    break;
                case 5:
                    stockMessage.setTradingAmount(Integer.parseInt(trimmedToken));
                    break;
                case 6:
                    stockMessage.setAveragePrice(Double.parseDouble(trimmedToken));
                    break;
                case 7:
                    stockMessage.setGain(Double.parseDouble(trimmedToken));
                    break;
            }
            tokenCounter++;
        }
        return stockMessage;
    }

    @Override
    public int getTokenCount(StockMessage message) {
        return message.getClass().getDeclaredFields().length;
    }
}
