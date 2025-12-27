package com.wallet.app.model;

public class WalletModel {
    private String address;
    private double balanceBTC;
    private double balanceUSD;
    private boolean isBalanceHidden;

    public WalletModel() {
    }

    public WalletModel(String address, double balanceBTC, double balanceUSD, boolean isBalanceHidden) {
        this.address = address;
        this.balanceBTC = balanceBTC;
        this.balanceUSD = balanceUSD;
        this.isBalanceHidden = isBalanceHidden;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBalanceBTC() {
        return balanceBTC;
    }

    public void setBalanceBTC(double balanceBTC) {
        this.balanceBTC = balanceBTC;
    }

    public double getBalanceUSD() {
        return balanceUSD;
    }

    public void setBalanceUSD(double balanceUSD) {
        this.balanceUSD = balanceUSD;
    }

    public boolean isBalanceHidden() {
        return isBalanceHidden;
    }

    public void setBalanceHidden(boolean balanceHidden) {
        isBalanceHidden = balanceHidden;
    }
}
