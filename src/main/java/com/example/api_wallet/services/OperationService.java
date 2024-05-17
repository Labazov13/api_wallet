package com.example.api_wallet.services;

import com.example.api_wallet.dao.WalletRepository;
import com.example.api_wallet.models.Wallet;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OperationService {
    private final WalletRepository walletRepository;

    public OperationService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public boolean deposit(Wallet wallet, BigDecimal amount) {
        if (amount.longValue() <= 0) {
            return false;
        }
        long newBalance = wallet.getAccount().longValue() + amount.longValue();
        wallet.setAccount(new BigDecimal(newBalance));
        walletRepository.save(wallet);
        return true;
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public boolean withdraw(Wallet wallet, BigDecimal amount) {
        long currentBalance = wallet.getAccount().longValue();
        if (wallet.getAccount().longValue() >= amount.longValue() && amount.longValue() != 0) {
            wallet.setAccount(new BigDecimal(currentBalance - amount.longValue()));
            walletRepository.save(wallet);
            return true;
        }
        return false;
    }
}






