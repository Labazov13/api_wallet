package com.example.api_wallet.advices;

import com.example.api_wallet.dto.Response;
import com.example.api_wallet.exceptions.NoSuchWalletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WalletControllerAdvice {
    @ExceptionHandler(NoSuchWalletException.class)
    public ResponseEntity<Response> handleException(NoSuchWalletException e){
        Response response = new Response(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
