package it.pyrox.directa.model;

public class PortMappingMessage {

    private String accountId;

    private int DatafeedPort;

    private int TradingPort;

    private int HistoricalDataPort;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getDatafeedPort() {
        return DatafeedPort;
    }

    public void setDatafeedPort(int datafeedPort) {
        DatafeedPort = datafeedPort;
    }

    public int getTradingPort() {
        return TradingPort;
    }

    public void setTradingPort(int tradingPort) {
        TradingPort = tradingPort;
    }

    public int getHistoricalDataPort() {
        return HistoricalDataPort;
    }

    public void setHistoricalDataPort(int historicalDataPort) {
        HistoricalDataPort = historicalDataPort;
    }

    @Override
    public String toString() {
        return "PortMappingMessage{" +
                "accountId='" + accountId + '\'' +
                ", DatafeedPort=" + DatafeedPort +
                ", TradingPort=" + TradingPort +
                ", HistoricalDataPort=" + HistoricalDataPort +
                '}';
    }
}
