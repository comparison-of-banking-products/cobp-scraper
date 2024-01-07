package ru.cobp.scraper.service.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cobp.scraper.model.currency.Currency;
import ru.cobp.scraper.model.currency.QCurrency;
import ru.cobp.scraper.repository.currency.CurrencyRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private static final QCurrency Q_CURRENCY = QCurrency.currency1;

    private final CurrencyRepository currencyRepository;

    @Override
    public Currency getByCode(String code) {
        throw new UnsupportedOperationException();
    }

}
