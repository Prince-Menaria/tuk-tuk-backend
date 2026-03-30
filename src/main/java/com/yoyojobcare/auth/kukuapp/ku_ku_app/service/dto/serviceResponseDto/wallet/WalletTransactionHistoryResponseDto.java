package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.wallet;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionHistoryResponseDto {
    private Integer currentPage;
    private Integer pageSize;
    private Long totalTransactions;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private List<TransactionResponseDto> transactions;

}
