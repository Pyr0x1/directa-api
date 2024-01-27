package it.pyrox.directa.model;

public class StockMessage extends Message {

    private String ticker;
    private String time;
    private int portfolioAmount;
    private String brokerAmount; // TODO manage this according to the wiki
    private int tradingAmount;
    private double averagePrice;
    private double gain;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPortfolioAmount() {
        return portfolioAmount;
    }

    public void setPortfolioAmount(int portfolioAmount) {
        this.portfolioAmount = portfolioAmount;
    }

    public String getBrokerAmount() {
        return brokerAmount;
    }

    public void setBrokerAmount(String brokerAmount) {
        this.brokerAmount = brokerAmount;
    }

    public int getTradingAmount() {
        return tradingAmount;
    }

    public void setTradingAmount(int tradingAmount) {
        this.tradingAmount = tradingAmount;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    @Override
    public String toString() {
        return "StockMessage{" +
                "type='" + type + '\'' +
                ", ticker='" + ticker + '\'' +
                ", time='" + time + '\'' +
                ", portfolioAmount=" + portfolioAmount +
                ", brokerAmount='" + brokerAmount + '\'' +
                ", tradingAmount=" + tradingAmount +
                ", averagePrice=" + averagePrice +
                ", gain=" + gain +
                '}';
    }
}
