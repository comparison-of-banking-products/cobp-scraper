package ru.cobp.scraper.scraper;

public enum GazprombankDepositCapitalization {

    WITHOUT_CAPITALIZATION("Без капитализации"),
    WITH_CAPITALIZATION("С капитализацией");

    public final String type;

    GazprombankDepositCapitalization(String type) {
        this.type = type;
    }

}
