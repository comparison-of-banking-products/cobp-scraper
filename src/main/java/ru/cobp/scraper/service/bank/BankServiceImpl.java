package ru.cobp.scraper.service.bank;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cobp.scraper.exception.ExceptionUtil;
import ru.cobp.scraper.model.bank.Bank;
import ru.cobp.scraper.model.bank.QBank;
import ru.cobp.scraper.repository.bank.BankRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private static final QBank Q_BANK = QBank.bank;

    private final BankRepository bankRepository;

    @Override
    public Bank getByName(String name) {
        Predicate p = buildQBankPredicateBy(name);
        return bankRepository
                .findOne(p)
                .orElseThrow(() -> ExceptionUtil.getBankNotFoundException(name));
    }

    private Predicate buildQBankPredicateBy(String name) {
        return new BooleanBuilder()
                .and(Q_BANK.name.eq(name));
    }

}
