package it.pyrox.directa.model;

import it.pyrox.directa.enums.ConnectionStatusEnum;

public class StatusMessage {

    private String type;

    private ConnectionStatusEnum connectionStatus;

    private boolean isDatafeedEnabled;

    private String release;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
}
