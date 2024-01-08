package it.pyrox.directa.api;

import it.pyrox.directa.enums.ApiEnum;
import it.pyrox.directa.model.PortMappingMessage;
import it.pyrox.directa.parser.PortMappingParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class TradingApi extends DirectaApi {

    private static final Integer DEFAULT_TRADING_PORT = 10002;

    private static final Logger logger = LoggerFactory.getLogger(TradingApi.class);

    public TradingApi(String accountId) {
        super(accountId);
    }

    @Override
    protected Integer getPort() {
        return DEFAULT_TRADING_PORT;
    }

    public void closeDarwin(boolean saveDesktop) throws IOException {
        openConnection();
        String comamnd = "CLOSEDARWIN";
        if (!saveDesktop) {
            comamnd += " FALSE";
        }
        sendCommand(comamnd);
        closeConnection();
    }

    public PortMappingMessage getMappedPorts() throws IOException {
        openConnection();
        sendCommand("SETCONNECTION");
        List<String> messageLines = readDelimitedMessage("BEGIN PORT", "END PORT", false);
        closeConnection();
        return mapPorts(messageLines);
    }

    protected PortMappingMessage mapPorts(List<String> messageLines) {
        PortMappingMessage portMapping = null;
        for (String messageLine : messageLines) {
            String accountId = messageLine.split(DELIMITER)[0];
            if (super.accountId.equals(accountId)) {
                portMapping = new PortMappingParser().parse(messageLine);
            }
        }
        return portMapping;
    }
}
