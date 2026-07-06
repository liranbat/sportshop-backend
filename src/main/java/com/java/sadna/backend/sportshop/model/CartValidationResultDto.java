package com.java.sadna.backend.sportshop.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CartValidationResultDto {

    private final boolean ok;
    private final List<VersionMismatchDto> versionMismatches;
    private final List<StockIssueDto> stockIssues;
    private final CartViewDto cart;
}
