package com.dorsetsoftware.PennyPal.accountvalue.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dorsetsoftware.PennyPal.accountvalue.dto.AccountValuePoint;
import com.dorsetsoftware.PennyPal.accountvalue.service.AccountValueService;

@RestController
@RequestMapping("/api/accountvalue")
public class AccountValueController {
    @Autowired
    private AccountValueService accountValueService;

    @GetMapping
    public ResponseEntity<Map<String, List<AccountValuePoint>>> getAccountValues(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return ResponseEntity.ok(accountValueService.getAccountValuesWithTotal(userDetails.getUsername(), startDate));
    }
}
