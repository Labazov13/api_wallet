package com.example.api_wallet.services;

import com.example.api_wallet.models.Wallet;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Lock;

import java.math.BigDecimal;

public interface ChangeAmount {
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    boolean deposit(Wallet wallet, BigDecimal amount);
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    boolean withdraw(Wallet wallet, BigDecimal amount);
}
