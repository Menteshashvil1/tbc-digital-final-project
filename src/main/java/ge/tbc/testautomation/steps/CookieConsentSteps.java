package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Page;
import ge.tbc.testautomation.pages.HomePage;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CookieConsentSteps {
    private static final int CONSENT_ACTIONS = 3;

    private final Page page;
    private final HomePage homePage;

    public CookieConsentSteps(Page page) {
        this.page = page;
        this.homePage = new HomePage(page);
    }

    @Step("Cookie banner should offer accept, customize and reject")
    public CookieConsentSteps bannerShouldOfferChoices(String expectedTitle) {
        assertThat(homePage.cookieBanner.root()).isVisible();
        assertThat(homePage.cookieBanner.title).hasText(expectedTitle);
        assertThat(homePage.cookieBanner.description).not().isEmpty();
        assertThat(homePage.cookieBanner.actionButtons).hasCount(CONSENT_ACTIONS);
        return this;
    }

    @Step("Reject all cookies")
    public CookieConsentSteps rejectAll() {
        homePage.cookieBanner.rejectAll();
        return this;
    }

    @Step("Accept all cookies")
    public CookieConsentSteps acceptAll() {
        homePage.cookieBanner.acceptAll();
        return this;
    }

    @Step("Cookie banner should be hidden")
    public CookieConsentSteps bannerShouldBeHidden() {
        assertThat(homePage.cookieBanner.root()).isHidden();
        return this;
    }

    @Step("Reload the page")
    public CookieConsentSteps reload() {
        page.reload();
        assertThat(homePage.header.root()).isVisible();
        return this;
    }

    @Step("A consent choice should be stored for the site")
    public CookieConsentSteps consentShouldBeStored(List<String> cookiesBefore) {
        List<String> cookiesAfter = cookieNames();
        Assert.assertTrue(cookiesAfter.size() > cookiesBefore.size() || storedConsent(),
                "Rejecting cookies should persist the choice, cookies were " + cookiesAfter);
        return this;
    }

    public List<String> cookieNames() {
        return page.context().cookies().stream()
                .map(cookie -> cookie.name)
                .toList();
    }

    private boolean storedConsent() {
        Object stored = page.evaluate("() => Object.keys(localStorage).filter(k => /cookie|consent/i.test(k)).length");
        return stored instanceof Number number && number.intValue() > 0;
    }
}
