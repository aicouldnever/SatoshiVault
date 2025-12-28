package com.example.demo.auth.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_users")
public class WalletUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "passphrase_encrypted", nullable = false, columnDefinition = "TEXT")
    private String passphraseEncrypted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public WalletUser() {
    }

    public WalletUser(String passwordHash, String passphraseEncrypted, LocalDateTime createdAt) {
        this.passwordHash = passwordHash;
        this.passphraseEncrypted = passphraseEncrypted;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPassphraseEncrypted() {
        return passphraseEncrypted;
    }

    public void setPassphraseEncrypted(String passphraseEncrypted) {
        this.passphraseEncrypted = passphraseEncrypted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
