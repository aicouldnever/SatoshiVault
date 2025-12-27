package com.wallet.app.service;

import com.wallet.app.model.TransactionModel;
import com.wallet.app.model.TransactionModel.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockTransactionService implements TransactionService {

    @Override
    public List<TransactionModel> getTransactions() {
        List<TransactionModel> transactions = new ArrayList<>();
        
        // Mock transaction data
        transactions.add(new TransactionModel(
            "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6",
            LocalDateTime.now().minusHours(2),
            0.0025,
            Status.RECEIVED
        ));
        
        transactions.add(new TransactionModel(
            "b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1",
            LocalDateTime.now().minusHours(5),
            -0.0015,
            Status.SENT
        ));
        
        transactions.add(new TransactionModel(
            "c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2",
            LocalDateTime.now().minusDays(1),
            0.0500,
            Status.RECEIVED
        ));
        
        transactions.add(new TransactionModel(
            "d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2c3",
            LocalDateTime.now().minusDays(2),
            -0.0100,
            Status.SENT
        ));
        
        transactions.add(new TransactionModel(
            "e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2c3d4",
            LocalDateTime.now().minusDays(3),
            0.1000,
            Status.RECEIVED
        ));
        
        transactions.add(new TransactionModel(
            "f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2c3d4e5",
            LocalDateTime.now().minusDays(5),
            -0.0200,
            Status.SENT
        ));
        
        transactions.add(new TransactionModel(
            "g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2c3d4e5f6",
            LocalDateTime.now().minusDays(7),
            0.0750,
            Status.RECEIVED
        ));
        
        transactions.add(new TransactionModel(
            "h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2c3d4e5f6g7",
            LocalDateTime.now().minusDays(10),
            -0.0050,
            Status.SENT
        ));
        
        transactions.add(new TransactionModel(
            "i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2c3d4e5f6g7h8",
            LocalDateTime.now().minusDays(15),
            0.0300,
            Status.RECEIVED
        ));
        
        transactions.add(new TransactionModel(
            "j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2c3d4e5f6g7h8i9",
            LocalDateTime.now().minusDays(20),
            -0.0075,
            Status.PENDING
        ));
        
        return transactions;
    }

    @Override
    public boolean sendTransaction(String address, double amount) {
        // Mock implementation - always returns true
        return true;
    }
}
