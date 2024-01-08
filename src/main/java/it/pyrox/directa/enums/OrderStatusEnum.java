package it.pyrox.directa.enums;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatusEnum {

    IN_NEGOTIATION(2000),
    ENTRY_ERROR(2001),
    TRADING_AFTER_VALIDATION_RECEIVED(2002),
    FILLED(2003),
    CANCELLED(2004),
    WAITING_FOR_VALIDATION(2005);

    private final int code;

    OrderStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Optional<OrderStatusEnum> decode(int code) {
        Optional<OrderStatusEnum> optEnum = Optional.empty();
        optEnum = Arrays.stream(OrderStatusEnum.values())
                        .filter(e -> code == e.getCode())
                        .findFirst();
        return optEnum;
    }
}
