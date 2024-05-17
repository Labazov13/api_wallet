package com.example.api_wallet.controllers;

import com.example.api_wallet.dao.WalletRepository;
import com.example.api_wallet.dto.Request;
import com.example.api_wallet.models.Wallet;
import com.example.api_wallet.services.OperationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.2");

    @Autowired
    MockMvc mockMvc;

    @InjectMocks
    WalletController walletController;
    @Mock
    WalletRepository walletRepository;
    @Mock
    OperationService operationService;


    /*@Test
    void getBalance_ReturnedHTTPStatus_OK() {
        UUID uuid = UUID.randomUUID();
        Wallet wallet = new Wallet(uuid, BigDecimal.TEN);
        when(walletRepository.findById(uuid)).thenReturn(Optional.of(wallet));
        ResponseEntity<String> response = walletController.getBalance(uuid);
        UUID id = UUID.fromString(Objects.requireNonNull(response.getBody()).substring(16, 52));
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id.toString(), response.getBody().substring(16, 52));
    }*/

    /*@Test
    void getBalance_ReturnedHTTPStatus_BAD_REQUEST() {
        UUID uuid = UUID.randomUUID();
        when(walletRepository.findById(uuid)).thenReturn(Optional.empty());
        ResponseEntity<String> response = walletController.getBalance(uuid);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No such account exists", response.getBody());
    }*/

    @Test
    void newWallet_ReturnedHTTPStatus_OK_and_IdWallet() {
        Request request = new Request(new BigDecimal(1000));
        var response = walletController.newWallet(request);
        UUID uuid = UUID.fromString(Objects.requireNonNull(response.getBody()).substring(16, 52));
        Wallet wallet = new Wallet(uuid, request.getAmount());
        verify(this.walletRepository).save(wallet);
        assertNotNull(response);
        assertEquals(wallet.getId().toString(), response.getBody().substring(16, 52));
    }

    @Test
    void newWalletWithZeroBalance_ReturnedHTTPStatus_BAD_REQUEST() {
        Request request = new Request(new BigDecimal(0));
        var response = this.walletController.newWallet(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot create a wallet with zero balance", response.getBody());
    }

    @Test
    void operationsWallet_ReturnedSuccessDepositAndHTTPStatus_OK() {
        UUID uuid = UUID.randomUUID();
        Request request = new Request(uuid, "DEPOSIT", BigDecimal.TEN);
        Wallet wallet = new Wallet(uuid, BigDecimal.TEN);
        when(walletRepository.findById(request.getWalletId())).thenReturn(Optional.of(wallet));
        when(operationService.deposit(wallet, request.getAmount())).thenReturn(true);
        ResponseEntity<String> response = walletController.operationsWallet(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully credited 10", response.getBody());
    }


    @Test
    void operationsWallet_ReturnedFailedDepositAndHTTPStatus_BAD_REQUEST() {
        UUID uuid = UUID.randomUUID();
        Request request = new Request(uuid, "DEPOSIT", BigDecimal.ZERO);
        Wallet wallet = new Wallet(uuid, BigDecimal.TEN);
        when(walletRepository.findById(request.getWalletId())).thenReturn(Optional.of(wallet));
        when(operationService.deposit(wallet, request.getAmount())).thenReturn(false);
        ResponseEntity<String> response = walletController.operationsWallet(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("You did not specify the replenishment amount or the amount is not a number", response.getBody());
    }


    @Test
    void operationsWallet_ReturnedSuccessWithdrawAndHTTPStatus_OK() {
        UUID uuid = UUID.randomUUID();
        Request request = new Request(uuid, "WITHDRAW", BigDecimal.TEN);
        Wallet wallet = new Wallet(uuid, BigDecimal.TEN);
        when(walletRepository.findById(request.getWalletId())).thenReturn(Optional.of(wallet));
        when(operationService.withdraw(wallet, request.getAmount())).thenReturn(true);
        ResponseEntity<String> response = walletController.operationsWallet(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(wallet.getAccount().toString(), Objects.requireNonNull(response.getBody()).substring(59, 61));
        assertEquals(String.format("Account balance %s equal %d", wallet.getId(),
                wallet.getAccount().longValue()), response.getBody());
    }


    @Test
    void operationsWallet_ReturnedFailedWithdrawAndHTTPStatus_BAD_REQUEST() {
        UUID uuid = UUID.randomUUID();
        Request request = new Request(uuid, "WITHDRAW", BigDecimal.ZERO);
        Wallet wallet = new Wallet(uuid, BigDecimal.TEN);
        when(walletRepository.findById(request.getWalletId())).thenReturn(Optional.of(wallet));
        when(operationService.withdraw(wallet, request.getAmount())).thenReturn(false);
        ResponseEntity<String> response = walletController.operationsWallet(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient funds or you did not indicate the withdrawal amount", response.getBody());
    }

    @Test
    void operationsWalletWithUnknownOperationType_ReturnedFailedAndHTTPStatus_I_AM_A_TEAPOT() {
        UUID uuid = UUID.randomUUID();
        Request request = new Request(uuid, "UNKNOWN", BigDecimal.TEN);
        Wallet wallet = new Wallet(uuid, BigDecimal.TEN);
        when(walletRepository.findById(request.getWalletId())).thenReturn(Optional.of(wallet));
        ResponseEntity<String> response = walletController.operationsWallet(request);
        assertEquals(HttpStatus.I_AM_A_TEAPOT, response.getStatusCode());
        assertEquals("Unknown operation type", response.getBody());
    }

    @Test
    void operationsWalletWithoutValidIdAccount_ReturnedFailedAndHTTPStatus_BAD_REQUEST() {
        UUID uuid = UUID.randomUUID();
        Request request = new Request(uuid, "UNKNOWN", BigDecimal.TEN);
        when(walletRepository.findById(request.getWalletId())).thenReturn(Optional.empty());
        ResponseEntity<String> response = walletController.operationsWallet(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No such account exists", response.getBody());
    }
}
