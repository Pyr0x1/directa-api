package it.pyrox.directa.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class DirectaApi {

    private static final String HOST = "127.0.0.1";
    private static final String EMPTY_STRING = "";
    public static final String DELIMITER = ";";
    protected final String accountId;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private static final Logger logger = LoggerFactory.getLogger(DirectaApi.class);

    public DirectaApi(String accountId) {
        this.accountId = accountId;
    }

    protected abstract Integer getPort();

    protected void openConnection() throws IOException {
        socket = new Socket(HOST, getPort());
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        logger.info("Opened connection on {}:{}", HOST, getPort());
    }

    protected void sendCommand(String command) throws IOException {
        output.println(command);
        logger.debug("Sent command: {}", command);
    }

    protected String readMessageLine() throws IOException {
        String messageLine = null;
        if (input != null) {
            input.readLine();
            logger.debug("Read message line: {}", messageLine);
        }
        return messageLine;
    }

    protected List<String> readDelimitedMessage(String start, String end, boolean includeDelimiters) throws IOException {
        if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
            throw new IllegalArgumentException("The starting and ending delimiters must be defined");
        }
        String buffer = null;
        List<String> lines = new ArrayList<>();
        buffer = readMessageLine();
        while (!start.equals(buffer)) {
            buffer = readMessageLine();
        }
        if (includeDelimiters) {
            lines.add(buffer);
        }
        while (!end.equals(buffer)) {
            buffer = readMessageLine();
            // avoid heartbeats
            if (!"H".equals(buffer)) {
                lines.add(buffer);
            }
        }
        if (!includeDelimiters) {
            lines.remove(lines.size() - 1);
        }

        return lines;
    }

    protected void closeConnection() throws IOException {
        input.close();
        output.close();
        socket.close();
        logger.info("Closed connection");
    }
}
