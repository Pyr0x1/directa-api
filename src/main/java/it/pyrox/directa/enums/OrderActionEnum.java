package it.pyrox.directa.enums;

import java.util.Arrays;
import java.util.Optional;

public enum OrderActionEnum {

    ACQAZ("Buy stocks at the specified limit price"),
    VENAZ("Sell stocks at the specified limit price"),
    ACQMARKET("Buy stocks at the market price"),
    VENMARKET("Sell stocks at the market price"),
    ACQSTOP("Buy stocks with stop market price"),
    VENSTOP("Sell stocks with stop market price"),
    ACQSTOPLIMIT("Buy stocks with limit price and trigger price"),
    VENSTOPLIMIT("Sell stocks with limit price and trigger price"),
    REVORD("Cancel the specified order"),
    REVALL("Cancel all the orders for the specified ticker"),
    CONFORD("Confirm the specified order"),
    MODORD("Change price for the specified order");

    private final String description;

    OrderActionEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<OrderActionEnum> decode(String action) {
        Optional<OrderActionEnum> optEnum;
        optEnum = Arrays.stream(OrderActionEnum.values())
                        .filter(e -> e.name().equals(action))
                        .findFirst();
        return optEnum;
    }
}
