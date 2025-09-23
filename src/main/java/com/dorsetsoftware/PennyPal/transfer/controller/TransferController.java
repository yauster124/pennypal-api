package com.dorsetsoftware.PennyPal.transfer.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dorsetsoftware.PennyPal.expense.dto.ExpenseDto;
import com.dorsetsoftware.PennyPal.transfer.dto.TransferCreateDto;
import com.dorsetsoftware.PennyPal.transfer.dto.TransferDto;
import com.dorsetsoftware.PennyPal.transfer.dto.TransferUpdateDto;
import com.dorsetsoftware.PennyPal.transfer.service.TransferService;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    @Autowired
    private TransferService transferService;

    @GetMapping
    public ResponseEntity<Page<TransferDto>> getTransfers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        return ResponseEntity.ok(transferService.getTransfers(userDetails.getUsername(), startDate, endDate, pageable));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TransferDto>> getRecentExpenses(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<TransferDto> transfers = transferService.getRecentTransfersForUser(userDetails.getUsername());

        return ResponseEntity.ok(transfers);
    }

    @PostMapping
    public ResponseEntity<TransferDto> createTransfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TransferCreateDto dto) {
        return ResponseEntity.ok(transferService.createTransfer(dto, userDetails.getUsername()));
    }

    @PutMapping("/{transferId}")
    public ResponseEntity<TransferDto> updateTransfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long transferId,
            @RequestBody TransferUpdateDto dto) {
        return ResponseEntity.ok(transferService.updateTransfer(transferId, dto));
    }
}
