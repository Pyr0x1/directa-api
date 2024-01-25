package it.pyrox.directa.api;

import it.pyrox.directa.model.StatusMessage;
import it.pyrox.directa.parser.StatusMessageParser;

import java.io.IOException;

public abstract class DirectaApi {

    protected final DirectaApiConnectionManager connectionManager;
    public static final String DELIMITER = ";";
    public static final String HEARTBEAT = "H";
    protected String accountId;

    protected DirectaApi(String accountId, DirectaApiConnectionManager connectionManager) {
        this.accountId = accountId;
        this.connectionManager = connectionManager;
    }

    protected DirectaApi(String accountId) {
        this.accountId = accountId;
        this.connectionManager = new DirectaApiConnectionManager();
    }

    protected abstract int getPort();

    protected abstract void openConnection() throws IOException;

    protected abstract void openConnection(String host, int timeout) throws IOException;

    protected abstract void closeConnection() throws IOException;

    protected StatusMessage getStatus() throws IOException {
        connectionManager.sendCommand("DARWINSTATUS");
        return readStatus();
    }

    /**
     * It allows to close the Darwin platform directly through an API command
     *
     * @param saveDesktop Saves the desktop configuration before exit
     * @throws IOException In case of communication error
     */
    public void closeDarwin(boolean saveDesktop) throws IOException {
        String command = "CLOSEDARWIN";
        if (!saveDesktop) {
            command += " FALSE";
        }
        connectionManager.sendCommand(command);
    }

    /**
     * Returns information about the connection and the platform version
     *
     * @return A StatusMessage containing status info
     * @throws IOException In case of communication error
     */
    protected StatusMessage readStatus() throws IOException {
        StatusMessageParser parser = new StatusMessageParser();
        // I expect only one line in response
        String messageLine = connectionManager.readMessageLine();
        return parser.parse(messageLine);
    }
}
