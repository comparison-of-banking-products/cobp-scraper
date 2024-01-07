package ru.cobp.scraper.scraper;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ScrapedRatesRecord(
        String currencyCode,
        Integer amountMin,
        Integer amountMax,
        Map<Integer, Double> termToRate,
        Boolean capitalization,
        Boolean replenishment,
        Boolean partialWithdrawal,
        List<String> extraOptions
) {
}
