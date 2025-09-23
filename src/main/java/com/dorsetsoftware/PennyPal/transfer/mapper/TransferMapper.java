package com.dorsetsoftware.PennyPal.transfer.mapper;

import com.dorsetsoftware.PennyPal.account.mapper.AccountMapper;
import com.dorsetsoftware.PennyPal.transfer.dto.TransferDto;
import com.dorsetsoftware.PennyPal.transfer.entity.Transfer;

public class TransferMapper {
    public static TransferDto toDto(Transfer transfer) {
        TransferDto dto = new TransferDto();
        dto.setId(transfer.getId());
        dto.setAmount(transfer.getAmount());
        dto.setDate(transfer.getDate());
        dto.setAccountFrom(AccountMapper.toDto(transfer.getAccountFrom()));
        dto.setAccountTo(AccountMapper.toDto(transfer.getAccountTo()));

        return dto;
    }
}
