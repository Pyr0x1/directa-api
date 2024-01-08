package it.pyrox.directa.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ConnectionStatusEnum {
    CONN_UNAVAILABLE,
    CONN_SLOW,
    CONN_OK,
    CONN_TROUBLE;

    public static Optional<ConnectionStatusEnum> decode(String value) {
        Optional<ConnectionStatusEnum> optEnum = Optional.empty();
        Optional<String> optValue = Optional.ofNullable(value);
        if (optValue.isPresent()) {
            optEnum = Arrays.stream(ConnectionStatusEnum.values())
                            .filter(e -> e.name().equals(optValue.get()))
                            .findFirst();

        }
        return optEnum;
    }
}
