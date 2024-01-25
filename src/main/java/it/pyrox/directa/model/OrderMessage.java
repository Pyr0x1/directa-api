package it.pyrox.directa.model;

import it.pyrox.directa.enums.OrderActionEnum;
import it.pyrox.directa.enums.OrderStatusEnum;

public class OrderMessage extends Message {
    public static final String PREFIX = "ORDER";
    private String ticker;
    private String time;
    private String orderId;
    private OrderActionEnum operationType;
    private double limitPrice;
    private double triggerPrice;
    private int amount;
    private OrderStatusEnum orderStatus;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderActionEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(OrderActionEnum operationType) {
        this.operationType = operationType;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public OrderStatusEnum getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "OrderMessage{" +
                "type='" + type + '\'' +
                ", ticker='" + ticker + '\'' +
                ", time='" + time + '\'' +
                ", orderId='" + orderId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", limitPrice=" + limitPrice +
                ", triggerPrice=" + triggerPrice +
                ", amount=" + amount +
                ", orderStatus=" + orderStatus +
                '}';
    }
}
