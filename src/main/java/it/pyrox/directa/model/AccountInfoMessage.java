package it.pyrox.directa.model;

public class AccountInfoMessage extends Message {

    public static final String PREFIX = "INFOACCOUNT";

    private String time;
    private String accountId;
    private double liquidity;
    private double gainEuro;
    private double openProfitLoss;
    private double equity;
    // This is missing in the docs, so tried to decode it by looking at the response
    private String environment;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getLiquidity() {
        return liquidity;
    }

    public void setLiquidity(double liquidity) {
        this.liquidity = liquidity;
    }

    public double getGainEuro() {
        return gainEuro;
    }

    public void setGainEuro(double gainEuro) {
        this.gainEuro = gainEuro;
    }

    public double getOpenProfitLoss() {
        return openProfitLoss;
    }

    public void setOpenProfitLoss(double openProfitLoss) {
        this.openProfitLoss = openProfitLoss;
    }

    public double getEquity() {
        return equity;
    }

    public void setEquity(double equity) {
        this.equity = equity;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Override
    public String toString() {
        return "AccountInfoMessage{" +
                "type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", accountId='" + accountId + '\'' +
                ", liquidity=" + liquidity +
                ", gainEuro=" + gainEuro +
                ", openProfitLoss=" + openProfitLoss +
                ", equity=" + equity +
                ", environment='" + environment + '\'' +
                '}';
    }
}
