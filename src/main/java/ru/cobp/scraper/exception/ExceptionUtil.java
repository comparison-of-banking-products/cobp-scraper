package ru.cobp.scraper.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtil {

    public static BankNotFoundException getBankNotFoundException(String bankName) {
        return new BankNotFoundException(
                String.format("%s [%s]", ExceptionMessage.BANK_NOT_FOUND, bankName)
        );
    }

}
