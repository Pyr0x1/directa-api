package it.pyrox.directa.api;

import it.pyrox.directa.model.PortMappingMessage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TradingApiTest {

    @Test
    void testMapPortsWhenMultipleAccountsThenMapGivenAccount() throws IOException {
        List<String> responseMessage = List.of(
                "A1234;10001;10002;10003",
                "B6546;10004;10005;10006",
                "C3212;10007;10008;10009");
        TradingApi api = new TradingApi("B6546");
        PortMappingMessage portMappingMessage = api.mapPorts(responseMessage);
        assertNotNull(portMappingMessage);
        assertEquals(10004, portMappingMessage.getDatafeedPort());
        assertEquals(10005, portMappingMessage.getTradingPort());
        assertEquals(10006, portMappingMessage.getHistoricalDataPort());
    }

    @Test
    void testMapPortsWhenOneAccountThenMapIt() throws IOException {
        List<String> responseMessage = List.of(
                "B6546;10001;10002;10003");
        TradingApi api = new TradingApi("B6546");
        PortMappingMessage portMappingMessage = api.mapPorts(responseMessage);
        assertNotNull(portMappingMessage);
        assertEquals(10001, portMappingMessage.getDatafeedPort());
        assertEquals(10002, portMappingMessage.getTradingPort());
        assertEquals(10003, portMappingMessage.getHistoricalDataPort());
    }

    @Test
    void testMapPortsWhenNoLinesThenReturnNull() throws IOException {
        List<String> responseMessage = List.of("");
        TradingApi api = new TradingApi("B6546");
        PortMappingMessage portMappingMessage = api.mapPorts(responseMessage);
        assertNull(portMappingMessage);
    }
}
