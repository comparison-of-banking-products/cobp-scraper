package ru.cobp.scraper.repository.deposit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.cobp.scraper.model.deposit.ScrapedDeposit;

@Repository
public interface ScrapedDepositRepository extends JpaRepository<ScrapedDeposit, Long>, QuerydslPredicateExecutor<ScrapedDeposit> {
}
