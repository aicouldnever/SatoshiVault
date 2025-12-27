package com.wallet.app.service;

import com.wallet.app.model.WalletModel;

public class MockWalletService implements WalletService {
    private static final String MOCK_ADDRESS = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh";
    private static final double MOCK_BALANCE_BTC = 0.15234567;
    private static final double MOCK_BTC_TO_USD = 42500.00;

    @Override
    public String getAddress() {
        return MOCK_ADDRESS;
    }

    @Override
    public double getBalance() {
        return MOCK_BALANCE_BTC;
    }

    @Override
    public WalletModel getWallet() {
        return new WalletModel(
            MOCK_ADDRESS,
            MOCK_BALANCE_BTC,
            MOCK_BALANCE_BTC * MOCK_BTC_TO_USD,
            false
        );
    }
}
