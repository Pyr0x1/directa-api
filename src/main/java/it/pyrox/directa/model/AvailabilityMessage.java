package it.pyrox.directa.model;

public class AvailabilityMessage extends Message {

    private String time;
    private int stocksAvailability;
    private int stocksAvailabilityWithLeverage;
    private int derivativesAvailability;
    private int derivativesAvailabilityWithLeverage;
    private double totalLiquidity;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStocksAvailability() {
        return stocksAvailability;
    }

    public void setStocksAvailability(int stocksAvailability) {
        this.stocksAvailability = stocksAvailability;
    }

    public int getStocksAvailabilityWithLeverage() {
        return stocksAvailabilityWithLeverage;
    }

    public void setStocksAvailabilityWithLeverage(int stocksAvailabilityWithLeverage) {
        this.stocksAvailabilityWithLeverage = stocksAvailabilityWithLeverage;
    }

    public int getDerivativesAvailability() {
        return derivativesAvailability;
    }

    public void setDerivativesAvailability(int derivativesAvailability) {
        this.derivativesAvailability = derivativesAvailability;
    }

    public int getDerivativesAvailabilityWithLeverage() {
        return derivativesAvailabilityWithLeverage;
    }

    public void setDerivativesAvailabilityWithLeverage(int derivativesAvailabilityWithLeverage) {
        this.derivativesAvailabilityWithLeverage = derivativesAvailabilityWithLeverage;
    }

    public double getTotalLiquidity() {
        return totalLiquidity;
    }

    public void setTotalLiquidity(double totalLiquidity) {
        this.totalLiquidity = totalLiquidity;
    }

    @Override
    public String toString() {
        return "AvailabilityMessage{" +
                "type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", stocksAvailability=" + stocksAvailability +
                ", stocksAvailabilityWithLeverage=" + stocksAvailabilityWithLeverage +
                ", derivativesAvailability=" + derivativesAvailability +
                ", derivativesAvailabilityWithLeverage=" + derivativesAvailabilityWithLeverage +
                ", totalLiquidity=" + totalLiquidity +
                '}';
    }
}
