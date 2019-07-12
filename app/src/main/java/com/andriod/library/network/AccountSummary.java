package com.andriod.library.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AccountSummary implements Serializable {
    private static final long serialVersionUID = -1;
    @SerializedName("transferStatus")
    public String transferStatus;
    @SerializedName("balance")
    public double balance;
    @SerializedName("totalAssets")
    public double totalAssets;
    @SerializedName("investmentMoney")
    public double investmentMoney;
    @SerializedName("historicalProfit")
    public double historicalProfit;
    @SerializedName("frozen")
    public double frozen;
    @SerializedName("dueInProfit")
    public double dueInProfit;
    @SerializedName("investStatus")
    public String investStatus;
    @SerializedName("accumulatedInvestment")
    public double accumulatedInvestment;
    @SerializedName("availableBalance")
    public double availableBalance;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AccountSummaryInfo{");
        sb.append("transferStatus='").append(transferStatus).append('\'');
        sb.append(", balance=").append(balance);
        sb.append(", totalAssets=").append(totalAssets);
        sb.append(", investmentMoney=").append(investmentMoney);
        sb.append(", historicalProfit=").append(historicalProfit);
        sb.append(", frozen=").append(frozen);
        sb.append(", dueInProfit=").append(dueInProfit);
        sb.append(", investStatus='").append(investStatus).append('\'');
        sb.append(", accumulatedInvestment=").append(accumulatedInvestment);
        sb.append(", availableBalance=").append(availableBalance);
        sb.append('}');
        return sb.toString();
    }
}
