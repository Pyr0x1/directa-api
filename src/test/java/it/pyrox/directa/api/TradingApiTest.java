package it.pyrox.directa.api;

import it.pyrox.directa.enums.ConnectionStatusEnum;
import it.pyrox.directa.enums.ErrorEnum;
import it.pyrox.directa.enums.OrderActionEnum;
import it.pyrox.directa.enums.OrderStatusEnum;
import it.pyrox.directa.exception.ErrorMessageException;
import it.pyrox.directa.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TradingApiTest {

    private static final String testAccountId = "B5678";

    @Test
    void testGetMappedPortsWhenWhenMultipleAccountsThenMapGivenAccount() throws IOException {
        List<String> responseMessage = List.of(
                "BEGIN PORT",
                "A1234;10001;10002;10003",
                "B5678;10004;10005;10006",
                "C9101;10007;10008;10009",
                "END PORT");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        PortMappingMessage portMappingMessage = api.getMappedPorts();
        assertNotNull(portMappingMessage);
        assertEquals(10004, portMappingMessage.getDatafeedPort());
        assertEquals(10005, portMappingMessage.getTradingPort());
        assertEquals(10006, portMappingMessage.getHistoricalDataPort());
    }

    @Test
    void testGetMappedPortsWhenOneAccountThenMapIt() throws IOException {
        List<String> responseMessage = List.of(
                "BEGIN PORT",
                "B5678;10001;10002;10003",
                "END PORT");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        PortMappingMessage portMappingMessage = api.getMappedPorts();
        assertNotNull(portMappingMessage);
        assertEquals(10001, portMappingMessage.getDatafeedPort());
        assertEquals(10002, portMappingMessage.getTradingPort());
        assertEquals(10003, portMappingMessage.getHistoricalDataPort());
    }

    @Test
    void testGetMappedPortsWhenNoAccountsThenReturnNull() throws IOException {
        List<String> responseMessage = List.of(
                "BEGIN PORT",
                "END PORT");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        PortMappingMessage portMappingMessage = api.getMappedPorts();
        assertNull(portMappingMessage);
    }

    @Test
    void testGetAccountInfoWhenOneRecordThenMapIt() throws IOException {
        List<String> responseMessage = List.of("INFOACCOUNT;12:49:11;B5678;150000.50;1200.50;430.50;2;PROD");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        AccountInfoMessage accountInfoMessage = api.getAccountInfo();
        assertNotNull(accountInfoMessage);
        assertEquals(AccountInfoMessage.PREFIX, accountInfoMessage.getType());
        assertEquals("12:49:11", accountInfoMessage.getTime());
        assertEquals("B5678", accountInfoMessage.getAccountId());
        assertEquals(150000.50, accountInfoMessage.getLiquidity());
        assertEquals(1200.50, accountInfoMessage.getGainEuro());
        assertEquals(430.50, accountInfoMessage.getOpenProfitLoss());
        assertEquals(2, accountInfoMessage.getEquity());
        assertEquals("PROD", accountInfoMessage.getEnvironment());
    }

    @Test
    void testGetAvailabilityInfoWhenOneRecordThenMapIt() throws IOException {
        List<String> responseMessage = List.of(
                "AVAILABILITY;14:47:04;1000;5000;0;1;5000");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        AvailabilityMessage response = api.getAvailabilityInfo();
        assertNotNull(response);
        assertEquals(AvailabilityMessage.PREFIX, response.getType());
        assertEquals("14:47:04", response.getTime());
        assertEquals(1000, response.getStocksAvailability());
        assertEquals(5000, response.getStocksAvailabilityWithLeverage());
        assertEquals(0, response.getDerivativesAvailability());
        assertEquals(1, response.getDerivativesAvailabilityWithLeverage());
        assertEquals(5000, response.getTotalLiquidity());
    }

    @Test
    void testGetStocksInfoWhenOneRecordThenMapIt() throws IOException {
        List<String> responseMessage = List.of("STOCK;A2A;10:40:58;4;0;4;1.2375;-1;");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        GetStocksInfoResponse response = api.getStocksInfo();
        assertNotNull(response);
        assertNotNull(response.getStockMessageList());
        assertEquals(1, response.getStockMessageList().size());
        assertEquals(StockMessage.PREFIX, response.getStockMessageList().get(0).getType());
        assertEquals("A2A", response.getStockMessageList().get(0).getTicker());
        assertEquals("10:40:58", response.getStockMessageList().get(0).getTime());
        assertEquals(4, response.getStockMessageList().get(0).getPortfolioAmount());
        assertEquals("0", response.getStockMessageList().get(0).getBrokerAmount());
        assertEquals(4, response.getStockMessageList().get(0).getTradingAmount());
        assertEquals(1.2375, response.getStockMessageList().get(0).getAveragePrice());
        assertEquals(-1, response.getStockMessageList().get(0).getGain());
    }

    @Test
    void testGetStocksInfoWhenMultipleRecordsThenMapThem() throws IOException {
        List<String> responseMessage = List.of(
                "STOCK;A2A;10:40:58;4;0;4;1.2375;-1;",
                "STOCK;B2B;11:41:40;5;0;5;2.3123;-1;");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        GetStocksInfoResponse response = api.getStocksInfo();
        assertNotNull(response);
        assertNotNull(response.getStockMessageList());
        assertEquals(2, response.getStockMessageList().size());
        // Record 1
        assertEquals(StockMessage.PREFIX, response.getStockMessageList().get(0).getType());
        assertEquals("A2A", response.getStockMessageList().get(0).getTicker());
        assertEquals("10:40:58", response.getStockMessageList().get(0).getTime());
        assertEquals(4, response.getStockMessageList().get(0).getPortfolioAmount());
        assertEquals("0", response.getStockMessageList().get(0).getBrokerAmount());
        assertEquals(4, response.getStockMessageList().get(0).getTradingAmount());
        assertEquals(1.2375, response.getStockMessageList().get(0).getAveragePrice());
        assertEquals(-1, response.getStockMessageList().get(0).getGain());
        // Record 2
        assertEquals(StockMessage.PREFIX, response.getStockMessageList().get(1).getType());
        assertEquals("B2B", response.getStockMessageList().get(1).getTicker());
        assertEquals("11:41:40", response.getStockMessageList().get(1).getTime());
        assertEquals(5, response.getStockMessageList().get(1).getPortfolioAmount());
        assertEquals("0", response.getStockMessageList().get(1).getBrokerAmount());
        assertEquals(5, response.getStockMessageList().get(1).getTradingAmount());
        assertEquals(2.3123, response.getStockMessageList().get(1).getAveragePrice());
        assertEquals(-1, response.getStockMessageList().get(1).getGain());
    }

    @Test
    void testGetPositionWhenOneRecordThenMapIt() throws IOException, ErrorMessageException {
        List<String> responseMessage = List.of("STOCK;B2B;11:41:40;5;0;5;2.3123;-1");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        StockMessage stockMessage = api.getPosition("A2A");
        assertNotNull(stockMessage);
        assertEquals(StockMessage.PREFIX, stockMessage.getType());
        assertEquals("B2B", stockMessage.getTicker());
        assertEquals("11:41:40", stockMessage.getTime());
        assertEquals(5, stockMessage.getPortfolioAmount());
        assertEquals("0", stockMessage.getBrokerAmount());
        assertEquals(5, stockMessage.getTradingAmount());
        assertEquals(2.3123, stockMessage.getAveragePrice());
        assertEquals(-1, stockMessage.getGain());
    }

    @Test
    void testGetPositionWhenNoRecordsThenError() throws IOException {
        List<String> responseMessage = List.of("ERR;B2B;1007");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        ErrorMessageException exception = assertThrows(ErrorMessageException.class, () -> {
            api.getPosition("B2B");
        });
        assertNotNull(exception);
        assertEquals(ErrorEnum.ERR_BAD_SUBSCRIPTION, exception.getError());
    }

    @Test
    void testGetOrderListWhenFilterCancelledAndOneRecordThenMapIt() throws IOException, ErrorMessageException {
        List<String> responseMessage = List.of("ORDER;STLAM;16:20:40;ORD1;ACQAZ;4.75;0.0;10;2000");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        List<OrderMessage> orderMessageList = api.getOrderList(true, false);
        assertNotNull(orderMessageList);
        assertEquals(1, orderMessageList.size());
        assertEquals(OrderMessage.PREFIX, orderMessageList.get(0).getType());
        assertEquals("STLAM", orderMessageList.get(0).getTicker());
        assertEquals("16:20:40", orderMessageList.get(0).getTime());
        assertEquals("ORD1", orderMessageList.get(0).getOrderId());
        assertEquals(OrderActionEnum.ACQAZ, orderMessageList.get(0).getOperationType());
        assertEquals(4.75, orderMessageList.get(0).getLimitPrice());
        assertEquals(0.0, orderMessageList.get(0).getTriggerPrice());
        assertEquals(10, orderMessageList.get(0).getAmount());
        assertEquals(OrderStatusEnum.IN_NEGOTIATION, orderMessageList.get(0).getOrderStatus());
    }

    @Test
    void testGetOrderListWhenFilterCancelledAndMultipleRecordsThenMapThem() throws IOException, ErrorMessageException {
        List<String> responseMessage = List.of("ORDER;STLAM;16:20:40;ORD1;ACQAZ;4.75;0.0;10;2000",
                                               "ORDER;SPAM;17:25:51;ORD2;ACQAZ;5.75;1.0;20;2005");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        List<OrderMessage> orderMessageList = api.getOrderList(true, false);
        assertNotNull(orderMessageList);
        assertEquals(2, orderMessageList.size());
        // Record 1
        assertEquals(OrderMessage.PREFIX, orderMessageList.get(0).getType());
        assertEquals("STLAM", orderMessageList.get(0).getTicker());
        assertEquals("16:20:40", orderMessageList.get(0).getTime());
        assertEquals("ORD1", orderMessageList.get(0).getOrderId());
        assertEquals(OrderActionEnum.ACQAZ, orderMessageList.get(0).getOperationType());
        assertEquals(4.75, orderMessageList.get(0).getLimitPrice());
        assertEquals(0.0, orderMessageList.get(0).getTriggerPrice());
        assertEquals(10, orderMessageList.get(0).getAmount());
        assertEquals(OrderStatusEnum.IN_NEGOTIATION, orderMessageList.get(0).getOrderStatus());
        // Record 2
        assertEquals(OrderMessage.PREFIX, orderMessageList.get(1).getType());
        assertEquals("SPAM", orderMessageList.get(1).getTicker());
        assertEquals("17:25:51", orderMessageList.get(1).getTime());
        assertEquals("ORD2", orderMessageList.get(1).getOrderId());
        assertEquals(OrderActionEnum.ACQAZ, orderMessageList.get(1).getOperationType());
        assertEquals(5.75, orderMessageList.get(1).getLimitPrice());
        assertEquals(1.0, orderMessageList.get(1).getTriggerPrice());
        assertEquals(20, orderMessageList.get(1).getAmount());
        assertEquals(OrderStatusEnum.WAITING_FOR_VALIDATION, orderMessageList.get(1).getOrderStatus());
    }

    @Test
    void testGetOrderListWhenFilterCancelledAndNoOrdersThenError() throws IOException {
        List<String> responseMessage = List.of("ERR;B2B;1019");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        ErrorMessageException exception = assertThrows(ErrorMessageException.class, () -> {
            api.getOrderList(true, false);
        });
        assertNotNull(exception);
        assertEquals(ErrorEnum.ERR_EMPTY_ORDERLIST, exception.getError());
    }

    @Test
    void testGetOrderListWhenUnsupportedFlagsThenError() throws IOException {
        TradingApi api = getMockedApi(testAccountId, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            api.getOrderList(false, true);
        });
        assertNotNull(exception);
    }

    @Test
    void testGetOrderListWithTickerWhenOneRecordThenMapIt() throws IOException, ErrorMessageException {
        List<String> responseMessage = List.of("ORDER;STLAM;16:20:40;ORD1;ACQAZ;4.75;0.0;10;2000");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        List<OrderMessage> orderMessageList = api.getOrderList("STLAM");
        assertNotNull(orderMessageList);
        assertEquals(1, orderMessageList.size());
        assertEquals(OrderMessage.PREFIX, orderMessageList.get(0).getType());
        assertEquals("STLAM", orderMessageList.get(0).getTicker());
        assertEquals("16:20:40", orderMessageList.get(0).getTime());
        assertEquals("ORD1", orderMessageList.get(0).getOrderId());
        assertEquals(OrderActionEnum.ACQAZ, orderMessageList.get(0).getOperationType());
        assertEquals(4.75, orderMessageList.get(0).getLimitPrice());
        assertEquals(0.0, orderMessageList.get(0).getTriggerPrice());
        assertEquals(10, orderMessageList.get(0).getAmount());
        assertEquals(OrderStatusEnum.IN_NEGOTIATION, orderMessageList.get(0).getOrderStatus());
    }

    @Test
    void testGetOrderListWithTickerWhenMultipleRecordsThenMapThem() throws IOException, ErrorMessageException {
        List<String> responseMessage = List.of("ORDER;STLAM;16:20:40;ORD1;ACQAZ;4.75;0.0;10;2000",
                "ORDER;STLAM;17:25:51;ORD2;ACQAZ;5.75;1.0;20;2005");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        List<OrderMessage> orderMessageList = api.getOrderList("STLAM");
        assertNotNull(orderMessageList);
        assertEquals(2, orderMessageList.size());
        // Record 1
        assertEquals(OrderMessage.PREFIX, orderMessageList.get(0).getType());
        assertEquals("STLAM", orderMessageList.get(0).getTicker());
        assertEquals("16:20:40", orderMessageList.get(0).getTime());
        assertEquals("ORD1", orderMessageList.get(0).getOrderId());
        assertEquals(OrderActionEnum.ACQAZ, orderMessageList.get(0).getOperationType());
        assertEquals(4.75, orderMessageList.get(0).getLimitPrice());
        assertEquals(0.0, orderMessageList.get(0).getTriggerPrice());
        assertEquals(10, orderMessageList.get(0).getAmount());
        assertEquals(OrderStatusEnum.IN_NEGOTIATION, orderMessageList.get(0).getOrderStatus());
        // Record 2
        assertEquals(OrderMessage.PREFIX, orderMessageList.get(1).getType());
        assertEquals("STLAM", orderMessageList.get(1).getTicker());
        assertEquals("17:25:51", orderMessageList.get(1).getTime());
        assertEquals("ORD2", orderMessageList.get(1).getOrderId());
        assertEquals(OrderActionEnum.ACQAZ, orderMessageList.get(1).getOperationType());
        assertEquals(5.75, orderMessageList.get(1).getLimitPrice());
        assertEquals(1.0, orderMessageList.get(1).getTriggerPrice());
        assertEquals(20, orderMessageList.get(1).getAmount());
        assertEquals(OrderStatusEnum.WAITING_FOR_VALIDATION, orderMessageList.get(1).getOrderStatus());
    }

    @Test
    void testGetOrderListWhenNoOrdersThenError() throws IOException {
        List<String> responseMessage = List.of("ERR;B2B;1019");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        ErrorMessageException exception = assertThrows(ErrorMessageException.class, () -> {
            api.getOrderList("STLAM");
        });
        assertNotNull(exception);
        assertEquals(ErrorEnum.ERR_EMPTY_ORDERLIST, exception.getError());
    }

    @Test
    void testGetTableListWhenMultipleRecordsThenMapThem() throws IOException {
        List<String> responseMessage = List.of(
                "BEGIN TABLE",
                "AO;OPTIONS",
                "AC;COMBO",
                "M0;MOT",
                "AZ;FIB",
                "END TABLE");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        List<TableMessage> tableMessageList = api.getTableList();
        assertNotNull(tableMessageList);
        assertEquals(4, tableMessageList.size());
        assertEquals("AO", tableMessageList.get(0).getCode());
        assertEquals("OPTIONS", tableMessageList.get(0).getDescription());
        assertEquals("AC", tableMessageList.get(1).getCode());
        assertEquals("COMBO", tableMessageList.get(1).getDescription());
        assertEquals("M0", tableMessageList.get(2).getCode());
        assertEquals("MOT", tableMessageList.get(2).getDescription());
        assertEquals("AZ", tableMessageList.get(3).getCode());
        assertEquals("FIB", tableMessageList.get(3).getDescription());
    }

    @Test
    void testGetTableTickerListWhenMultipleRecordsThenMapThem() throws IOException {
        List<String> responseMessage = List.of(
                "BEGIN LIST A1;PRIMA (9)",
                "AZM;AZIMUT",
                "A2A;A2A",
                "B2B;BLA2BLA",
                "END LIST");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        TableMessage tableMessage = new TableMessage();
        tableMessage.setCode("A1");
        tableMessage.setDescription("PRIMA");
        List<TableMessage> tableMessageList = api.getTableTickerList(tableMessage);
        assertNotNull(tableMessageList);
        assertEquals(3, tableMessageList.size());
        assertEquals("AZM", tableMessageList.get(0).getCode());
        assertEquals("AZIMUT", tableMessageList.get(0).getDescription());
        assertEquals("A2A", tableMessageList.get(1).getCode());
        assertEquals("A2A", tableMessageList.get(1).getDescription());
        assertEquals("B2B", tableMessageList.get(2).getCode());
        assertEquals("BLA2BLA", tableMessageList.get(2).getDescription());
    }

    @Test
    void testGetTableTickerListWhenNoRecordsThenEmptyList() throws IOException {
        List<String> responseMessage = List.of(
                "BEGIN LIST A1;PRIMA (0)",
                "END LIST");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        TableMessage tableMessage = new TableMessage();
        tableMessage.setCode("A1");
        tableMessage.setDescription("PRIMA");
        List<TableMessage> tableMessageList = api.getTableTickerList(tableMessage);
        assertNotNull(tableMessageList);
        assertEquals(0, tableMessageList.size());
    }

    @Test
    void testGetTableTickerListWhenNoInputThenError() throws IOException {
        TradingApi api = getMockedApi(testAccountId, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            api.getTableTickerList(null);
        });
        assertNotNull(exception);
    }

    @Test
    void testGetTableTickerListWhenNoTableCodeThenError() throws IOException {
        TableMessage tableMessage = new TableMessage();
        tableMessage.setDescription("test");
        TradingApi api = getMockedApi(testAccountId, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            api.getTableTickerList(tableMessage);
        });
        assertNotNull(exception);
    }

    @Test
    void testGetTableTickerListWhenNoTableDescriptionThenError() throws IOException {
        TableMessage tableMessage = new TableMessage();
        tableMessage.setCode("code");
        TradingApi api = getMockedApi(testAccountId, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            api.getTableTickerList(tableMessage);
        });
        assertNotNull(exception);
    }

    // TODO this is a shared functionality, so maybe it could be moved in a common test class where all shared functionalities are tested
    @Test
    void testGetStatusWhenOneRecordThenMapIt() throws IOException {
        List<String> responseMessage = List.of(
                "DARWIN_STATUS;CONN_OK;TRUE;Release 1.2.1 build 01/08/2020 14:10:00 more info at http://app1.directatrading.com/trading-api-directa/index.html");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        StatusMessage response = api.getStatus();
        assertNotNull(response);
        assertEquals(StatusMessage.PREFIX, response.getType());
        assertEquals(ConnectionStatusEnum.CONN_OK, response.getConnectionStatus());
        assertTrue(response.isDatafeedEnabled());
        assertEquals("Release 1.2.1 build 01/08/2020 14:10:00 more info at http://app1.directatrading.com/trading-api-directa/index.html", response.getRelease());
    }

    private TradingApi getMockedApi(String accountId, List<String> messages) throws IOException {
        DirectaApiConnectionManager connectionManager = mock(DirectaApiConnectionManager.class);
        when(connectionManager.readDelimitedMessage(anyString(), anyString(), anyBoolean())).thenCallRealMethod();
        when(connectionManager.readMessageLine()).thenAnswer(new Answer<String>() {
            private int count = 0;

            public String answer(InvocationOnMock invocation) throws SocketTimeoutException {
                String result = null;
                if (messages != null && !messages.isEmpty()) {
                    if (count < messages.size()) {
                        result = messages.get(count++);
                    }
                    else {
                        throw new SocketTimeoutException();
                    }
                }
                return result;
            }
        });
        return new TradingApi(accountId, connectionManager);
    }
}
