package com.example.api_wallet.dao;

import com.example.api_wallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findById(UUID uuid);

}
