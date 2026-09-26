package ge.tbc.testautomation.tests.ui;

import ge.tbc.testautomation.constants.PageSlugs;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.steps.CookieConsentSteps;
import ge.tbc.testautomation.steps.NavigationSteps;
import ge.tbc.testautomation.tests.BaseUiTest;
import ge.tbc.testautomation.utils.LocalizationData;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.util.List;

@Feature("Cookie consent")
public class CookieConsentTest extends BaseUiTest {

    @Override
    protected boolean rejectCookiesAutomatically() {
        return false;
    }

    @Test(description = "TBC-T1 | Rejecting cookies hides the consent banner and the choice survives reload and navigation")
    public void rejectedConsentShouldPersist() {
        LocalizationData english = LocalizationData.forLocale(SiteLocale.EN);
        NavigationSteps navigationSteps = new NavigationSteps(page());
        CookieConsentSteps cookieSteps = new CookieConsentSteps(page());

        navigationSteps.openPage(SiteLocale.EN, PageSlugs.HOME);
        List<String> cookiesBeforeChoice = cookieSteps.cookieNames();

        cookieSteps
                .bannerShouldOfferChoices(english.get("cookie.title"))
                .rejectAll()
                .bannerShouldBeHidden()
                .consentShouldBeStored(cookiesBeforeChoice)
                .reload()
                .bannerShouldBeHidden();

        navigationSteps
                .openPage(SiteLocale.EN, PageSlugs.CONSUMER_LOAN)
                .urlShouldBe(SiteLocale.EN, PageSlugs.CONSUMER_LOAN);
        cookieSteps.bannerShouldBeHidden();
    }
}
