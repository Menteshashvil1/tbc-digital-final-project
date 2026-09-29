# TBC Digital - Advanced Test Automation

Final Project 2. UI, API, database-driven and network tests for [tbcbank.ge](https://tbcbank.ge),
written with Java 21, Playwright, TestNG, MyBatis and Rest Assured.

| | |
|---|---|
| Site | https://tbcbank.ge (`/en` and `/ka`) |
| APIs | `https://apigw.tbcbank.ge/api/v1/sites/...` (content) and `/api/v1/exchangeRates/...` (rates) |
| Stack | Java 21, Playwright 1.62, TestNG 7.12, Rest Assured 5.5, MyBatis 3.5, H2 2.4, Allure 2.29, Maven |
| Test methods | 11 (TBC-T1 ... TBC-T11) |
| Test runs | 17 (DataProviders expand localization x2, API-to-UI x2 and database scenarios x5) |
| Execution | TestNG `parallel="methods"`, 4 threads, one Playwright instance per thread |
| Time | about 20-30 seconds for the whole suite |

```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## How to run

```bash
mvn test
```

Runs `testng.xml` (API and UI tests in parallel, headless Chromium).

| Goal | Command |
|---|---|
| API tests only | `mvn test -Dsuite=testng-api.xml` |
| UI tests only | `mvn test -Dsuite=testng-ui.xml` |
| Watch the browser | `mvn test -Dheadless=false` |
| Other browser | `mvn test -Dbrowser=firefox` or `-Dbrowser=webkit` |
| Slow motion | `mvn test -Dheadless=false -Dslow.mo=300` |
| Allure report | `mvn allure:serve` after a run |

Every key in `src/main/resources/config.properties` can be overridden with `-Dkey=value`
(`ConfigReader` checks system properties first): `base.url`, `api.url`, `site.id`, `browser`, `headless`,
`slow.mo`, `viewport.width`, `viewport.height`, `default.timeout`, `navigation.timeout`,
`assertion.timeout`, `block.third.party`, `db.url`. Thread count lives in the suite files.

Requirements: JDK 21 and Maven 3.9+. Playwright downloads its browsers on the first run.
No database server is needed: the H2 database is created and seeded from SQL scripts when the first
DataProvider asks for data.

## Project structure

```
src/
  main/java/ge/tbc/testautomation/
    pages/            BasePage, HomePage, ConsumerLoanPage, ConsumerLoanTermsPage, CurrencyExchangePage
    components/       HeaderComponent, LanguageSwitcherComponent, CookieBannerComponent,
                      BreadcrumbsComponent, CtaSectionComponent, CurrencyDropdownComponent
    steps/            NavigationSteps, ConsumerLoanSteps, CurrencyExchangeSteps, CookieConsentSteps,
                      ContentApiSteps, ExchangeRateApiSteps
    api/clients/      BaseApiClient, SiteContentClient, ExchangeRateClient
    api/models/       Site, SitePage, SiteLocaleInfo, ContentPage, Seo, Breadcrumbs, Link,
                      SectionComponent, SectionInputs, RichTextNode, ListItem, Button,
                      ExchangeRate, CommercialRates, CommercialRate, ApiError
    database/         MyBatisSessionFactory, CurrencyConversionRepository
    database/mappers/ CurrencyConversionMapper
    database/models/  CurrencyConversion
    utils/            ConfigReader, PlaywrightManager, DataProviders, LocalizationData,
                      UrlBuilder, Patterns, AmountParser, JsonMapper
    constants/        SiteLocale, PageSlugs, Endpoints, TestConstants
  main/resources/
    config.properties
    mybatis/          mybatis-config.xml, mappers/CurrencyConversionMapper.xml
    testdata/         schema.sql, data.sql, localization/en.properties, localization/ka.properties
  test/java/ge/tbc/testautomation/tests/
    BaseUiTest
    ui/               CookieConsentTest, LocalizationTest, CurrencyConversionDataDrivenTest,
                      CurrencyNetworkTest, CurrencyCalculatorTest, MegaMenuNavigationTest,
                      ConsumerLoanApiUiConsistencyTest
    api/              SiteContentApiTest, ExchangeRateApiTest
pom.xml, testng.xml, testng-api.xml, testng-ui.xml
```

## Test inventory

| Zephyr | Test | What it proves |
|---|---|---|
| TBC-T1 | `CookieConsentTest` | Reject All hides the banner, the `tbc-ge-cookie-consent-v1` cookie stores `reject-all`, the banner stays away after reload and on another page |
| TBC-T2 | `LocalizationTest` (en, ka) | Switch language from the other locale, go through the mega menu to Consumer Loan and on to its Terms page, all localized |
| TBC-T3 | `CurrencyConversionDataDrivenTest` (5 DB rows) | For every active row: choose currency, type amount, buy amount = amount x quoted rate, URL describes the conversion |
| TBC-T4 | `CurrencyNetworkTest` | Choosing EUR fires `GET getExchangeRate?Iso1=EUR&Iso2=GEL`; request, status, body and resulting UI are all checked |
| TBC-T5 | `CurrencyCalculatorTest` | Swap reverses GEL/USD, symbols, rate description, amount and URL |
| TBC-T6 | `MegaMenuNavigationTest` | Mega menu to Consumer Loan, then back through the Loans and Home breadcrumbs |
| TBC-T7 | `ConsumerLoanApiUiConsistencyTest` (en, ka) | Page title, breadcrumbs, heading, description, benefits and CTA equal the content API response |
| TBC-T8 | `SiteContentApiTest` | Site structure: locales, default locale, published routes |
| TBC-T9 | `SiteContentApiTest` | Consumer loan content in both locales: nested breadcrumbs, CTA section, list, links, SEO |
| TBC-T10 | `SiteContentApiTest` | Negative: unknown page id returns 404 with an `ApiError` body |
| TBC-T11 | `ExchangeRateApiTest` | Rate list is sane, direct rate equals the list, rates are the same in both locales |

UI scenarios: T1-T7 (seven; T1-T5 and T7 go beyond navigation or static content). API: T8-T11 (one negative).

## 9.1 Framework architecture

The layers follow one rule: *selectors live in pages/components, decisions and assertions live in steps,
tests only describe the journey*.

- **components/** own a root locator and the selectors inside it, plus the behaviour that belongs to that
  widget. `HeaderComponent` knows how to hover a navigation item and find a mega menu link by href,
  `LanguageSwitcherComponent` knows how to toggle the language, `CookieBannerComponent` knows how to reject
  and how to register itself as a Playwright locator handler, `CurrencyDropdownComponent` knows how to open
  its overlay and pick an ISO code.
- **pages/** describe one URL. `BasePage` composes the components every page has (header, cookie banner,
  breadcrumbs), concrete pages add their own: `ConsumerLoanPage` and `ConsumerLoanTermsPage` both reuse
  `CtaSectionComponent`, `CurrencyExchangePage` uses two `CurrencyDropdownComponent` instances
  (`sellCurrency` and `buyCurrency`) instead of two copies of the same selectors.
- **steps/** are the business vocabulary (`switchLanguageTo`, `selectSellCurrencyCapturingRate`,
  `convertedAmountShouldBe`). They use Playwright web-first assertions and synchronise on network events.
  API steps (`ContentApiSteps`, `ExchangeRateApiSteps`) hold the API assertions so API and API-to-UI tests
  share them.
- **api/** has Rest Assured clients (`BaseApiClient` builds one spec with base URI, Jackson mapper and the
  Allure filter) and Java records that mirror the JSON.
- **database/** holds the MyBatis session factory, the mapper interface and the row model.
- **tests/** only chain steps. `BaseUiTest` opens a new browser context per test method and closes it.

**Why components are separate from pages.** The header, breadcrumbs and cookie banner are the same DOM on
every tbcbank.ge page. If each page defined them, a markup change in the header would mean editing every
page object. Concrete reuse example: `BreadcrumbsComponent` is created once in `BasePage`; the same
`breadcrumbs.links` locator is used by `LocalizationTest` on the Consumer Loan page and on the Terms page,
by `MegaMenuNavigationTest` on the Loans page and by `ConsumerLoanApiUiConsistencyTest`. The
`CtaSectionComponent` is another one: the hero of Consumer Loan and the hero of the Terms page have the
same structure, one class serves both (when the Terms page turned out to render its title in a `<p>` and
not an `<h1>`, one locator change in the component fixed both pages).

## 9.2 Localization strategy

- `SiteLocale` enum holds what the code needs to know about a locale: URL prefix (`en`, `ka`) and API code
  (`en-US`, `ka-GE`). `UrlBuilder` and the API clients take a `SiteLocale`, so no URL or query string is
  written per language.
- Expected texts live in `src/main/resources/testdata/localization/{en,ka}.properties` (UTF-8): switcher
  label, mega menu label, breadcrumbs, heading, CTA text, cookie banner title. `LocalizationData` loads and
  caches them.
- `DataProviders.locales()` builds one row per `SiteLocale.values()` entry. `LocalizationTest` has a single
  method; it receives `LocalizationData`, starts on the *other* locale (`locale.alternative()`), switches
  with the real switcher and then does the whole journey. After the switch it also checks `<html lang>`,
  that every breadcrumb href stays under `/{locale}` and that the Terms page (reached by clicking, not by
  URL) is still in the same locale, so a broken journey after a locale change is detected.
- The API-to-UI test uses the same provider, so in Georgian the expectations come from the API with
  `locale=ka-GE` and no Georgian text is hard-coded there at all.

Adding a locale (for example Russian): add `RU("ru", "ru-RU")` to `SiteLocale`, add
`testdata/localization/ru.properties` with the same keys. Both parameterised tests pick it up
automatically. The only code decision is `alternative()`, which today returns the first other locale; with
three locales the switcher on the site becomes a dropdown and `LanguageSwitcherComponent.toggleLanguage()`
would need an option picker.

## 9.3 SQL and test data strategy

The currency scenarios are data, not code: which currency, which amount, which symbol the field should
show, and whether the scenario is active. Keeping them in SQL lets a tester add or switch off a case
without touching Java, and MyBatis keeps the SQL outside the code in an XML mapper.

Flow:

```
testdata/schema.sql + data.sql  ->  H2 (jdbc:h2:mem, MSSQLServer mode)
MyBatisSessionFactory           ->  builds SqlSessionFactory from mybatis/mybatis-config.xml, runs the scripts once
CurrencyConversionMapper.xml    ->  SELECT ... FROM currency_conversion WHERE active = TRUE ORDER BY id
CurrencyConversion              ->  resultMap maps sell_currency, sell_symbol, amount (DECIMAL -> BigDecimal) ...
DataProviders.currencyConversions() -> one Object[] per row, parallel = true
CurrencyConversionDataDrivenTest    -> same method runs for every row (named by the row's toString)
```

The session factory is lazy and synchronised, so four parallel DataProvider threads see one seeded
database. The data script uses `MERGE INTO ... KEY (id)`, so running it twice cannot duplicate rows.

New variation: add a `MERGE INTO currency_conversion ...` line to `data.sql` with a new id, a currency the
calculator quotes per 1 unit (for example `CAD` or `AZN`), the symbol the amount field shows for it, an
amount and `TRUE`. No test, provider or mapper changes. Row 6 (JPY) shows the other direction: it is kept
in the table with `active = FALSE` because JPY is quoted per 100 units, so the query filters it out.

## 9.4 API to UI strategy

Selected API: the content delivery API the site itself uses.

1. `GET /api/v1/sites/{siteId}` returns the route table; the test finds the page id by slug
   `/loans/consumer-loan` instead of hard-coding the id.
2. `GET /api/v1/sites/pages/{id}?locale={code}` returns the page; it is mapped to `ContentPage` with
   nested `Seo`, `Breadcrumbs -> List<Link>`, `List<SectionComponent> -> SectionInputs -> List<ListItem>`,
   `List<Button> -> Link` and a recursive `RichTextNode` for the Contentful heading.

Compared with the UI (API is the source of truth):

| API | UI |
|---|---|
| `seo.title` | browser tab title |
| `breadcrumbs.items[].label` | breadcrumb links, in order |
| `ctaSection.richTextTitle` heading-1 text | hero heading |
| `ctaSection.bodyText` | hero description |
| `ctaSection.list[].label` | benefit list, in order |
| `ctaSection.buttons[0].label` and `link.url` | CTA text and `href` (with `/{locale}` prefix) |

These values were chosen because they are what a customer reads and clicks on the product page and
because they are edited in the CMS, so they change without a code release. The test would catch: a CMS
edit that the SSR cache is not showing yet, a benefit that is missing or in another order, a translated
field falling back to the wrong language, a CTA pointing to an old URL or missing the locale prefix, a
breadcrumb label changed in one place only, and a rendering bug where the rich text heading is dropped.

## 9.5 Network validation

`CurrencyNetworkTest` (TBC-T4). The UI action is choosing **EUR** in the "Sell" dropdown of the currency
calculator. `CurrencyExchangeSteps.selectSellCurrencyCapturingRate` wraps that click in
`page.waitForResponse(predicate, action)`, so the listener exists before the click and the step returns
exactly the response the click produced.

Network level:
- endpoint path is `/api/v1/exchangeRates/getExchangeRate`,
- method is `GET`,
- query parameters `Iso1=EUR` and `Iso2=GEL`,
- status 200,
- body mapped to the same `ExchangeRate` record the Rest Assured client uses: `iso1`, `iso2`, positive
  `buyRate`, `sellRate >= buyRate`.

UI level, using the captured `buyRate`: the description reads `1 EUR = {buyRate} GEL`, the buy field shows
`100 x buyRate`, and the URL becomes `/EUR-to-GEL?amount=100`. Nothing waits for time; the step returns
when the response arrives and the UI checks are auto-retrying assertions.

## 9.6 Test stability

Scenario: `LocalizationTest` (language switch + mega menu + CTA journey), the longest UI flow.

1. **Clicking before Angular hydrates.** tbcbank.ge is server-side rendered. Buttons are visible before
   Angular attaches handlers, so an early click on the language switcher does nothing and any
   visibility-based wait passes. `NavigationSteps.openPage` wraps `page.navigate` in
   `waitForResponse` for the page's own content call (`/api/v1/sites/pages/...?locale=...`), which the
   client app only makes once it has booted. The language switch and the mega menu click are also wrapped
   in `waitForResponse` for the content of the *target* locale/page, so the next assertion starts when the
   new content has arrived.
2. **Cookie banner covering elements.** The banner appears some time after load and can sit over the
   element being clicked. Instead of a "close it if present" check (a race), `BaseUiTest` registers
   `CookieBannerComponent.rejectWheneverShown()`, a Playwright locator handler that rejects the banner
   whenever it is visible before an action. `CookieConsentTest` switches this off to test the banner itself.
3. **Third-party scripts.** Tag Manager, VWO (which hides the page while it loads experiments), Facebook,
   LinkedIn, PostHog and Medallia add requests, overlays and timing noise. `PlaywrightManager` aborts them
   with one `context.route` pattern (`block.third.party=true`), which also keeps `waitForResponse`
   predicates free of noise.
4. **Mega menu is hover driven.** The link only exists in the visible sub menu after hover, so the step
   hovers, asserts the sub menu is visible, and resolves the link by `href` built from the locale (labels
   are asserted separately from localized data, not used as selectors).
5. **Parallel runs and the site firewall.** Each thread has its own `Playwright`/`Browser` in a
   `ThreadLocal`, and each test its own `BrowserContext`, so tests share no cookies or state and can run
   in any order. tbcbank.ge rate-limits aggressive traffic ("Your request has been blocked, Error code
   101"); four threads plus blocked trackers stay well below that, and if the firewall still blocks,
   `openPage` fails immediately with the firewall message instead of a generic timeout.

There is no `Thread.sleep`, `waitForTimeout` or retry analyzer in the project; every wait is a network
event or a web-first assertion (`assertThat(...).hasText/hasURL/hasValue/isVisible`).

## Zephyr Scale traceability

All scenarios are documented in Zephyr Scale with preconditions, numbered steps, expected results and
test data. Every automated test starts its description with the Zephyr key:

```java
@Test(description = "TBC-T4 | Selecting a currency requests its rate from the exchange API and renders it")
```

The key appears in the TestNG/Surefire and Allure reports, so a failed run points straight to the
Zephyr test case.
