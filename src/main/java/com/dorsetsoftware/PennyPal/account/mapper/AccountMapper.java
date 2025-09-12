package com.dorsetsoftware.PennyPal.account.mapper;

import com.dorsetsoftware.PennyPal.account.dto.AccountDto;
import com.dorsetsoftware.PennyPal.account.entity.Account;

public class AccountMapper {
    public static AccountDto toDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setBalance(account.getBalance());

        return dto;
    }
}
