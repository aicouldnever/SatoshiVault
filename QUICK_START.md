# SatoshiVault - Quick Start Guide

## 🚀 Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Internet connection (for Bitcoin Testnet API)

---

### Step 1: Start the Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

**Expected Output:**
```
Started DemoApplication in X.XXX seconds
```

The backend will be available at: `http://localhost:8080`

**Test Backend Health:**
```bash
curl http://localhost:8080/api/health
```

Should return: `{"status":"UP","timestamp":"..."}`

---

### Step 2: Start the Frontend

Open a **new terminal** (keep backend running):

```bash
cd frontend
mvn clean install
mvn javafx:run
```

**Expected Output:**
- JavaFX application window opens
- Login screen appears
- Toast notification: "Backend is ready"

---

## 📱 Using the Application

### 1. **Login**
- Enter any passphrase (e.g., `my-secure-passphrase-123`)
- Click **Unlock Wallet**
- Main wallet interface loads

---

### 2. **Set Private Key** (Required for Sending Transactions)

**Navigate to Settings:**
1. Click **Settings** button at bottom
2. Scroll to **Private Key Management** section

**Option A - Generate New Keypair:**
1. Click **Generate New Keypair**
2. Private key will appear in field
3. Click **Set Private Key**
4. Status shows: ✓ Private key is set

**Option B - Use Existing Key:**
1. Paste your private key (WIF format)
2. Click **Set Private Key**
3. Derived address appears for verification

---

### 3. **Receive Bitcoin**

**Navigate to Receive:**
1. Click **Receive** button at bottom
2. QR code displays your wallet address
3. Click **Copy Address** to copy to clipboard
4. Share address with sender

---

### 4. **Send Bitcoin** (Requires Testnet3 BTC)

**Navigate to Send:**
1. Click **Send** button at bottom
2. **Recipient**: Enter destination address
   - OR click **Scan QR** to upload QR code image
3. **Amount**: Enter BTC amount (e.g., `0.001`)
   - OR click **Max** to send all available
4. Review dynamic network fee
5. Click **Review Transaction**
6. Confirm in dialog
7. Wait for "Transaction Sent" toast
8. Wait for "Confirmed" toast (30-50 seconds)

---

### 5. **View History**

**Navigate to History:**
1. Click **History** button at bottom
2. View all transactions
3. Use search bar to filter by amount/address
4. Use date picker to filter by date
5. Click filter buttons: All / Sent / Received / Pending
6. Double-click transaction for details

---

## 💰 Getting Testnet Bitcoin

To test sending transactions, you need Testnet3 BTC:

### **Testnet Faucets** (Free)

1. **Bitcoin Testnet Faucet**
   - https://testnet-faucet.com/btc-testnet/
   - Enter your wallet address
   - Receive 0.01 BTC (testnet)

2. **Coinfaucet**
   - https://coinfaucet.eu/en/btc-testnet/
   - Enter your wallet address
   - Solve CAPTCHA
   - Receive testnet BTC

3. **Bitcoinfaucet.uo1.net**
   - https://bitcoinfaucet.uo1.net/
   - Enter your wallet address
   - Get testnet coins

**Steps:**
1. Go to **Receive** page in app
2. Copy your wallet address
3. Visit faucet website
4. Paste address and request testnet BTC
5. Wait 10-20 minutes for confirmation
6. Check balance in **Home** page

---

## 🔧 Troubleshooting

### **Backend Won't Start**

**Error: Port 8080 already in use**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID)
taskkill /PID <PID> /F
```

**Error: Cannot resolve dependencies**
```bash
cd backend
mvn clean install -U
```

---

### **Frontend Won't Start**

**Error: JavaFX runtime components missing**
- Ensure Java 17+ is installed
- Verify `JAVA_HOME` environment variable

**Error: Maven dependency issues**
```bash
cd frontend
mvn clean install -U
```

---

### **"Cannot Connect to Backend" Toast**

1. Verify backend is running:
   ```bash
   curl http://localhost:8080/api/health
   ```

2. Check backend console for errors

3. Restart backend:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. Click **Retry** button in toast notification

---

### **"Private Key Required" Warning**

1. Go to **Settings** page
2. Either:
   - Click **Generate New Keypair** + **Set Private Key**
   - OR paste existing key + **Set Private Key**
3. Verify status shows ✓

---

### **Transaction Fails to Send**

**Check:**
1. Private key is set (Settings page)
2. Sufficient balance (check Home page)
3. Valid recipient address (Bitcoin testnet format)
4. Backend is running and connected
5. Check backend logs for errors

---

### **QR Code Not Displaying**

1. Verify dependencies loaded:
   ```bash
   cd frontend
   mvn clean install
   ```

2. Check console for ZXing errors

3. Restart application

---

## 🎨 Theme Switching

**Light Theme:**
1. Go to **Settings**
2. Uncheck **Dark Theme** checkbox
3. Theme changes instantly

**Dark Theme (Bitcoin Orange):**
1. Go to **Settings**
2. Check **Dark Theme** checkbox
3. Theme changes instantly

Preference is saved and persists across sessions.

---

## 🔐 Security Best Practices

### **1. Private Key Storage**
- ⚠️ Private key stored **in-memory only**
- 🔒 Lost when app closes (by design)
- ✅ Must be re-entered each session
- ❌ Never shared or logged

### **2. Testnet Only**
- ⚠️ This wallet uses **Bitcoin Testnet**
- 💰 Testnet coins have **no real value**
- 🚫 Do NOT use mainnet private keys
- ✅ Safe for testing and development

### **3. Passphrase Security**
- 🔑 Passphrase generates wallet address
- 💾 Stored in-memory only
- ✅ Choose unique, strong passphrase
- 📝 Write it down securely

---

## 📊 Application Features

| Feature | Status |
|---------|--------|
| ✅ Backend-Frontend Integration | Complete |
| ✅ JWT Authentication | Complete |
| ✅ Real-time Balance Display | Complete |
| ✅ Transaction History | Complete |
| ✅ Send Bitcoin (Testnet) | Complete |
| ✅ Receive Bitcoin | Complete |
| ✅ QR Code Generation | Complete |
| ✅ QR Code Scanning | Complete |
| ✅ Private Key Management | Complete |
| ✅ Dynamic Fee Estimation | Complete |
| ✅ Transaction Polling/Confirmation | Complete |
| ✅ Toast Notifications | Complete |
| ✅ Error Retry Functionality | Complete |
| ✅ Dark/Light Theme | Complete |
| ✅ In-Memory Security | Complete |

---

## 📞 Support

If you encounter issues:

1. **Check Backend Logs** (`backend` terminal)
2. **Check Frontend Console** (JavaFX console output)
3. **Verify API Health**: `curl http://localhost:8080/api/health`
4. **Review** [INTEGRATION_SUMMARY.md](INTEGRATION_SUMMARY.md) for details

---

## 🎉 Success Checklist

- [ ] Backend running on port 8080
- [ ] Frontend application opens
- [ ] "Backend is ready" toast appears
- [ ] Login successful with passphrase
- [ ] Private key generated or set
- [ ] Wallet address displays in Receive page
- [ ] QR code visible in Receive page
- [ ] Balance shows in Home page
- [ ] Transaction history loads
- [ ] Theme toggle works

**All checked?** ✅ You're ready to use SatoshiVault!

---

## 💡 Tips

1. **First Time User?**
   - Generate new keypair in Settings
   - Request testnet BTC from faucet
   - Wait 10-20 minutes for confirmation
   - Test sending small amount back to faucet

2. **Developer Testing?**
   - Use same passphrase for consistent address
   - Set private key in Settings each session
   - Monitor backend logs for API calls
   - Use toast notifications for debugging

3. **Production Deployment?**
   - Change `ConfigManager.DEFAULT_BACKEND_URL`
   - Enable HTTPS for API calls
   - Implement proper key encryption
   - Switch to Bitcoin Mainnet (requires code changes)

---

**Enjoy using SatoshiVault! 🚀**
