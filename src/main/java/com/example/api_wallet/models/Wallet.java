package com.example.api_wallet.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Entity
@Table(name = "wallets")
public class Wallet implements Serializable {
    @Id
    private UUID id;
    private BigDecimal account;

    public Wallet() {
    }

    public Wallet(UUID id, BigDecimal account) {
        this.id = id;
        this.account = account;
    }
    public Wallet(BigDecimal initialBalance) {
        this.account = initialBalance;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAccount() {
        return account;
    }

    public void setAccount(BigDecimal account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id) && Objects.equals(account, wallet.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account);
    }
}
