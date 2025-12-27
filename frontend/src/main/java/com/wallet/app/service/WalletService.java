package com.wallet.app.service;

import com.wallet.app.model.WalletModel;

public interface WalletService {
    String getAddress();
    double getBalance();
    WalletModel getWallet();
}
