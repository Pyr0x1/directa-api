package it.pyrox.directa.api;

import it.pyrox.directa.exception.ErrorMessageException;
import it.pyrox.directa.model.ErrorMessage;
import it.pyrox.directa.parser.ErrorMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class DirectaApiConnectionManager {

    private static final String LOCAL_HOST = "127.0.0.1";
    private static final int DEFAULT_TIMEOUT_MS = 5000;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private static final Logger logger = LoggerFactory.getLogger(DirectaApiConnectionManager.class);

    protected void openConnection(String host, int port, int timeout) throws IOException {
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            socket = new Socket(host, port);
            socket.setSoTimeout(timeout);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            logger.info("Opened connection on {}:{}", host, port);
        }
        else {
            throw new IOException("The socket is already connected, close the connection before creating a new one");
        }
    }

    protected void openConnection(int port) throws IOException {
        openConnection(LOCAL_HOST, port, DEFAULT_TIMEOUT_MS);
    }

    protected void sendCommand(String command) throws IOException {
        output.println(command);
        logger.debug("Sent command: {}", command);
    }

    /**
     * Method used to read a message line from the socket.
     *
     * @return Message line or null in case of heartbeat or if connection hasn't been opened
     * @throws IOException In case of communication error
     */
    protected String readMessageLine() throws IOException {
        String messageLine = null;
        if (socket != null && socket.isConnected() && input != null) {
            messageLine = input.readLine();
            logger.debug("Read message line: {}", messageLine);
            if (messageLine.equals(DirectaApi.HEARTBEAT)) {
                messageLine = null;
            }
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
        while (!buffer.startsWith(start)) {
            buffer = readMessageLine();
        }
        if (includeDelimiters) {
            lines.add(buffer);
        }
        while (!end.equals(buffer)) {
            buffer = readMessageLine();
            // avoid heartbeats
            if (buffer != null) {
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
