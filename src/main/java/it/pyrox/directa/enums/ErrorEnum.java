package it.pyrox.directa.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ErrorEnum {

    ERR_UNKNOWN(0, "Generic error", null),
    ERR_MAX_SUBSCRIPTION_OVERFLOW(1000, "Reached the maximum number of subscribable securities", ApiEnum.DATAFEED),
    ERR_ALREADY_SUBSCRIBED(1001, "Requested security already subscribed", ApiEnum.DATAFEED),
    ERR_EMPTY_LIST(1002, "No security sent in the command", ApiEnum.DATAFEED),
    ERR_UNKNOWN_COMMAND(1003, "Unknown command", null),
    ERR_COMMAND_NOT_EXECUTED(1004, "Command not executed", null),
    ERR_NOT_SUBSCRIBED(1005, "Subscription error", null),
    ERR_DARWIN_STOP(1006, "Darwin closure in progress", null),
    ERR_BAD_SUBSCRIPTION(1007, "Non existing security error", null),
    ERR_DATA_UNAVAILABLE(1008, "Requested flow unavailable", ApiEnum.DATAFEED),
    ERR_TRADING_CMD_INCOMPLETE(1009, "Incomplete trading command", ApiEnum.TRADING),
    ERR_TRADING_CMD_ERROR(1010, "Wrong trading command", ApiEnum.TRADING),
    ERR_TRADING_UNAVAILABLE(1011, "Trading unavailable", ApiEnum.TRADING),
    ERR_TRADING_REQUEST_ERROR(1012, "Order entry error", ApiEnum.TRADING),
    ERR_HISTORICAL_PARAMS(1013, "Error number params inseto into command", ApiEnum.HISTORICAL_DATA),
    ERR_HISTORICAL_RANGE_INTRADAY(1015, "Error range date for intraday request", ApiEnum.HISTORICAL_DATA),
    ERR_HISTORICAL_DAY_OR_RANGE(1016, "Days or range date errors", ApiEnum.HISTORICAL_DATA),
    ERR_EMPTY_STOCKLIST(1018, "No security in the portfolio", ApiEnum.TRADING),
    ERR_EMPTY_ORDERLIST(1019, "No order is present", ApiEnum.TRADING),
    ERR_DUPLICATED_ID(1020, "Duplicated order ID", ApiEnum.TRADING),
    ERR_INVALID_ORDER_STATE(1021, "Order status inconsistent with the required operation", ApiEnum.TRADING),
    ERR_TRADING_PUSH_DISCONNECTED(1024, "Trading channel disconnected", ApiEnum.TRADING),
    ERR_TRADING_PUSH_RECONNECTION_OK(1025, "Connection to the trading channel correctly reestablished", ApiEnum.TRADING),
    ERR_TRADING_PUSH_RELOAD(1026, "", ApiEnum.TRADING),
    ERR_DATAFEED_DISCONNECTED(1027, "Real-time quotes data feed disconnected", ApiEnum.DATAFEED),
    ERR_DATAFEED_RELOAD(1028, "Connection to the real-time quotes channel correctly reestablished", ApiEnum.DATAFEED),
    ERR_MARKET_UNAVAILABLE(1030, "The requested ticker is part of a market which quotes are not available for your account", ApiEnum.DATAFEED),
    SESSION_NOT_ACTIVE(1031, "Session token not active anymore, you need to restart your application back from the login. All previous connection will be dropped or will receive no data", ApiEnum.TRADING),
    DATAFEED_NOT_ENABLED(1032, "Real-time data feed not enabled, please verify if the paid subscription is (still) in place", ApiEnum.DATAFEED);

    private final int code;
    private final String description;
    private final ApiEnum api; // If it is null, it means the error can be returned by all apis

    ErrorEnum(int code, String description, ApiEnum api) {
        this.code = code;
        this.description = description;
        this.api = api;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public ApiEnum getApi() {
        return api;
    }

    public static Map<Integer, ErrorEnum> getErrorsByApi(ApiEnum apiEnum) {
        return Arrays.stream(ErrorEnum.values())
                     .filter(e -> e.getApi() == null || apiEnum.equals(e.getApi()))
                     .collect(Collectors.toUnmodifiableMap(e -> e.getCode(),
                                                           e -> e));
    }

    public static Optional<ErrorEnum> decode(int code) {
        return Arrays.stream(ErrorEnum.values())
                     .filter(e -> code == e.getCode())
                     .findFirst();
    }
}
