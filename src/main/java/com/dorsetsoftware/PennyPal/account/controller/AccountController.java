package com.dorsetsoftware.PennyPal.account.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dorsetsoftware.PennyPal.account.dto.AccountCreateDto;
import com.dorsetsoftware.PennyPal.account.dto.AccountDto;
import com.dorsetsoftware.PennyPal.account.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(accountService.getAccountsForUser(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody AccountCreateDto dto
    ) {
        return ResponseEntity.ok(accountService.createAccount(dto, userDetails.getUsername()));
    }
}
