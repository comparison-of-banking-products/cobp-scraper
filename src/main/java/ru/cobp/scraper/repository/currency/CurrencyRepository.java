package ru.cobp.scraper.repository.currency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.cobp.scraper.model.currency.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long>, QuerydslPredicateExecutor<Currency> {
}
