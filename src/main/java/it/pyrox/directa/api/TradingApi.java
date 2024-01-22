package it.pyrox.directa.api;

import it.pyrox.directa.exception.ErrorMessageException;
import it.pyrox.directa.model.*;
import it.pyrox.directa.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class TradingApi extends DirectaApi {

    private static final Integer DEFAULT_TRADING_PORT = 10002;
    private static final Logger logger = LoggerFactory.getLogger(TradingApi.class);

    // Used in tests only
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
        readStatus();
        readStocksInfoResponse();
    }

    @Override
    public void closeConnection() throws IOException {
        connectionManager.closeConnection();
    }

    /**
     * Allows to retrieve the port configuration for the current account
     *
     * @return A PortMapping message containing info about ports
     * @throws IOException In case of communication error
     */
    public PortMappingMessage getMappedPorts() throws IOException {
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

    /**
     * Retrieve information about the account status
     *
     * @return An AccountInfoMessage containing info about the account status
     * @throws IOException In case of communication error
     */
    public AccountInfoMessage getAccountInfo() throws IOException {
        connectionManager.sendCommand("INFOACCOUNT");
        AccountInfoMessageParser parser = new AccountInfoMessageParser();
        // I expect only one line in response
        String messageLine = connectionManager.readMessageLine();
        return parser.parse(messageLine);
    }

    /**
     * Returns the situation of the availability in the portfolio
     *
     * @return An AvailabilityMessage containing availability info
     * @throws IOException In case of communication error
     */
    public AvailabilityMessage getAvailabilityInfo() throws IOException {
        connectionManager.sendCommand("INFOAVAILABILITY");
        AvailabilityMessageParser parser = new AvailabilityMessageParser();
        // I expect only one line in response
        String messageLine = connectionManager.readMessageLine();
        return parser.parse(messageLine);
    }

    /**
     * Retrieves the list of stocks in the portfolio and in negotiation
     *
     * @return A GetStockInfoResponse object containing the list of stocks,
     * the list of orders and a list of possible errors (for example if you have no active orders)
     * @throws IOException In case of communication error
     */
    public GetStocksInfoResponse getStocksInfo() throws IOException {
        connectionManager.sendCommand("INFOSTOCKS");
        return readStocksInfoResponse();
    }

    /**
     * Returns information about the position of a single security
     *
     * @param ticker The ticker of the item you want to get info about
     * @return A StockMessage containing info about the element with the given ticker
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error (for example no position found for the given ticker)
     */
    public StockMessage getPosition(String ticker) throws IOException, ErrorMessageException {
        if (ticker == null || ticker.isEmpty()) {
            throw new IllegalArgumentException("The ticker must be specified for the GETPOSITION command");
        }
        connectionManager.sendCommand("GETPOSITION " + ticker);
        // I expect one line in response, that can be a stock message or an error message if you don't own the specified item
        String messageLine = connectionManager.readMessageLine();
        MessageParser parser = ParserFactory.create(messageLine);
        if (parser instanceof StockMessageParser) {
            return ((StockMessageParser) parser).parse(messageLine);
        }
        else if (parser instanceof ErrorMessageParser) {
            ErrorMessage errorMessage = ((ErrorMessageParser) parser).parse(messageLine);
            throw new ErrorMessageException(errorMessage.getError());
        }
        else {
            return null;
        }
    }

    /**
     * Returns the list of orders, that can be filtered with the input flags
     *
     * @param filterCanceled Set it to true to filter out cancelled orders
     * @param filterExecuted Set it to true to filter out executed orders (this can be true only if the flag for cancelled orders is true)
     * @return A list of OrderMessage containing order info
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public List<OrderMessage> getOrderList(boolean filterCanceled, boolean filterExecuted) throws IOException, ErrorMessageException {
        List<OrderMessage> response = new ArrayList<>();
        if (filterExecuted && !filterCanceled) {
            throw new IllegalArgumentException("Unsupported filter combination, you can filter out executed orders only if you filter out cancelled orders as well");
        }
        if (filterCanceled && !filterExecuted) {
            connectionManager.sendCommand("ORDERLISTNOREV");
        }
        else if (filterCanceled) {
            connectionManager.sendCommand("ORDERLISTPENDING");
        }
        else {
            connectionManager.sendCommand("ORDERLIST");
        }
        try {
            while (true) {
                String messageLine = connectionManager.readMessageLine();
                MessageParser parser = ParserFactory.create(messageLine);
                if (parser instanceof OrderMessageParser) {
                    response.add(((OrderMessageParser)parser).parse(messageLine));
                }
                else if (parser instanceof ErrorMessageParser) {
                    ErrorMessage errorMessage = ((ErrorMessageParser) parser).parse(messageLine);
                    throw new ErrorMessageException(errorMessage.getError());
                }
            }
        } catch (SocketTimeoutException e) {
            // Break the loop in case of socket timeout, must do this
            // because I do not know beforehand how many lines to read
        }
        return response;
    }

    /**
     * Returns the list of orders for the specified ticker
     *
     * @param ticker The ticker you want to search for related orders
     * @return A list of OrderMessage containing order info
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public List<OrderMessage> getOrderList(String ticker) throws IOException, ErrorMessageException {
        List<OrderMessage> response = new ArrayList<>();
        connectionManager.sendCommand("ORDERLIST " + ticker);
        try {
            while (true) {
                String messageLine = connectionManager.readMessageLine();
                MessageParser parser = ParserFactory.create(messageLine);
                if (parser instanceof OrderMessageParser) {
                    response.add(((OrderMessageParser)parser).parse(messageLine));
                }
                else if (parser instanceof ErrorMessageParser) {
                    ErrorMessage errorMessage = ((ErrorMessageParser) parser).parse(messageLine);
                    throw new ErrorMessageException(errorMessage.getError());
                }
            }
        } catch (SocketTimeoutException e) {
            // Break the loop in case of socket timeout, must do this
            // because I do not know beforehand how many lines to read
        }
        return response;
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
