package it.pyrox.directa.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TradingMessageTypeEnum {

    TRADOK("Positive outcome of the operation (PLACEMENT, EXECUTION, CANCELLATION)"),
    TRADERR("Negative outcome"),
    TRADCONFIRM("Needed order confirmation from the client");

    private final String description;

    TradingMessageTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<TradingMessageTypeEnum> decode(String type) {
        Optional<TradingMessageTypeEnum> optEnum;
        optEnum = Arrays.stream(TradingMessageTypeEnum.values())
                .filter(e -> e.name().equals(type))
                .findFirst();
        return optEnum;
    }
}
