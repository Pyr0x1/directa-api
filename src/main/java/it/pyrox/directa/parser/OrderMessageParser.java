package it.pyrox.directa.parser;

import it.pyrox.directa.api.DirectaApi;
import it.pyrox.directa.api.DirectaApiConnectionManager;
import it.pyrox.directa.enums.OrderActionEnum;
import it.pyrox.directa.enums.OrderStatusEnum;
import it.pyrox.directa.model.Message;
import it.pyrox.directa.model.OrderMessage;
import it.pyrox.directa.enums.ApiEnum;

import java.util.Optional;
import java.util.StringTokenizer;

public class OrderMessageParser implements MessageParser {

    private static final int NUMBER_OF_TOKENS = 9;

    @Override
    public OrderMessage parse(String messageLine) {
        OrderMessage orderMessage = new OrderMessage();
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
                    orderMessage.setType(trimmedToken);
                    break;
                case 1:
                    orderMessage.setTicker(trimmedToken);
                    break;
                case 2:
                    orderMessage.setTime(trimmedToken);
                    break;
                case 3:
                    orderMessage.setOrderId(trimmedToken);
                    break;
                case 4:
                    Optional<OrderActionEnum> optActionEnum = OrderActionEnum.decode(trimmedToken);
                    orderMessage.setOperationType(optActionEnum.orElse(null));
                    break;
                case 5:
                    orderMessage.setLimitPrice(Double.parseDouble(trimmedToken));
                    break;
                case 6:
                    orderMessage.setTriggerPrice(Double.parseDouble(trimmedToken));
                    break;
                case 7:
                    orderMessage.setAmount(Integer.parseInt(trimmedToken));
                    break;
                case 8:
                    Optional<OrderStatusEnum> optStatusEnum = OrderStatusEnum.decode(Integer.parseInt(trimmedToken));
                    orderMessage.setOrderStatus(optStatusEnum.orElse(null));
                    break;
            }
            tokenCounter++;
        }
        return orderMessage;
    }

    @Override
    public int getTokenCount() {
        return NUMBER_OF_TOKENS;
    }
}
