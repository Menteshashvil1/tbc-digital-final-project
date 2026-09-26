package ge.tbc.testautomation.tests.ui;

import ge.tbc.testautomation.constants.PageSlugs;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.steps.ConsumerLoanSteps;
import ge.tbc.testautomation.steps.NavigationSteps;
import ge.tbc.testautomation.tests.BaseUiTest;
import ge.tbc.testautomation.utils.LocalizationData;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("Navigation")
public class MegaMenuNavigationTest extends BaseUiTest {

    @Test(description = "TBC-T6 | Reach Consumer Loan through the mega menu and return to Loans through the breadcrumbs")
    public void megaMenuAndBreadcrumbsShouldNavigateBetweenLoanPages() {
        LocalizationData english = LocalizationData.forLocale(SiteLocale.EN);
        NavigationSteps navigationSteps = new NavigationSteps(page());

        navigationSteps
                .openPage(SiteLocale.EN, PageSlugs.HOME)
                .openPersonalMegaMenu()
                .chooseMegaMenuLink(SiteLocale.EN, PageSlugs.CONSUMER_LOAN, english.get("menu.consumer"))
                .urlShouldBe(SiteLocale.EN, PageSlugs.CONSUMER_LOAN)
                .breadcrumbsShouldBe(english.values("breadcrumb.home", "breadcrumb.loans", "breadcrumb.consumer"));

        new ConsumerLoanSteps(page()).headingShouldBe(english.get("consumer.heading"));

        navigationSteps
                .openBreadcrumb(english.get("breadcrumb.loans"))
                .urlShouldBe(SiteLocale.EN, PageSlugs.LOANS)
                .breadcrumbsShouldBe(english.values("breadcrumb.home", "breadcrumb.loans"))
                .openBreadcrumb(english.get("breadcrumb.home"))
                .urlShouldBe(SiteLocale.EN, PageSlugs.HOME);
    }
}
