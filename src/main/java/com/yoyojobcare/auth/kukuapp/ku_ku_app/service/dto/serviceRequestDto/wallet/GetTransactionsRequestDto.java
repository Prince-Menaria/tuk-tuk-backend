package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTransactionsRequestDto {
    private int page = 0;
    private int size = 20;
    private String currencyType; // "DIAMONDS", "GOLD", "ALL"
    private String sortBy = "transactionDate";
    private String sortDirection = "DESC";

}
