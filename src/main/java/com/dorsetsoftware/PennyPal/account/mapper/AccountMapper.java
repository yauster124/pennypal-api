package com.dorsetsoftware.PennyPal.account.mapper;

import java.math.BigDecimal;

import com.dorsetsoftware.PennyPal.account.dto.AccountDto;
import com.dorsetsoftware.PennyPal.account.dto.AccountSummaryDto;
import com.dorsetsoftware.PennyPal.account.entity.Account;

public class AccountMapper {
    public static AccountDto toDto(Account account, BigDecimal balance) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setBalance(balance);

        return dto;
    }

    public static AccountSummaryDto toSummaryDto(Account account) {
        AccountSummaryDto dto = new AccountSummaryDto();
        dto.setId(account.getId());
        dto.setName(account.getName());

        return dto;
    }
}
