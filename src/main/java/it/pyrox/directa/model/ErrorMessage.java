package it.pyrox.directa.model;

import it.pyrox.directa.enums.ErrorEnum;

public class ErrorMessage extends Message {

    public static final String PREFIX = "ERR";

    private String ticker;

    private ErrorEnum error;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public ErrorEnum getError() {
        return error;
    }

    public void setError(ErrorEnum error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "type='" + type + '\'' +
                ", ticker='" + ticker + '\'' +
                ", error=" + error +
                '}';
    }
}
