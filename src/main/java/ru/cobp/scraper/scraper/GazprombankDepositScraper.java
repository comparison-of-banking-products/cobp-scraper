package ru.cobp.scraper.scraper;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class GazprombankDepositScraper implements DepositScraper {

    private static final String GAZPROMBANK_NAME = "Газпромбанк";

    private static final String DEPOSIT_URL = "https://www.gazprombank.ru/personal/increase/deposits/detail/2491/";

    private static final double SLOW_MOTION_DELAY = 2000.0;

    private static final double DAYS_TO_MONTHS_CONVERSION_CONSTANT = 30.41;

    @Override
    public ScrapedDepositRecord scrapeDeposit() {
        log.debug("scrapeDeposit()");

        ScrapedDepositRecord scrapedDepositRecord;

        try (Playwright playwright = Playwright.create()) {
            log.debug("Playwright.create()");

            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setSlowMo(SLOW_MOTION_DELAY);

            try (Browser browser = playwright.chromium().launch(launchOptions)) {
                log.debug("playwright.chromium().launch()");

                BrowserContext context = browser.newContext();
                Page page = context.newPage();

                log.debug("page.setDefaultTimeout(180_000)");
                page.setDefaultTimeout(180_000);

                log.debug("page.navigate(DEPOSIT_URL) [" + DEPOSIT_URL + "]");
                page.navigate(DEPOSIT_URL);

                String depositTitle = scrapeDepositTitle(page);

                List<ScrapedRatesRecord> depositRates = new ArrayList<>();
                depositRates.add(scrapeDepositRates(
                        page,
                        GazprombankDepositRatesOffer.FOR_NEW_CLIENTS,
                        GazprombankDepositCapitalization.WITHOUT_CAPITALIZATION
                ));

                depositRates.add(scrapeDepositRates(
                        page,
                        GazprombankDepositRatesOffer.FOR_NEW_CLIENTS,
                        GazprombankDepositCapitalization.WITH_CAPITALIZATION
                ));

                depositRates.add(scrapeDepositRates(
                        page,
                        GazprombankDepositRatesOffer.FOR_ALL,
                        GazprombankDepositCapitalization.WITHOUT_CAPITALIZATION
                ));

                depositRates.add(scrapeDepositRates(
                        page,
                        GazprombankDepositRatesOffer.FOR_ALL,
                        GazprombankDepositCapitalization.WITH_CAPITALIZATION
                ));

                scrapedDepositRecord = ScrapedDepositRecord.builder()
                        .bankName(GAZPROMBANK_NAME)
                        .depositName(depositTitle)
                        .depositUrl(DEPOSIT_URL)
                        .rates(depositRates)
                        .build();
            }
        }

        return scrapedDepositRecord;
    }

    private String scrapeDepositTitle(Page page) {
        log.debug("scrapeDepositTitle()");

        return page.locator("//div[@class='title_banner_product__info-83e']")
                .filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.HEADING)))
                .innerText()
                .split("\\R")[0];
    }

    private ScrapedRatesRecord scrapeDepositRates(
            Page page,
            GazprombankDepositRatesOffer offerToClient,
            GazprombankDepositCapitalization capitalization
    ) {
        log.debug("scrapeDepositRates()");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Процентные ставки")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(offerToClient.type)).click();

        List<String> rateTableAllInnerText = page.locator("//div[@data-code='rate_table_switcher']")
                .filter(new Locator.FilterOptions().setHas(page.getByText(capitalization.type)))
                .allInnerTexts();

        String rateTableElem = rateTableAllInnerText.stream()
                .filter(Predicate.not(String::isEmpty))
                .findFirst()
                .orElseThrow();

        String[] ratesTable = rateTableElem.split("\\R");

        List<Integer> terms = parseTermsRow(ratesTable[2]);
        List<Double> rates = parseRatesRow(ratesTable[3]);
        Integer amountMin = parseRatesRowAmountMin(ratesTable[3]);

        Map<Integer, Double> termToRate = IntStream.range(0, terms.size())
                .boxed()
                .collect(Collectors.toMap(terms::get, rates::get));

        return ScrapedRatesRecord.builder()
                .amountMin(amountMin)
                .termToRate(termToRate)
                .capitalization(GazprombankDepositCapitalization.WITH_CAPITALIZATION == capitalization)
                .extraOptions(List.of(offerToClient.type))
                .build();
    }

    private List<Integer> parseTermsRow(String termsRow) {
        log.debug("parseTermsRow() [" + termsRow + "]");

        String[] termsRowSplit = termsRow.split("\\t");
        String[] termsCells = Arrays.copyOfRange(termsRowSplit, 1, termsRowSplit.length);
        return getTermsInMonths(parseTermsInDays(termsCells));
    }

    private List<Double> parseRatesRow(String ratesRow) {
        log.debug("parseRatesRow() [" + ratesRow + "]");

        String[] ratesRowSplit = ratesRow.split("\\t");
        String[] ratesCells = Arrays.copyOfRange(ratesRowSplit, 1, ratesRowSplit.length);
        return parseRates(ratesCells);
    }

    private Integer parseRatesRowAmountMin(String ratesRow) {
        log.debug("parseRatesRowAmountMin() [" + ratesRow + "]");

        String[] ratesRowSplit = ratesRow.split("\\t");
        return parseAmountMin(ratesRowSplit[0]);
    }

    private Integer parseAmountMin(String amountMin) {
        log.debug("parseAmountMin() [" + amountMin + "]");

        return amountMin.contains("от") && amountMin.contains("₽")
                ? parseInteger(amountMin)
                : null;
    }

    private List<Integer> parseTermsInDays(String[] termsInDays) {
        log.debug("parseTermsInDays() [" + Arrays.toString(termsInDays) + "]");

        return Arrays.stream(termsInDays)
                .filter(t -> t.contains("день") || t.contains("дней"))
                .map(this::parseTerm)
                .collect(Collectors.toList());
    }

    private List<Integer> getTermsInMonths(List<Integer> termsInDays) {
        log.debug("getTermsInMonths() [" + termsInDays + "]");

        return termsInDays.stream()
                .map(t -> (int) (t / DAYS_TO_MONTHS_CONVERSION_CONSTANT))
                .collect(Collectors.toList());
    }

    private Integer parseTerm(String term) {
        log.debug("parseTerm() [" + term + "]");

        return parseInteger(term);
    }

    private Integer parseInteger(String s) {
        log.debug("parseInteger() [" + s + "]");

        String replaced = s.replaceAll("[^0-9]", "");
        return replaced.isEmpty()
                ? null
                : Integer.parseInt(replaced);
    }

    private List<Double> parseRates(String[] rates) {
        log.debug("parseRates() [" + Arrays.toString(rates) + "]");

        return Arrays.stream(rates)
                .filter(r -> r.contains("%"))
                .map(this::parseRate)
                .collect(Collectors.toList());
    }

    private Double parseRate(String rate) {
        log.debug("parseRate() [" + rate + "]");

        String replaced = rate.replaceAll("[^0-9.]", "");
        return replaced.isEmpty()
                ? null
                : Double.parseDouble(replaced);
    }

}
