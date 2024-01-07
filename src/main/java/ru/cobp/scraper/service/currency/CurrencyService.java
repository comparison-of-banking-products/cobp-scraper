package ru.cobp.scraper.service.currency;

import ru.cobp.scraper.model.currency.Currency;

public interface CurrencyService {

    Currency getByCode(String code);

}
