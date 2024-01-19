package it.pyrox.directa.api;

import it.pyrox.directa.enums.ConnectionStatusEnum;
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
    void testGetMappedPortsWhenWhenMultipleAccountsThenMapGivenAccount() throws IOException, ErrorMessageException {
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
    void testGetMappedPortsWhenOneAccountThenMapIt() throws IOException, ErrorMessageException {
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
    void testGetMappedPortsWhenNoAccountsThenReturnNull() throws IOException, ErrorMessageException {
        List<String> responseMessage = List.of(
                "BEGIN PORT",
                "END PORT");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        PortMappingMessage portMappingMessage = api.getMappedPorts();
        assertNull(portMappingMessage);
    }

    @Test
    void testGetStocksInfoWhenOneResultThenMapIt() throws IOException, ErrorMessageException {
        List<String> responseMessage = List.of("STOCK;A2A;10:40:58;4;0;4;1.2375;-1;");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        GetStocksInfoResponse response = api.getStocksInfo();
        assertNotNull(response);
        assertNotNull(response.getStockMessageList());
        assertEquals(1, response.getStockMessageList().size());
        assertEquals("A2A", response.getStockMessageList().get(0).getTicker());
        assertEquals("10:40:58", response.getStockMessageList().get(0).getTime());
        assertEquals(4, response.getStockMessageList().get(0).getPortfolioAmount());
        assertEquals("0", response.getStockMessageList().get(0).getBrokerAmount());
        assertEquals(4, response.getStockMessageList().get(0).getTradingAmount());
        assertEquals(1.2375, response.getStockMessageList().get(0).getAveragePrice());
        assertEquals(-1, response.getStockMessageList().get(0).getGain());
    }

    @Test
    void testGetStocksInfoWhenMultipleResultsThenMapThem() throws IOException, ErrorMessageException {
        List<String> responseMessage = List.of(
                "STOCK;A2A;10:40:58;4;0;4;1.2375;-1;",
                "STOCK;B2B;11:41:40;5;0;5;2.3123;-1;");
        TradingApi api = getMockedApi(testAccountId, responseMessage);
        GetStocksInfoResponse response = api.getStocksInfo();
        assertNotNull(response);
        assertNotNull(response.getStockMessageList());
        assertEquals(2, response.getStockMessageList().size());
        // Record 1
        assertEquals("A2A", response.getStockMessageList().get(0).getTicker());
        assertEquals("10:40:58", response.getStockMessageList().get(0).getTime());
        assertEquals(4, response.getStockMessageList().get(0).getPortfolioAmount());
        assertEquals("0", response.getStockMessageList().get(0).getBrokerAmount());
        assertEquals(4, response.getStockMessageList().get(0).getTradingAmount());
        assertEquals(1.2375, response.getStockMessageList().get(0).getAveragePrice());
        assertEquals(-1, response.getStockMessageList().get(0).getGain());
        // Record 2
        assertEquals("B2B", response.getStockMessageList().get(1).getTicker());
        assertEquals("11:41:40", response.getStockMessageList().get(1).getTime());
        assertEquals(5, response.getStockMessageList().get(1).getPortfolioAmount());
        assertEquals("0", response.getStockMessageList().get(1).getBrokerAmount());
        assertEquals(5, response.getStockMessageList().get(1).getTradingAmount());
        assertEquals(2.3123, response.getStockMessageList().get(1).getAveragePrice());
        assertEquals(-1, response.getStockMessageList().get(1).getGain());
    }

    @Test
    void testGetAvailabilityInfoWhenOneRecordThenMapIt() throws IOException, ErrorMessageException {
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

    // TODO this is a shared functionality, so maybe it could be moved in a common test class where all shared functionalities are tested
    @Test
    void testGetStatusWhenOneRecordThenMapIt() throws IOException, ErrorMessageException {
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

    private TradingApi getMockedApi(String accountId, List<String> messages) throws IOException, ErrorMessageException {
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
