package com.example.api_wallet.controllers;

import com.example.api_wallet.dao.WalletRepository;
import com.example.api_wallet.dto.Request;
import com.example.api_wallet.exceptions.NoSuchWalletException;
import com.example.api_wallet.models.Wallet;
import com.example.api_wallet.services.OperationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
public class WalletController {
    private final WalletRepository walletRepository;
    private final OperationService operationService;

    public WalletController(WalletRepository walletRepository, OperationService operationService) {
        this.walletRepository = walletRepository;
        this.operationService = operationService;
    }

    @GetMapping(value = "/wallets/{WALLET_UUID}")
    public ResponseEntity<String> getBalance(@PathVariable(name = "WALLET_UUID") UUID id) {
        Optional<Wallet> wallet = walletRepository.findById(id);
        return wallet.map(value -> ResponseEntity.ok().body(String.format("Account balance %s equal %d",
                        value.getId(), value.getAccount().longValue())))
                .orElseGet(() -> ResponseEntity.badRequest().body("No such account exists"));
    }
    /*@GetMapping(value = "/wallets/{WALLET_UUID}")
    public ResponseEntity<?> getBalance(@PathVariable(name = "WALLET_UUID") UUID id)
    throws NoSuchWalletException {
        Optional<Wallet> wallet = walletRepository.findById(id);
        return ResponseEntity.ok().body(wallet.orElseThrow(() -> new NoSuchWalletException("No such account exists")));
    }*/

    @PostMapping(value = "/wallet/new")
    public ResponseEntity<String> newWallet(@RequestBody Request request) {
        if (request.getAmount().longValue() <= 0) {
            return ResponseEntity.badRequest().body("Cannot create a wallet with zero balance");
        }
        Wallet wallet = new Wallet(UUID.randomUUID(), request.getAmount());
        walletRepository.save(wallet);
        return ResponseEntity.ok().body(String.format("Your id number: %s", wallet.getId()));
    }

    @PostMapping(value = "/wallet")
    public ResponseEntity<String> operationsWallet(@RequestBody Request request) {
        Optional<Wallet> wallet = walletRepository.findById(request.getWalletId());
        if (wallet.isPresent()) {
            String operationType = request.getOperationType();
            if (operationType.equals("DEPOSIT")) {
                boolean isDeposit = operationService.deposit(wallet.get(), request.getAmount());
                if (isDeposit) {
                    return ResponseEntity.ok().body(String.format("Successfully credited %d", request.getAmount().longValue()));
                }
                return ResponseEntity.badRequest().body("You did not specify the replenishment amount or the amount is not a number");
            } else if (operationType.equals("WITHDRAW")) {
                boolean isWithdraw = operationService.withdraw(wallet.get(), request.getAmount());
                if (isWithdraw) {
                    return ResponseEntity.ok().body(String.format("Account balance %s equal %d", wallet.get().getId(),
                            wallet.get().getAccount().longValue()));
                }
                return ResponseEntity.badRequest().body("Insufficient funds or you did not indicate the withdrawal amount");
            } else {
                return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Unknown operation type");
            }
        }
        return ResponseEntity.badRequest().body("No such account exists");
    }
}
