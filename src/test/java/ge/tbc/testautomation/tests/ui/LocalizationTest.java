package ge.tbc.testautomation.tests.ui;

import ge.tbc.testautomation.constants.PageSlugs;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.steps.ConsumerLoanSteps;
import ge.tbc.testautomation.steps.NavigationSteps;
import ge.tbc.testautomation.tests.BaseUiTest;
import ge.tbc.testautomation.utils.DataProviders;
import ge.tbc.testautomation.utils.LocalizationData;
import ge.tbc.testautomation.utils.UrlBuilder;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("Localization")
public class LocalizationTest extends BaseUiTest {

    @Test(description = "TBC-T2 | Switch language and complete the consumer loan journey in the selected locale",
            dataProvider = DataProviders.LOCALES, dataProviderClass = DataProviders.class)
    public void consumerLoanJourneyShouldBeLocalized(LocalizationData expected) {
        SiteLocale locale = expected.locale();
        NavigationSteps navigationSteps = new NavigationSteps(page());
        ConsumerLoanSteps consumerLoanSteps = new ConsumerLoanSteps(page());

        navigationSteps
                .openPage(locale.alternative(), PageSlugs.HOME)
                .switchLanguageTo(locale)
                .urlShouldBe(locale, PageSlugs.HOME)
                .documentLanguageShouldBe(locale)
                .languageSwitcherShouldOffer(expected.get("switcher.offered"))
                .logoShouldLeadHome(locale)
                .openPersonalMegaMenu()
                .chooseMegaMenuLink(locale, PageSlugs.CONSUMER_LOAN, expected.get("menu.consumer"))
                .urlShouldBe(locale, PageSlugs.CONSUMER_LOAN)
                .breadcrumbsShouldBe(expected.values("breadcrumb.home", "breadcrumb.loans", "breadcrumb.consumer"))
                .breadcrumbLinksShouldUseLocale(locale);

        consumerLoanSteps
                .headingShouldBe(expected.get("consumer.heading"))
                .benefitsShouldHaveCount(expected.getInt("consumer.benefit.count"))
                .primaryButtonShouldBe(expected.get("consumer.terms.button"),
                        UrlBuilder.localizedPath(locale, PageSlugs.CONSUMER_LOAN_TERMS))
                .openTerms(locale);

        navigationSteps
                .urlShouldBe(locale, PageSlugs.CONSUMER_LOAN_TERMS)
                .documentLanguageShouldBe(locale)
                .breadcrumbsShouldBe(expected.values(
                        "breadcrumb.home", "breadcrumb.loans", "breadcrumb.consumer", "breadcrumb.terms"));

        consumerLoanSteps.termsPageShouldBeShown(expected.get("terms.heading"));
    }
}
