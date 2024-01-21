package it.pyrox.directa.api;

import java.io.IOException;

public class DatafeedApi extends DirectaApi {

    private static final Integer DEFAULT_DATAFEED_PORT = 10001;

    private String accountId;

    // Used in tests only
    protected DatafeedApi(String accountId, DirectaApiConnectionManager connectionManager) {
        super(accountId, connectionManager);
    }

    public DatafeedApi(String accountId) {
        super(accountId);
    }

    @Override
    protected int getPort() {
        return DEFAULT_DATAFEED_PORT;
    }

    @Override
    protected void openConnection() throws IOException {
        connectionManager.openConnection(getPort());
    }

    @Override
    protected void openConnection(String host, int timeout) throws IOException {
        connectionManager.openConnection(host, getPort(), timeout);
    }

    @Override
    protected void closeConnection() throws IOException {
        connectionManager.closeConnection();
    }
}
