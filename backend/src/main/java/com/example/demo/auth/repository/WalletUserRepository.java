package com.example.demo.auth.repository;

import com.example.demo.auth.model.WalletUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletUserRepository extends JpaRepository<WalletUser, Long> {
    // No custom queries yet
}
