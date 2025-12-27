package com.wallet.app.controller;

import com.wallet.app.model.TransactionModel;
import com.wallet.app.model.WalletModel;
import com.wallet.app.service.MockTransactionService;
import com.wallet.app.service.MockWalletService;
import com.wallet.app.service.TransactionService;
import com.wallet.app.service.WalletService;
import com.wallet.app.util.CurrencyFormatter;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeController {
    @FXML
    private Label addressLabel;
    
    @FXML
    private Label balanceLabel;
    
    @FXML
    private Label balanceUSDLabel;
    
    @FXML
    private Button toggleBalanceButton;
    
    @FXML
    private LineChart<Number, Number> balanceChart;
    
    @FXML
    private NumberAxis xAxis;
    
    @FXML
    private NumberAxis yAxis;
    
    @FXML
    private ListView<String> recentTransactionsList;
    
    @FXML
    private Button filter1H;
    @FXML
    private Button filter1D;
    @FXML
    private Button filter1W;
    @FXML
    private Button filter1M;
    @FXML
    private Button filter1Y;
    @FXML
    private Button filterAll;
    
    private WalletService walletService;
    private TransactionService transactionService;
    private WalletModel wallet;
    private boolean isBalanceHidden = false;
    private NavigationController navigationController;
    
    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
    }
    
    @FXML
    public void initialize() {
        walletService = new MockWalletService();
        transactionService = new MockTransactionService();
        
        loadWalletData();
        loadRecentTransactions();
        updateChart("1D"); // Default to 1 Day
    }
    
    private void loadWalletData() {
        wallet = walletService.getWallet();
        addressLabel.setText(wallet.getAddress());
        updateBalanceDisplay();
    }
    
    private void updateBalanceDisplay() {
        if (isBalanceHidden) {
            balanceLabel.setText("****");
            balanceUSDLabel.setText("****");
        } else {
            balanceLabel.setText(CurrencyFormatter.formatBTC(wallet.getBalanceBTC()));
            balanceUSDLabel.setText("≈ " + CurrencyFormatter.formatUSD(wallet.getBalanceUSD()));
        }
    }
    
    private void loadRecentTransactions() {
        List<TransactionModel> transactions = transactionService.getTransactions();
        recentTransactionsList.getItems().clear();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
        
        // Show only last 5 transactions
        int count = Math.min(5, transactions.size());
        for (int i = 0; i < count; i++) {
            TransactionModel tx = transactions.get(i);
            String status = tx.getStatus().toString();
            String amount = CurrencyFormatter.formatBTCAmount(tx.getAmount());
            String date = tx.getDate().format(formatter);
            String item = String.format("%s | %s | %s", status, amount, date);
            recentTransactionsList.getItems().add(item);
        }
    }
    
    private void updateChart(String timeFilter) {
        balanceChart.getData().clear();
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Balance");
        
        // Static predefined data points for each time filter
        switch (timeFilter) {
            case "1H":
                series.getData().add(new XYChart.Data<>(0, 0.1520));
                series.getData().add(new XYChart.Data<>(10, 0.1521));
                series.getData().add(new XYChart.Data<>(20, 0.1522));
                series.getData().add(new XYChart.Data<>(30, 0.1523));
                series.getData().add(new XYChart.Data<>(40, 0.1523));
                series.getData().add(new XYChart.Data<>(50, 0.1524));
                series.getData().add(new XYChart.Data<>(60, 0.1523));
                xAxis.setLabel("Minutes");
                break;
            case "1D":
                series.getData().add(new XYChart.Data<>(0, 0.1500));
                series.getData().add(new XYChart.Data<>(4, 0.1505));
                series.getData().add(new XYChart.Data<>(8, 0.1510));
                series.getData().add(new XYChart.Data<>(12, 0.1515));
                series.getData().add(new XYChart.Data<>(16, 0.1520));
                series.getData().add(new XYChart.Data<>(20, 0.1523));
                series.getData().add(new XYChart.Data<>(24, 0.1523));
                xAxis.setLabel("Hours");
                break;
            case "1W":
                series.getData().add(new XYChart.Data<>(0, 0.1200));
                series.getData().add(new XYChart.Data<>(1, 0.1250));
                series.getData().add(new XYChart.Data<>(2, 0.1300));
                series.getData().add(new XYChart.Data<>(3, 0.1350));
                series.getData().add(new XYChart.Data<>(4, 0.1400));
                series.getData().add(new XYChart.Data<>(5, 0.1450));
                series.getData().add(new XYChart.Data<>(6, 0.1500));
                series.getData().add(new XYChart.Data<>(7, 0.1523));
                xAxis.setLabel("Days");
                break;
            case "1M":
                series.getData().add(new XYChart.Data<>(0, 0.1000));
                series.getData().add(new XYChart.Data<>(5, 0.1100));
                series.getData().add(new XYChart.Data<>(10, 0.1200));
                series.getData().add(new XYChart.Data<>(15, 0.1300));
                series.getData().add(new XYChart.Data<>(20, 0.1400));
                series.getData().add(new XYChart.Data<>(25, 0.1450));
                series.getData().add(new XYChart.Data<>(30, 0.1523));
                xAxis.setLabel("Days");
                break;
            case "1Y":
                series.getData().add(new XYChart.Data<>(0, 0.0500));
                series.getData().add(new XYChart.Data<>(2, 0.0700));
                series.getData().add(new XYChart.Data<>(4, 0.0900));
                series.getData().add(new XYChart.Data<>(6, 0.1100));
                series.getData().add(new XYChart.Data<>(8, 0.1300));
                series.getData().add(new XYChart.Data<>(10, 0.1450));
                series.getData().add(new XYChart.Data<>(12, 0.1523));
                xAxis.setLabel("Months");
                break;
            case "ALL":
                series.getData().add(new XYChart.Data<>(0, 0.0100));
                series.getData().add(new XYChart.Data<>(1, 0.0300));
                series.getData().add(new XYChart.Data<>(2, 0.0600));
                series.getData().add(new XYChart.Data<>(3, 0.0900));
                series.getData().add(new XYChart.Data<>(4, 0.1200));
                series.getData().add(new XYChart.Data<>(5, 0.1523));
                xAxis.setLabel("Years");
                break;
        }
        
        balanceChart.getData().add(series);
        updateActiveFilter(timeFilter);
    }
    
    private void updateActiveFilter(String activeFilter) {
        filter1H.setStyle("");
        filter1D.setStyle("");
        filter1W.setStyle("");
        filter1M.setStyle("");
        filter1Y.setStyle("");
        filterAll.setStyle("");
        
        String activeStyle = "-fx-background-color: #0078d4; -fx-text-fill: white;";
        switch (activeFilter) {
            case "1H": filter1H.setStyle(activeStyle); break;
            case "1D": filter1D.setStyle(activeStyle); break;
            case "1W": filter1W.setStyle(activeStyle); break;
            case "1M": filter1M.setStyle(activeStyle); break;
            case "1Y": filter1Y.setStyle(activeStyle); break;
            case "ALL": filterAll.setStyle(activeStyle); break;
        }
    }
    
    @FXML
    private void handleCopyAddress() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(wallet.getAddress());
        clipboard.setContent(content);
    }
    
    @FXML
    private void handleToggleBalance() {
        isBalanceHidden = !isBalanceHidden;
        toggleBalanceButton.setText(isBalanceHidden ? "Show" : "Hide");
        updateBalanceDisplay();
    }
    
    @FXML
    private void handleFilter1H() {
        updateChart("1H");
    }
    
    @FXML
    private void handleFilter1D() {
        updateChart("1D");
    }
    
    @FXML
    private void handleFilter1W() {
        updateChart("1W");
    }
    
    @FXML
    private void handleFilter1M() {
        updateChart("1M");
    }
    
    @FXML
    private void handleFilter1Y() {
        updateChart("1Y");
    }
    
    @FXML
    private void handleFilterAll() {
        updateChart("ALL");
    }
    
    @FXML
    private void handleSeeAll() {
        if (navigationController != null) {
            navigationController.showHistory();
        }
    }
}
