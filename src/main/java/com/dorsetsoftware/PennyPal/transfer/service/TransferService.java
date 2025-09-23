package com.dorsetsoftware.PennyPal.transfer.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dorsetsoftware.PennyPal.account.entity.Account;
import com.dorsetsoftware.PennyPal.account.repository.AccountRepository;
import com.dorsetsoftware.PennyPal.accountvalue.service.AccountValueService;
import com.dorsetsoftware.PennyPal.transfer.dto.TransferCreateDto;
import com.dorsetsoftware.PennyPal.transfer.dto.TransferDto;
import com.dorsetsoftware.PennyPal.transfer.dto.TransferUpdateDto;
import com.dorsetsoftware.PennyPal.transfer.entity.Transfer;
import com.dorsetsoftware.PennyPal.transfer.mapper.TransferMapper;
import com.dorsetsoftware.PennyPal.transfer.repository.TransferRepository;
import com.dorsetsoftware.PennyPal.user.entity.User;
import com.dorsetsoftware.PennyPal.user.repository.UserRepository;

@Service
public class TransferService {
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountValueService accountValueService;
    @Autowired
    private UserRepository userRepository;

    public Page<TransferDto> getTransfers(
            String username,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        User user = userRepository.findByUsername(username);
        Page<Transfer> transfers = transferRepository.findByUserAndOptionalDateRange(
                user,
                startDate,
                endDate,
                pageable);

        return transfers.map(TransferMapper::toDto);
    }

    @Transactional
    public TransferDto createTransfer(TransferCreateDto dto, String username) {
        User user = userRepository.findByUsername(username);
        Account accountFrom = accountRepository.findById(dto.getAccountFromId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountFrom.setBalance(dto.getAmount().negate());
        Account accountTo = accountRepository.findById(dto.getAccountToId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountTo.setBalance(dto.getAmount());
        accountValueService.snapshotAllAccounts(user.getId(), dto.getDate());
        Transfer transfer = new Transfer(dto.getAmount(), dto.getDate(), accountFrom, accountTo, user);

        return TransferMapper.toDto(transferRepository.save(transfer));
    }

    @Transactional
    public TransferDto updateTransfer(Long transferId, TransferUpdateDto dto) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));
        
        if (dto.getAccountFromId() != transfer.getAccountFrom().getId()) {
            transfer.getAccountFrom().setBalance(transfer.getAmount());
            Account newAccountFrom = accountRepository.findById(dto.getAccountFromId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
            newAccountFrom.setBalance(dto.getAmount().negate());
        } else if (transfer.getAmount().compareTo(dto.getAmount()) != 0) {
            transfer.getAccountFrom().setBalance(transfer.getAmount().subtract(dto.getAmount()));
        }

        if (dto.getAccountToId() != transfer.getAccountTo().getId()) {
            transfer.getAccountTo().setBalance(transfer.getAmount().negate());
            Account newAccountTo = accountRepository.findById(dto.getAccountToId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
            newAccountTo.setBalance(dto.getAmount());
        } else if (transfer.getAmount().compareTo(dto.getAmount()) != 0) {
            transfer.getAccountTo().setBalance(dto.getAmount().subtract(transfer.getAmount()));
        }

        accountValueService.snapshotAllAccounts(transfer.getUser().getId(), dto.getDate());
        transfer.setAmount(dto.getAmount());
        transfer.setDate(dto.getDate());

        return TransferMapper.toDto(transfer);
    }
}
