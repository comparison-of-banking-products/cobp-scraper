package ru.cobp.scraper.service.bank;

import ru.cobp.scraper.model.bank.Bank;

public interface BankService {

    Bank getByName(String name);

}
