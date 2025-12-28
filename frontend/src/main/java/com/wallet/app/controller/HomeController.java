package com.wallet.app.controller;

import com.wallet.app.model.TransactionModel;
import com.wallet.app.model.WalletModel;
import com.wallet.app.service.TransactionService;
import com.wallet.app.service.TransactionServiceImpl;
import com.wallet.app.service.WalletService;
import com.wallet.app.service.WalletServiceImpl;
import com.wallet.app.util.CurrencyFormatter;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

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
    private ListView<TransactionModel> recentTransactionsList;
    
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
        walletService = new WalletServiceImpl();
        transactionService = new TransactionServiceImpl();
        
        // Setup custom cell factory for transactions
        setupRecentTransactionsCellFactory();
        
        loadWalletData();
        loadRecentTransactions();
        updateChart("1D"); // Default to 1 Day
    }
    
    private void setupRecentTransactionsCellFactory() {
        recentTransactionsList.setCellFactory(listView -> new ListCell<TransactionModel>() {
            @Override
            protected void updateItem(TransactionModel tx, boolean empty) {
                super.updateItem(tx, empty);
                
                if (empty || tx == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("transaction-cell");
                } else {
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    // Status icon
                    Label statusIcon = new Label(getStatusIcon(tx.getStatus()));
                    statusIcon.getStyleClass().add("status-icon");
                    statusIcon.setMinWidth(20);
                    
                    // Status
                    Label statusLabel = new Label(tx.getStatus().toString());
                    statusLabel.setMinWidth(80);
                    
                    // Amount
                    Label amountLabel = new Label(CurrencyFormatter.formatBTCAmount(tx.getAmount()));
                    amountLabel.getStyleClass().addAll("amount", tx.getStatus().toString().toLowerCase());
                    amountLabel.setMinWidth(120);
                    
                    // Date
                    Label dateLabel = new Label(tx.getDate().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
                    dateLabel.getStyleClass().add("date");
                    HBox.setHgrow(dateLabel, Priority.ALWAYS);
                    dateLabel.setMaxWidth(Double.MAX_VALUE);
                    dateLabel.setAlignment(Pos.CENTER_RIGHT);
                    
                    container.getChildren().addAll(statusIcon, statusLabel, amountLabel, dateLabel);
                    setGraphic(container);
                    getStyleClass().add("transaction-cell");
                }
            }
        });
    }
    
    private String getStatusIcon(TransactionModel.Status status) {
        switch (status) {
            case RECEIVED: return "↓";
            case SENT: return "↑";
            case PENDING: return "⏳";
            default: return "•";
        }
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
        
        // Show only last 5 transactions
        int count = Math.min(5, transactions.size());
        for (int i = 0; i < count; i++) {
            recentTransactionsList.getItems().add(transactions.get(i));
        }
    }
    
    private void updateChart(String timeFilter) {
        balanceChart.getData().clear();
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Balance");
        
        try {
            // Fetch all transactions to build balance history
            List<TransactionModel> allTransactions = transactionService.getTransactions();
            
            if (allTransactions.isEmpty()) {
                // If no transactions, show current balance as flat line
                double currentBalance = wallet.getBalanceBTC();
                series.getData().add(new XYChart.Data<>(0, currentBalance));
                series.getData().add(new XYChart.Data<>(1, currentBalance));
            } else {
                // Sort transactions by date (oldest first)
                allTransactions.sort((a, b) -> a.getDate().compareTo(b.getDate()));
                
                // Calculate balance at each transaction point
                double runningBalance = 0;
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime startTime;
                
                // Determine time range based on filter
                switch (timeFilter) {
                    case "1H":
                        startTime = now.minusHours(1);
                        xAxis.setLabel("Minutes ago");
                        break;
                    case "1D":
                        startTime = now.minusDays(1);
                        xAxis.setLabel("Hours ago");
                        break;
                    case "1W":
                        startTime = now.minusWeeks(1);
                        xAxis.setLabel("Days ago");
                        break;
                    case "1M":
                        startTime = now.minusMonths(1);
                        xAxis.setLabel("Days ago");
                        break;
                    case "1Y":
                        startTime = now.minusYears(1);
                        xAxis.setLabel("Months ago");
                        break;
                    default: // "ALL"
                        startTime = allTransactions.get(0).getDate();
                        xAxis.setLabel("Time");
                        break;
                }
                
                // Filter transactions within time range
                List<TransactionModel> filteredTxs = new java.util.ArrayList<>();
                for (TransactionModel tx : allTransactions) {
                    if (tx.getDate().isAfter(startTime) || timeFilter.equals("ALL")) {
                        filteredTxs.add(tx);
                    }
                }
                
                // Calculate starting balance (before filtered transactions)
                for (TransactionModel tx : allTransactions) {
                    if (tx.getDate().isBefore(startTime) || tx.getDate().isEqual(startTime)) {
                        if (tx.getStatus() == TransactionModel.Status.RECEIVED) {
                            runningBalance += tx.getAmount();
                        } else if (tx.getStatus() == TransactionModel.Status.SENT) {
                            runningBalance -= tx.getAmount();
                        }
                    }
                }
                
                // Add starting point
                series.getData().add(new XYChart.Data<>(0, runningBalance));
                
                // Add data points for each transaction
                int dataPointIndex = 1;
                for (TransactionModel tx : filteredTxs) {
                    if (tx.getStatus() == TransactionModel.Status.RECEIVED) {
                        runningBalance += tx.getAmount();
                    } else if (tx.getStatus() == TransactionModel.Status.SENT) {
                        runningBalance -= tx.getAmount();
                    }
                    series.getData().add(new XYChart.Data<>(dataPointIndex++, runningBalance));
                }
                
                // If no transactions in range, show flat line at starting balance
                if (filteredTxs.isEmpty()) {
                    series.getData().add(new XYChart.Data<>(1, runningBalance));
                }
            }
        } catch (Exception e) {
            // Fallback to showing current balance as flat line
            double currentBalance = wallet.getBalanceBTC();
            series.getData().add(new XYChart.Data<>(0, currentBalance));
            series.getData().add(new XYChart.Data<>(1, currentBalance));
        }
        
        balanceChart.getData().add(series);
        updateActiveFilter(timeFilter);
    }
    
    private void updateActiveFilter(String activeFilter) {
        filter1H.getStyleClass().remove("active");
        filter1D.getStyleClass().remove("active");
        filter1W.getStyleClass().remove("active");
        filter1M.getStyleClass().remove("active");
        filter1Y.getStyleClass().remove("active");
        filterAll.getStyleClass().remove("active");
        
        switch (activeFilter) {
            case "1H": filter1H.getStyleClass().add("active"); break;
            case "1D": filter1D.getStyleClass().add("active"); break;
            case "1W": filter1W.getStyleClass().add("active"); break;
            case "1M": filter1M.getStyleClass().add("active"); break;
            case "1Y": filter1Y.getStyleClass().add("active"); break;
            case "ALL": filterAll.getStyleClass().add("active"); break;
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
