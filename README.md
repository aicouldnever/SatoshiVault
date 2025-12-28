# SatoshiVault - Bitcoin Wallet on Testnet

## Project Description

The **SatoshiVault** project involves developing a secure Bitcoin wallet operating on the Testnet network. This choice was motivated by the need to simulate real transactions without committing actual funds, allowing all features to be tested and validated in a safe and controlled environment.

### Key Features

- **Multi-layer Authentication**: Secure registration and login with password and recovery phrase
- **Key Management**: Secure generation and management of private keys and Bitcoin addresses
- **Send and Receive Funds**: Intuitive interface for Bitcoin transactions
- **Transaction History**: Complete tracking of completed transactions
- **Testnet3 Support**: All operations are performed on the Bitcoin Testnet network

## Running the Application

### Prerequisites

- Java 21
- Maven 3.9.11
- Spring Boot 3.2.0
- JavaFX 21.0.4
- Internet connection (for Bitcoin Testnet API)

### Step 1: Start the Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Step 2: Start the Frontend

```bash
cd frontend
mvn clean install
mvn javafx:run
```
