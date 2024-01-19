package it.pyrox.directa.model;

import it.pyrox.directa.enums.ConnectionStatusEnum;

public class StatusMessage extends Message {

    public static final String PREFIX = "DARWIN_STATUS";

    private ConnectionStatusEnum connectionStatus;

    private boolean isDatafeedEnabled;

    private String release;

    public ConnectionStatusEnum getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatusEnum connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public boolean isDatafeedEnabled() {
        return isDatafeedEnabled;
    }

    public void setDatafeedEnabled(boolean datafeedEnabled) {
        isDatafeedEnabled = datafeedEnabled;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                "type='" + type + '\'' +
                ", connectionStatus=" + connectionStatus +
                ", isDatafeedEnabled=" + isDatafeedEnabled +
                ", release='" + release + '\'' +
                '}';
    }
}
