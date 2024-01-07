package ru.cobp.scraper.scraper;

import lombok.Builder;

import java.util.List;

@Builder
public record ScrapedDepositRecord(
        String bankName,
        String depositName,
        String depositUrl,
        String currencyCode,
        Integer amountMin,
        Integer amountMax,
        List<ScrapedRatesRecord> rates
) {
}
