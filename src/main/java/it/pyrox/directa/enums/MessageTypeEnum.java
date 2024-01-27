package it.pyrox.directa.enums;

import java.util.Arrays;
import java.util.Optional;

public enum MessageTypeEnum {

    INFOACCOUNT(""),
    AVAILABILITY(""),
    ERR(""),
    ORDER(""),
    DARWIN_STATUS(""),
    STOCK(""),
    TRADOK("Positive outcome of the operation (PLACEMENT, EXECUTION, CANCELLATION)"),
    TRADERR("Negative outcome"),
    TRADCONFIRM("Needed order confirmation from the client");

    private final String description;

    MessageTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<MessageTypeEnum> decode(String type) {
        Optional<MessageTypeEnum> optEnum;
        optEnum = Arrays.stream(MessageTypeEnum.values())
                .filter(e -> e.name().equals(type))
                .findFirst();
        return optEnum;
    }
}
