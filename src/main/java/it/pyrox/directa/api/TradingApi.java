package it.pyrox.directa.api;

import it.pyrox.directa.exception.ErrorMessageException;
import it.pyrox.directa.model.AccountInfoMessage;
import it.pyrox.directa.model.AvailabilityMessage;
import it.pyrox.directa.model.GetStocksInfoResponse;
import it.pyrox.directa.model.PortMappingMessage;
import it.pyrox.directa.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

public class TradingApi extends DirectaApi {

    private static final Integer DEFAULT_TRADING_PORT = 10002;
    private static final Logger logger = LoggerFactory.getLogger(TradingApi.class);

    protected TradingApi(String accountId, DirectaApiConnectionManager connectionManager) {
        super(accountId, connectionManager);
    }

    public TradingApi(String accountId) {
        super(accountId);
    }

    @Override
    protected int getPort() {
        return DEFAULT_TRADING_PORT;
    }

    @Override
    public void openConnection() throws IOException {
        connectionManager.openConnection(getPort());
        // When you open the connection the trading api will send the portfolio composition
        // So, read it so the channel is then "clear" for further commands
        readStatus();
        readStocksInfoResponse();
    }

    @Override
    public void openConnection(String host, int timeout) throws IOException {
        connectionManager.openConnection(host, getPort(), timeout);
        // When you open the connection the trading api will send the portfolio composition
        // So, read it so the channel is then "clear" for further commands
        readStocksInfoResponse();
    }

    @Override
    public void closeConnection() throws IOException {
        connectionManager.closeConnection();
    }

    public void closeDarwin(boolean saveDesktop) throws IOException {
        String comamnd = "CLOSEDARWIN";
        if (!saveDesktop) {
            comamnd += " FALSE";
        }
        connectionManager.sendCommand(comamnd);
    }

    public PortMappingMessage getMappedPorts() throws IOException, ErrorMessageException {
        PortMappingMessage portMapping = null;
        connectionManager.sendCommand("SETCONNECTION");
        List<String> messageLines = connectionManager.readDelimitedMessage("BEGIN PORT", "END PORT", false);
        for (String messageLine : messageLines) {
            String accountId = messageLine.split(DELIMITER)[0];
            if (super.accountId.equals(accountId)) {
                portMapping = new PortMappingParser().parse(messageLine);
            }
        }
        return portMapping;
    }

    public GetStocksInfoResponse getStocksInfo() throws IOException {
        connectionManager.sendCommand("INFOSTOCKS");
        return readStocksInfoResponse();
    }

    public AvailabilityMessage getAvailabilityInfo() throws IOException {
        connectionManager.sendCommand("INFOAVAILABILITY");
        AvailabilityMessageParser parser = new AvailabilityMessageParser();
        // I expect only one line in response
        String messageLine = connectionManager.readMessageLine();
        return parser.parse(messageLine);
    }

    // TODO verify the format of the response
    public GetStocksInfoResponse getPosition(String ticker) throws IOException {
        if (ticker == null || ticker.isEmpty()) {
            throw new IllegalArgumentException("The ticker must be specified for the GETPOSITION command");
        }
        connectionManager.sendCommand("GETPOSITION " + ticker);
        return readStocksInfoResponse();
    }

    // TODO test this
    public AccountInfoMessage getAccountInfo() throws IOException {
        connectionManager.sendCommand("INFOACCOUNT");
        AccountInfoMessageParser parser = new AccountInfoMessageParser();
        // I expect only one line in response
        String messageLine = connectionManager.readMessageLine();
        return parser.parse(messageLine);
    }

    private GetStocksInfoResponse readStocksInfoResponse() throws IOException {
        GetStocksInfoResponse response = new GetStocksInfoResponse();
        try {
            while (true) {
                String messageLine = connectionManager.readMessageLine();
                MessageParser parser = ParserFactory.create(messageLine);
                if (parser instanceof StockMessageParser) {
                    response.getStockMessageList().add(((StockMessageParser) parser).parse(messageLine));
                }
                else if (parser instanceof OrderMessageParser) {
                    response.getOrderMessageList().add(((OrderMessageParser) parser).parse(messageLine));
                }
                else if (parser instanceof ErrorMessageParser) {
                    response.getErrorMessageList().add(((ErrorMessageParser) parser).parse(messageLine));
                }
            }
        } catch (SocketTimeoutException e) {
            // Break the loop in case of socket timeout, must do this
            // because I do not know beforehand how many lines to read
        }
        return response;
    }
}
