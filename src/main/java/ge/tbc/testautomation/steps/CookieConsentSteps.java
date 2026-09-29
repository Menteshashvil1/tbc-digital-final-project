package ge.tbc.testautomation.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import ge.tbc.testautomation.constants.TestConstants;
import ge.tbc.testautomation.pages.HomePage;
import ge.tbc.testautomation.utils.JsonMapper;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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

    @Step("No consent choice should be stored yet")
    public CookieConsentSteps consentShouldNotBeStored() {
        Assert.assertTrue(consentCookie() == null, "Fresh context should not have a consent cookie");
        return this;
    }

    @Step("Reject all cookies")
    public CookieConsentSteps rejectAll() {
        homePage.cookieBanner.rejectAll();
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

    @Step("Consent cookie should store the {expectedResult} choice")
    public CookieConsentSteps consentShouldBeStored(String expectedResult) {
        Cookie cookie = consentCookie();
        Assert.assertNotNull(cookie, "Choosing an option should store " + TestConstants.CONSENT_COOKIE);
        JsonNode consent = JsonMapper.fromJson(URLDecoder.decode(cookie.value, StandardCharsets.UTF_8), JsonNode.class);
        Assert.assertEquals(consent.path("result").asText(), expectedResult, "Stored consent choice");
        return this;
    }

    private Cookie consentCookie() {
        return page.context().cookies().stream()
                .filter(cookie -> TestConstants.CONSENT_COOKIE.equals(cookie.name))
                .findFirst()
                .orElse(null);
    }
}
