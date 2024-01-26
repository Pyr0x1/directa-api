package it.pyrox.directa.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TradingMessageCodeEnum {

    REQUEST_RECEIVED(3000, "Request correctly received"),
    REQUEST_EXECUTED(3001, "Request executed"),
    REQUEST_CANCELLED(3002, "Request cancelled"),
    ORDER_CONFIRMATION_NEEDED(3003, "Order confirmation needed");

    private final int code;
    private final String meaning;

    TradingMessageCodeEnum(int code, String meaning) {
        this.code = code;
        this.meaning = meaning;
    }

    public int getCode() {
        return code;
    }

    public String getMeaning() {
        return meaning;
    }

    public static Optional<TradingMessageCodeEnum> decode(int code) {
        Optional<TradingMessageCodeEnum> optEnum;
        optEnum = Arrays.stream(TradingMessageCodeEnum.values())
                .filter(e -> code == e.getCode())
                .findFirst();
        return optEnum;
    }
}
