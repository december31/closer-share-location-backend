package com.example.closersharelocation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.closersharelocation.Model.Account;
import com.example.closersharelocation.dao.AccountRepository;
import com.example.closersharelocation.exception.AccountAlreadyRegisteredException;
import com.example.closersharelocation.exception.EmailNotExistedException;
import com.example.closersharelocation.exception.PasswordIncorrectException;
import com.example.closersharelocation.securiy.Encoder;
import com.example.closersharelocation.utils.Constants;

@Service
public class AccountService {
    private AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public List<Account> getAll() {
        return repository.findAll();
    }

    public Account findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Account login(Account account) throws EmailNotExistedException, PasswordIncorrectException {
        Account savedAccount = repository.findByEmail(account.getEmail());
        if (savedAccount != null) {
            if (Encoder.encode(account.getPassword()).equals(savedAccount.getPassword())) {
                return savedAccount;
            } else {
                throw new PasswordIncorrectException(Constants.PASSWORD_INCORRECT);
            }
        } else {
            throw new EmailNotExistedException(Constants.EMAIL_NOT_REGISTERED);
        }
    }

    public Account register(Account account) throws AccountAlreadyRegisteredException {
        Account savedAccount = repository.findByEmail(account.getEmail());
        if (savedAccount == null) {
            account.setPassword(Encoder.encode(account.getPassword()));
            account.setAvatarUrl(Constants.DEFAULT_IMAGE_URL);
            account.setCreatedTime(System.currentTimeMillis());
            account.setLastModified(System.currentTimeMillis());
            return repository.save(account);
        } else {
            throw new AccountAlreadyRegisteredException(Constants.ACCOUNT_ALREADY_EXISTED);
        }
    }
}