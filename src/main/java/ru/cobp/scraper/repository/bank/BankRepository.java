package ru.cobp.scraper.repository.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.cobp.scraper.model.bank.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long>, QuerydslPredicateExecutor<Bank> {
}
