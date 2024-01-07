package ru.cobp.scraper.scraper;

public enum GazprombankDepositRatesOffer {

    FOR_NEW_CLIENTS("Для новых клиентов"),
    FOR_PREMIUM_CLIENTS("Для премиальных клиентов"),
    FOR_PAYROLL_CLIENTS("Для зарплатных клиентов"),
    FOR_PENSIONERS("Для пенсионеров"),
    FOR_ALL("Для всех");

    public final String type;

    GazprombankDepositRatesOffer(String type) {
        this.type = type;
    }

}
