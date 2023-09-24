package com.example.closersharelocation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.closersharelocation.Model.Account;
import com.example.closersharelocation.base.BaseResponse;
import com.example.closersharelocation.exception.AccountAlreadyRegisteredException;
import com.example.closersharelocation.exception.EmailNotExistedException;
import com.example.closersharelocation.exception.PasswordIncorrectException;
import com.example.closersharelocation.response.ResponseBuilder;
import com.example.closersharelocation.service.AccountService;
import com.example.closersharelocation.utils.Constants;

@RestController
@RequestMapping("/account")
public class AccountController {
    @GetMapping
    String getAccountInfo(@RequestParam Long id) {
        return "user with id " + id.toString();
    }

    private AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(@RequestBody Account account) {
        BaseResponse<?> response = null;
        ResponseBuilder builder = new ResponseBuilder();
        try {
            Account savedAccount = service.login(account);
            savedAccount.setPassword(null);
            response = builder
                    .withStatusCode(HttpStatus.OK)
                    .withMessage(Constants.LOGIN_SUCCESS)
                    .withData(savedAccount)
                    .build();
        } catch (EmailNotExistedException e) {
            response = builder
                    .withStatusCode(HttpStatus.UNAUTHORIZED)
                    .withMessage(e.getMessage())
                    .build();
        } catch (PasswordIncorrectException e) {
            response = builder
                    .withStatusCode(HttpStatus.UNAUTHORIZED)
                    .withMessage(e.getMessage())
                    .build();
        }
        return new ResponseEntity<>(response, null, response.getStatus());
    }

    @PostMapping("/register")
    public BaseResponse<?> register(@RequestBody Account account) {
        BaseResponse<?> response = null;
        ResponseBuilder builder = new ResponseBuilder();
        try {
            Account savedAccount = service.register(account);
            savedAccount.setPassword(null);
            response = builder
                    .withStatusCode(HttpStatus.OK)
                    .withMessage(Constants.REGISTER_SUCCESS)
                    .withData(savedAccount)
                    .build();
        } catch (AccountAlreadyRegisteredException e) {
            response = builder
                    .withStatusCode(HttpStatus.CONFLICT)
                    .withMessage(e.getMessage())
                    .build();
        }
        return response;
    }

    @GetMapping("/test")
    public String test() {
        return "Hello world";
    }
}
