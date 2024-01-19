package it.pyrox.directa.model;

public class AccountInfoMessage extends Message {

    private String time;
    private String accountId;
    private double liquidity;
    private double gainEuro;
    private double openProfitLoss;

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
}
