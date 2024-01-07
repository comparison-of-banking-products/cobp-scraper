package ru.cobp.scraper.service.deposit;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cobp.scraper.model.bank.Bank;
import ru.cobp.scraper.model.deposit.ScrapedDeposit;
import ru.cobp.scraper.repository.deposit.ScrapedDepositRepository;
import ru.cobp.scraper.scraper.DepositScraper;
import ru.cobp.scraper.scraper.ScrapedDepositRecord;
import ru.cobp.scraper.scraper.ScrapedRatesRecord;
import ru.cobp.scraper.service.bank.BankService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private static final String DEPOSIT_NAME_DELIMITER = ", ";

    private final BankService bankService;

    private final DepositScraper depositScraper;

    private final ScrapedDepositRepository scrapedDepositRepository;

    @Scheduled(fixedDelayString = "${scraper.deposits.update-delay}")
    @Transactional
    void scrapeDeposits() {
        this.scrapedDepositRepository.deleteAllInBatch();

        ScrapedDepositRecord depositRecord = depositScraper.scrapeDeposit();
        Bank bank = bankService.getByName(depositRecord.bankName());

        List<ScrapedDeposit> scrapedDeposits = new ArrayList<>();

        for (ScrapedRatesRecord rateRecord : depositRecord.rates()) {
            for (Integer term : rateRecord.termToRate().keySet()) {
                ScrapedDeposit scrapedDeposit = new ScrapedDeposit();
                scrapedDeposit.setBank(bank);

                String depositName = buildDepositName(depositRecord.bankName(), rateRecord.extraOptions());
                scrapedDeposit.setName(depositName);
                scrapedDeposit.setProductUrl(depositRecord.depositUrl());

                scrapedDeposit.setAmountMin(rateRecord.amountMin());
                scrapedDeposit.setTerm(term);
                scrapedDeposit.setRate(rateRecord.termToRate().get(term));
                scrapedDeposit.setCapitalization(rateRecord.capitalization());

                scrapedDeposits.add(scrapedDeposit);
            }
        }

        this.scrapedDepositRepository.saveAll(scrapedDeposits);
    }

    private String buildDepositName(String depositName, List<String> extraOptions) {
        return depositName + DEPOSIT_NAME_DELIMITER + String.join(DEPOSIT_NAME_DELIMITER, extraOptions);
    }

}
