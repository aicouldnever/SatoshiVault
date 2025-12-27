package com.wallet.app.service;

import com.wallet.app.model.TransactionModel;
import java.util.List;

public interface TransactionService {
    List<TransactionModel> getTransactions();
    boolean sendTransaction(String address, double amount);
}
