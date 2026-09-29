# Zephyr Scale Test Cases

Project key: `TBC` · Folder: `/TBC Digital/Final Project 2`

Every automated test carries its Zephyr key at the start of its TestNG description, for example
`@Test(description = "TBC-T2 | Switch language and complete the consumer loan journey in the selected locale")`.
The same keys are used below and in [zephyr-test-cases.csv](zephyr-test-cases.csv), which can be imported
through *Zephyr Scale → Import → CSV* (one row per step, test case fields filled on the first step row).

| Key | Title | Type | Automated in |
|---|---|---|---|
| TBC-T1 | Rejecting cookies hides the consent banner and the choice survives reload and navigation | UI | `CookieConsentTest` |
| TBC-T2 | Switch language and complete the consumer loan journey in the selected locale | Localization | `LocalizationTest` |
| TBC-T3 | Currency calculator converts database-driven amounts with the quoted rate | SQL / DataProvider | `CurrencyConversionDataDrivenTest` |
| TBC-T4 | Selecting a currency requests its rate from the exchange API and renders it | Network | `CurrencyNetworkTest` |
| TBC-T5 | Swapping currencies reverses the conversion direction and recalculates the amount | UI | `CurrencyCalculatorTest` |
| TBC-T6 | Reach Consumer Loan through the mega menu and return to Loans through the breadcrumbs | UI | `MegaMenuNavigationTest` |
| TBC-T7 | Consumer Loan page renders the heading, benefits, CTA and breadcrumbs served by the content API | API → UI | `ConsumerLoanApiUiConsistencyTest` |
| TBC-T8 | Site structure API publishes both locales and the Consumer Loan route | API | `SiteContentApiTest` |
| TBC-T9 | Consumer Loan content API returns a complete, translated page in English and Georgian | API | `SiteContentApiTest` |
| TBC-T10 | Content API answers an unknown page id with a structured 404 error | API (negative) | `SiteContentApiTest` |
| TBC-T11 | Commercial rate list and direct rate endpoint agree on the EUR to GEL rate | API | `ExchangeRateApiTest` |

---

## TBC-T1 · Rejecting cookies hides the consent banner and the choice survives reload and navigation

**Objective:** A visitor who rejects optional cookies is not asked again on the next page load or page.

**Preconditions:** Fresh browser context (no tbcbank.ge cookies or local storage).

**Test data:** Locale `en`, banner title `Cookie Consent`.

| # | Step | Expected result |
|---|---|---|
| 1 | Open `https://tbcbank.ge/en` | Home page loads, the cookie consent banner is shown |
| 2 | Check the banner | Title is "Cookie Consent", a description is present, three actions are offered (Accept All, Customize, Reject All) |
| 3 | Click **Reject All** | Banner disappears and cookie `tbc-ge-cookie-consent-v1` stores `result = reject-all` (it did not exist before the choice) |
| 4 | Reload the page | Banner stays hidden |
| 5 | Open `/en/loans/consumer-loan` | Page opens and the banner is still hidden |

## TBC-T2 · Switch language and complete the consumer loan journey in the selected locale

**Objective:** The same journey works in Georgian and English after the visitor switches the language.

**Preconditions:** Fresh browser context, cookie banner dismissed.

**Test data** (`src/main/resources/testdata/localization/{en,ka}.properties`):

| Field | en | ka |
|---|---|---|
| Start locale | ka | en |
| Switcher offers | ქარ | EN |
| Mega menu link | Consumer | სამომხმარებლო |
| Breadcrumbs | Home › Loans › Consumer loan | მთავარი › სესხები › სამომხმარებლო სესხი |
| Heading | Consumer Loan | სამომხმარებლო სესხი |
| CTA button | Terms | პირობები |
| Terms breadcrumb | Terms | პირობები |

| # | Step | Expected result |
|---|---|---|
| 1 | Open the home page in the *other* locale | Home page loads |
| 2 | Click the language switcher | Site reloads content for the target locale, URL becomes `/{locale}`, `<html lang>` is the target locale |
| 3 | Check the header | Switcher offers the opposite language, logo links to `/{locale}` |
| 4 | Hover **Personal** and choose the consumer loan link | Link label is localized, `/{locale}/loans/consumer-loan` opens |
| 5 | Check the page | Localized breadcrumbs, every breadcrumb link stays under `/{locale}`, localized heading, 3 benefits, localized CTA pointing to `/{locale}/loans/consumer-loan/digital` |
| 6 | Click the CTA | Terms page opens in the same locale with the four-level localized breadcrumb, localized heading and the terms tabs |

## TBC-T3 · Currency calculator converts database-driven amounts with the quoted rate

**Objective:** The calculator converts any stored amount using the rate it displays.

**Preconditions:** Local H2 database seeded from `testdata/schema.sql` and `testdata/data.sql`; only rows with `active = TRUE` are used.

**Test data (table `currency_conversion`):**

| id | Scenario | Sell | Buy | Amount | Symbol | Active |
|---|---|---|---|---|---|---|
| 1 | US dollar small amount | USD | GEL | 100.00 | $ | yes |
| 2 | Euro medium amount | EUR | GEL | 250.00 | € | yes |
| 3 | Pound sterling large amount | GBP | GEL | 1500.00 | £ | yes |
| 4 | Swiss franc fractional amount | CHF | GEL | 75.50 | CHF | yes |
| 5 | Turkish lira low rate currency | TRY | GEL | 1000.00 | ₺ | yes |
| 6 | Japanese yen weighted rate | JPY | GEL | 5000.00 | ¥ | no |

| # | Step | Expected result |
|---|---|---|
| 1 | Open `/en/valutis-kursi` | Calculator shows USD → GEL with a "1 USD = x GEL" description |
| 2 | Choose the stored sell currency | Selected currency and amount symbol match the record, description quotes "1 {sell} = x {buy}" |
| 3 | Type the stored amount | Sell field shows the amount |
| 4 | Read the quoted rate | Buy field equals amount × rate rounded to 2 decimals |
| 5 | Check the URL | URL ends with `/{sell}-to-{buy}?amount={amount}` |

## TBC-T4 · Selecting a currency requests its rate from the exchange API and renders it

**Objective:** The calculator fetches the rate for the chosen currency and shows exactly that value.

**Preconditions:** Currency page open with the default USD → GEL pair.

**Test data:** Sell currency `EUR`, buy currency `GEL`, amount `100`.

| # | Step | Expected result |
|---|---|---|
| 1 | Choose **EUR** in the sell dropdown while listening to network traffic | One request to `/api/v1/exchangeRates/getExchangeRate` is captured |
| 2 | Inspect the request | Method `GET`, query `Iso1=EUR`, `Iso2=GEL` |
| 3 | Inspect the response | Status 200, body `iso1=EUR`, `iso2=GEL`, `buyRate > 0`, `sellRate ≥ buyRate` |
| 4 | Check the UI | Description is "1 EUR = {buyRate} GEL", buy field is 100 × buyRate, URL ends with `/EUR-to-GEL?amount=100` |

## TBC-T5 · Swapping currencies reverses the conversion direction and recalculates the amount

**Preconditions:** Currency page open with USD → GEL.

| # | Step | Expected result |
|---|---|---|
| 1 | Click the swap button | A new rate for GEL → USD is requested |
| 2 | Check the calculator | Sell is GEL (₾), buy is USD ($), description quotes "1 GEL = x USD" |
| 3 | Check the result | Buy field equals 100 × quoted rate, URL ends with `/GEL-to-USD?amount=100` |

## TBC-T6 · Reach Consumer Loan through the mega menu and return to Loans through the breadcrumbs

**Preconditions:** Fresh context, English home page.

| # | Step | Expected result |
|---|---|---|
| 1 | Hover **Personal** in the header | Mega menu opens with the Personal sub menu |
| 2 | Click **Consumer** | `/en/loans/consumer-loan` opens with breadcrumbs Home › Loans › Consumer loan and heading "Consumer Loan" |
| 3 | Click the **Loans** breadcrumb | `/en/loans` opens with breadcrumbs Home › Loans |
| 4 | Click the **Home** breadcrumb | `/en` opens |

## TBC-T7 · Consumer Loan page renders the heading, benefits, CTA and breadcrumbs served by the content API

**Objective:** The UI shows exactly what the CMS delivery API returns (the API is the source of truth). Runs for `en-US` and `ka-GE`.

**Preconditions:** Content API reachable.

| # | Step | Expected result |
|---|---|---|
| 1 | GET `/api/v1/sites/{siteId}` and find the page with slug `/loans/consumer-loan` | Page id is returned |
| 2 | GET `/api/v1/sites/pages/{id}?locale={code}` and map it to `ContentPage` | First `ctaSection` has a heading, benefits list and a primary button |
| 3 | Open `/{locale}/loans/consumer-loan` | Page loads |
| 4 | Compare | Browser title = `seo.title`; breadcrumbs = `breadcrumbs.items[].label`; heading = `richTextTitle` heading-1; description = `bodyText`; benefits = `list[].label` in order; CTA text = `buttons[0].label`, href = `/{locale}` + `buttons[0].link.url` |

## TBC-T8 · Site structure API publishes both locales and the Consumer Loan route

| # | Step | Expected result |
|---|---|---|
| 1 | GET `/api/v1/sites/QmI6EfBbkDQ9awR6nBYXx` | 200, body maps to `Site` |
| 2 | Check `locales[]` | `ka-GE` (short code `ka`, default) and `en-US` (short code `en`) are present, each with a label, exactly one default |
| 3 | Check `pages[]` | At least 50 routes; `/loans/consumer-loan` and `/valutis-kursi` exist with ids; cookie consent entry is referenced |

## TBC-T9 · Consumer Loan content API returns a complete, translated page in English and Georgian

| # | Step | Expected result |
|---|---|---|
| 1 | Resolve the consumer loan id from the site structure | Id found |
| 2 | GET the page for `en-US` and `ka-GE` | Both 200 and map to `ContentPage` with the same id and slug |
| 3 | Check SEO | Title filled, page indexable, titles differ between locales |
| 4 | Check breadcrumbs | 2–5 items, first is `/`, last is the page slug, all labelled, same depth in both locales |
| 5 | Check the hero `ctaSection` | Heading, visible non-empty benefit list, internal primary link under `/loans/consumer-loan` |

## TBC-T10 · Content API answers an unknown page id with a structured 404 error

**Test data:** page id `tbcDigitalMissingPage404`, locale `en-US`.

| # | Step | Expected result |
|---|---|---|
| 1 | GET `/api/v1/sites/pages/tbcDigitalMissingPage404?locale=en-US` | Status 404 with a JSON body |
| 2 | Map the body to `ApiError` | `type` and `code` are `ObjectNotFoundException`, `status` is 404, `detail` names the requested id, `traceId` is present |

## TBC-T11 · Commercial rate list and direct rate endpoint agree on the EUR to GEL rate

| # | Step | Expected result |
|---|---|---|
| 1 | GET `/api/v1/exchangeRates/commercialList?locale=en-US` and `ka-GE` | 200, `rates[]` has at least 5 currencies including USD and EUR |
| 2 | Check every rate | Buy > 0, sell ≥ buy, official rate between buy and sell, weight > 0 |
| 3 | GET `/api/v1/exchangeRates/getExchangeRate?Iso1=EUR&Iso2=GEL` | Buy rate, sell rate and weight equal the EUR row of the list |
| 4 | Compare locales | Rates are identical, currency names are translated |
