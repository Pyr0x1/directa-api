package it.pyrox.directa.model;

import java.util.ArrayList;
import java.util.List;

public class GetStocksInfoResponse {

    List<StockMessage> stockMessageList;

    List<OrderMessage> orderMessageList;

    List<ErrorMessage> errorMessageList;

    public GetStocksInfoResponse() {
        this.stockMessageList = new ArrayList<>();
        this.orderMessageList = new ArrayList<>();
        this.errorMessageList = new ArrayList<>();
    }

    public List<StockMessage> getStockMessageList() {
        return stockMessageList;
    }

    public void setStockMessageList(List<StockMessage> stockMessageList) {
        this.stockMessageList = stockMessageList;
    }

    public List<OrderMessage> getOrderMessageList() {
        return orderMessageList;
    }

    public void setOrderMessageList(List<OrderMessage> orderMessageList) {
        this.orderMessageList = orderMessageList;
    }

    public List<ErrorMessage> getErrorMessageList() {
        return errorMessageList;
    }

    public void setErrorMessageList(List<ErrorMessage> errorMessageList) {
        this.errorMessageList = errorMessageList;
    }

    @Override
    public String toString() {
        return "GetStocksInfoResponse{" +
                "stockMessageList=" + stockMessageList +
                ", orderMessageList=" + orderMessageList +
                ", errorMessageList=" + errorMessageList +
                '}';
    }
}
