package it.pyrox.directa.api;

import it.pyrox.directa.enums.MessageTypeEnum;
import it.pyrox.directa.enums.OrderActionEnum;
import it.pyrox.directa.exception.ErrorMessageException;
import it.pyrox.directa.model.*;
import it.pyrox.directa.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        setConfigurationAndFlush();
    }

    @Override
    public void openConnection(String host, int timeout) throws IOException {
        connectionManager.openConnection(host, getPort(), timeout);
        setConfigurationAndFlush();
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
        PortMappingMessageParser parser = new PortMappingMessageParser();
        PortMappingMessage portMapping = null;
        connectionManager.sendCommand("SETCONNECTION");
        List<String> messageLines = connectionManager.readDelimitedMessage("BEGIN PORT", "END PORT", false);
        for (String messageLine : messageLines) {
            String accountId = messageLine.split(DELIMITER_SEMICOLON)[0];
            if (super.accountId.equals(accountId)) {
                portMapping = parser.parse(messageLine);
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
     * @return A list of StockMessages containing info about stocks in the portfolio
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public List<StockMessage> getStocksInfo() throws IOException, ErrorMessageException {
        connectionManager.sendCommand("INFOSTOCKS");
        List<StockMessage> stockList = new ArrayList<>();
        List<String> messageLines = connectionManager.readDelimitedMessage("BEGIN STOCKLIST", "END STOCKLIST", false);
        for (String messageLine : messageLines) {
            MessageParser parser = ParserFactory.create(messageLine);
            if (parser instanceof StockMessageParser) {
                stockList.add(((StockMessageParser)parser).parse(messageLine));
            }
            else if (parser instanceof ErrorMessageParser) {
                ErrorMessage errorMessage = ((ErrorMessageParser) parser).parse(messageLine);
                throw new ErrorMessageException(errorMessage.getError());
            }
        }
        return stockList;
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
        List<String> messageLines = connectionManager.readDelimitedMessage("BEGIN ORDERLIST", "END ORDERLIST", false);
        for (String messageLine : messageLines) {
            MessageParser parser = ParserFactory.create(messageLine);
            if (parser instanceof OrderMessageParser) {
                response.add(((OrderMessageParser)parser).parse(messageLine));
            }
            else if (parser instanceof ErrorMessageParser) {
                ErrorMessage errorMessage = ((ErrorMessageParser) parser).parse(messageLine);
                throw new ErrorMessageException(errorMessage.getError());
            }
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
        List<String> messageLines = connectionManager.readDelimitedMessage("BEGIN ORDERLIST", "END ORDERLIST", false);
        for (String messageLine : messageLines) {
            MessageParser parser = ParserFactory.create(messageLine);
            if (parser instanceof OrderMessageParser) {
                response.add(((OrderMessageParser)parser).parse(messageLine));
            }
            else if (parser instanceof ErrorMessageParser) {
                ErrorMessage errorMessage = ((ErrorMessageParser) parser).parse(messageLine);
                throw new ErrorMessageException(errorMessage.getError());
            }
        }

        return response;
    }

    /**
     * Returns the list of tables for the current account
     *
     * @return A list of TableMessage containing table code and description, an empty list if there are no tables
     * @throws IOException In case of communication error
     */
    public List<TableMessage> getTableList() throws IOException {
        TableMessageParser parser = new TableMessageParser();
        List<TableMessage> response = new ArrayList<>();
        connectionManager.sendCommand("TABLELIST");
        List<String> messageLines = connectionManager.readDelimitedMessage("BEGIN TABLE", "END TABLE", false);
        for (String messageLine : messageLines) {
            response.add(parser.parse(messageLine));
        }
        return response;
    }

    /**
     * Returns the list of elements for a specific table. If the table doesn't exist nothing is returned by the
     * server, so the call will end after the timeout set in the configuration
     *
     * @param tableName The table to get data about, with its relative code and description
     *                  as returned by {@link #getTableList() getTableList()}
     * @return A list of TableMessage containing ticker and description, an empty list if there are no elements in
     *         the specified table
     * @throws IOException In case of communication error
     */
    public List<TableMessage> getTableTickerList(TableMessage tableName) throws IOException {
        if (tableName == null || tableName.getCode() == null || tableName.getDescription() == null) {
            throw new IllegalArgumentException("The table name must be specified with both code and description");
        }
        TableMessageParser parser = new TableMessageParser();
        List<TableMessage> response = new ArrayList<>();
        connectionManager.sendCommand("TABLE " + tableName.getCode() + DirectaApi.DELIMITER_SEMICOLON + tableName.getDescription());
        List<String> messageLines = null;
        try {
            messageLines = connectionManager.readDelimitedMessage("BEGIN LIST", "END LIST", false);
        }
        catch (SocketTimeoutException e) {
            messageLines = new ArrayList<>();
        }
        for (String messageLine : messageLines) {
            response.add(parser.parse(messageLine));
        }
        return response;
    }

    // TODO why the orderId needs to be passed as input and isn't generated by the server???

    /**
     * Allows to buy a security at the specified price
     *
     * @param orderId The order id
     * @param ticker The ticker of the security to buy
     * @param amount The amount of elements to buy
     * @param price The limit price
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage buyAtLimitPrice(String orderId, String ticker, int amount, double price) throws IOException, ErrorMessageException {
        return buyOrSellGenericCall(OrderActionEnum.ACQAZ, orderId, ticker, amount, price, null);
    }

    /**
     * Allows to sell a security at the specified price
     *
     * @param orderId The order id
     * @param ticker The ticker of the security to sell
     * @param amount The amount of elements to sell
     * @param price The limit price
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage sellAtLimitPrice(String orderId, String ticker, int amount, double price) throws IOException, ErrorMessageException {
        return buyOrSellGenericCall(OrderActionEnum.VENAZ, orderId, ticker, amount, price, null);
    }

    /**
     * Allows to buy a security at the current market price
     *
     * @param orderId The order id
     * @param ticker The ticker of the security to buy
     * @param amount The amount of elements to buy
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage buyAtMarketPrice(String orderId, String ticker, int amount) throws IOException, ErrorMessageException {
        return buyOrSellGenericCall(OrderActionEnum.ACQMARKET, orderId, ticker, amount, null, null);
    }

    /**
     * Allows to sell a security at the current market price
     *
     * @param orderId The order id
     * @param ticker The ticker of the security to sell
     * @param amount The amount of elements to sell
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage sellAtMarketPrice(String orderId, String ticker, int amount) throws IOException, ErrorMessageException {
        return buyOrSellGenericCall(OrderActionEnum.VENMARKET, orderId, ticker, amount, null, null);
    }

    /**
     * Allows to buy a security with stop market price
     *
     * @param orderId The order id
     * @param ticker The ticker of the security to buy
     * @param amount The amount of elements to buy
     * @param trigger The trigger price
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage buyWithStopTrigger(String orderId, String ticker, int amount, double trigger) throws IOException, ErrorMessageException {
        return buyOrSellGenericCall(OrderActionEnum.ACQSTOP, orderId, ticker, amount, null, trigger);
    }

    /**
     * Allows to sell a security with stop market price
     *
     * @param orderId The order id
     * @param ticker The ticker of the security to sell
     * @param amount The amount of elements to sell
     * @param trigger The trigger price
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage sellWithStopTrigger(String orderId, String ticker, int amount, double trigger) throws IOException, ErrorMessageException {
        return buyOrSellGenericCall(OrderActionEnum.VENSTOP, orderId, ticker, amount, null, trigger);
    }

    /**
     * Allows to buy a security by specifying a stop trigger and a limit price
     *
     * @param orderId The order id
     * @param ticker The ticker of the security to buy
     * @param amount The amount of elements to buy
     * @param price The limit price
     * @param trigger The trigger price
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage buyAtLimitPriceWithStopTrigger(String orderId, String ticker, int amount, double price, double trigger) throws IOException, ErrorMessageException {
        return buyOrSellGenericCall(OrderActionEnum.ACQSTOPLIMIT, orderId, ticker, amount, price, trigger);
    }

    /**
     * Allows to sell a security by specifying a stop trigger and a limit price
     *
     * @param orderId The order id
     * @param ticker The ticker of the security to sell
     * @param amount The amount of elements to sell
     * @param price The limit price
     * @param trigger The trigger price
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage sellAtLimitPriceWithStopTrigger(String orderId, String ticker, int amount, double price, double trigger) throws IOException, ErrorMessageException {
        return buyOrSellGenericCall(OrderActionEnum.VENSTOPLIMIT, orderId, ticker, amount, price, trigger);
    }

    /**
     * Allows to cancel the specified order
     *
     * @param orderId The order id
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage revokeOrder(String orderId) throws IOException, ErrorMessageException {
        String command = String.join(DirectaApi.DELIMITER_SPACE, OrderActionEnum.REVORD.name(), orderId);
        connectionManager.sendCommand(command);
        String messageLine = connectionManager.readMessageLine();
        return manageTradingMessageResponse(messageLine);
    }

    /**
     * Allows to cancel all the orders for the specified ticker
     *
     * @param ticker The ticker of the security whose orders need to be cancelled
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage revokeAllOrders(String ticker) throws IOException, ErrorMessageException {
        String command = String.join(DirectaApi.DELIMITER_SPACE, OrderActionEnum.REVALL.name(), ticker);
        connectionManager.sendCommand(command);
        String messageLine = connectionManager.readMessageLine();
        return manageTradingMessageResponse(messageLine);
    }

    /**
     * Confirm the specified order, needed after a TradingMessage response of type TRADCONFIRM from a previous call
     *
     * @param orderId The order id
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage confirmOrder(String orderId) throws IOException, ErrorMessageException {
        String command = String.join(DirectaApi.DELIMITER_SPACE, OrderActionEnum.CONFORD.name(), orderId);
        connectionManager.sendCommand(command);
        String messageLine = connectionManager.readMessageLine();
        return manageTradingMessageResponse(messageLine);
    }

    /**
     * Allows to change the price for the specified order
     *
     * @param orderId The order id
     * @param price The new limit price to set
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage editLimitPrice(String orderId, double price) throws IOException, ErrorMessageException {
        String args = String.join(DirectaApi.DELIMITER_COMMA, orderId, Double.toString(price));
        String command = String.join(DirectaApi.DELIMITER_SPACE, OrderActionEnum.MODORD.name(), args);
        connectionManager.sendCommand(command);
        String messageLine = connectionManager.readMessageLine();
        return manageTradingMessageResponse(messageLine);
    }

    // TODO what about editing stop price only? It seems it can't be done by reading the api documentation...

    /**
     * Allows to change the price and the stop trigger for the specified order
     *
     * @param orderId The order id
     * @param price The new limit price to set
     * @param trigger The trigger price
     * @return A TradingMessage containing the server response
     * @throws IOException In case of communication error
     * @throws ErrorMessageException In case of application error
     */
    public TradingMessage editLimitPriceAndStopTrigger(String orderId, double price, double trigger) throws IOException, ErrorMessageException {
        String args = String.join(DirectaApi.DELIMITER_COMMA, orderId, Double.toString(price), Double.toString(trigger));
        String command = String.join(DirectaApi.DELIMITER_SPACE, OrderActionEnum.MODORD.name(), args);
        connectionManager.sendCommand(args);
        String messageLine = connectionManager.readMessageLine();
        return manageTradingMessageResponse(messageLine);
    }

    private TradingMessage buyOrSellGenericCall(OrderActionEnum action, String orderId, String ticker, Integer amount, Double price, Double trigger) throws IOException, ErrorMessageException {
        // Don't perform input validation because the server will do it and send an error if the command isn't compliant.
        // These arguments are always needed, so join them, if one is missing there will be an error
        String args = String.join(DirectaApi.DELIMITER_COMMA,
                      Optional.ofNullable(orderId).orElse(DirectaApi.EMPTY_STRING),
                      Optional.ofNullable(ticker).orElse(DirectaApi.EMPTY_STRING),
                      Optional.ofNullable(amount).isEmpty() ? DirectaApi.EMPTY_STRING : Integer.toString(amount));
        // These arguments depend on the action, so join them only if present.
        // The public methods will have these arguments as primitives, so they will need to be specified by the user.
        // Be careful to observe this detail otherwise something unexpected may happen, for example calling the method
        // to place a stop order passing the trigger value as null would place a market order
        // (this could also be an acceptable design, as of now a more explicit solution is preferred)
        if (price != null) {
            args = String.join(DirectaApi.DELIMITER_COMMA, args, Double.toString(price));
        }
        if (trigger != null) {
            args = String.join(DirectaApi.DELIMITER_COMMA, args, Double.toString(trigger));
        }
        String command = String.join(DirectaApi.DELIMITER_SPACE, action.name(), args);
        connectionManager.sendCommand(command);
        String messageLine = connectionManager.readMessageLine();
        return manageTradingMessageResponse(messageLine);
    }

    private TradingMessage manageTradingMessageResponse(String messageLine) throws ErrorMessageException {
        TradingMessage response = null;
        MessageParser parser = ParserFactory.create(messageLine);
        if (parser instanceof TradingMessageParser) {
            response = ((TradingMessageParser)parser).parse(messageLine);
            if (MessageTypeEnum.TRADERR.equals(response.getType())) {
                throw new ErrorMessageException(response.getErrorCode());
            }
        }
        else if (parser instanceof ErrorMessageParser) {
            ErrorMessage errorMessage = ((ErrorMessageParser) parser).parse(messageLine);
            throw new ErrorMessageException(errorMessage.getError());
        }
        return response;
    }

    private void setConfigurationAndFlush() throws IOException {
        // Set the flowpoint to true so order and stock list operations will have delimiters
        connectionManager.sendCommand("FLOWPOINT TRUE");
        String messageLine = "";
        // Read everything in the buffer until the response related to the flowpoint command.
        // This will flush the portfolio information that will be sent by the server
        // when a connection is established
        do {
            messageLine = connectionManager.readMessageLine();
        } while (!messageLine.startsWith("FLOWPOINT"));
    }
}
