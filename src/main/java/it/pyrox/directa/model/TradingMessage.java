package it.pyrox.directa.model;

import it.pyrox.directa.enums.ErrorEnum;
import it.pyrox.directa.enums.OrderActionEnum;
import it.pyrox.directa.enums.TradingMessageCodeEnum;

public class TradingMessage extends Message {

    private String ticker;
    private String orderId;
    private TradingMessageCodeEnum code;
    private ErrorEnum ErrorCode;
    private OrderActionEnum sentCommand;
    private int amount;
    private double price;
    private String errorDescription;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public TradingMessageCodeEnum getCode() {
        return code;
    }

    public void setCode(TradingMessageCodeEnum code) {
        this.code = code;
    }

    public ErrorEnum getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(ErrorEnum errorCode) {
        ErrorCode = errorCode;
    }

    public OrderActionEnum getSentCommand() {
        return sentCommand;
    }

    public void setSentCommand(OrderActionEnum sentCommand) {
        this.sentCommand = sentCommand;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        return "TradingMessage{" +
                "type=" + type +
                ", ticker='" + ticker + '\'' +
                ", orderId='" + orderId + '\'' +
                ", code=" + code +
                ", sentCommand=" + sentCommand +
                ", amount=" + amount +
                ", price=" + price +
                ", errorDescription='" + errorDescription + '\'' +
                '}';
    }
}
