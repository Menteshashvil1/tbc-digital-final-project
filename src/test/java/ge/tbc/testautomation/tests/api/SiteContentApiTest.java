package ge.tbc.testautomation.tests.api;

import ge.tbc.testautomation.api.models.ContentPage;
import ge.tbc.testautomation.api.models.Site;
import ge.tbc.testautomation.api.models.SitePage;
import ge.tbc.testautomation.constants.PageSlugs;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.constants.TestConstants;
import ge.tbc.testautomation.steps.ContentApiSteps;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.annotations.Test;

@Feature("Content API")
public class SiteContentApiTest {
    private final ContentApiSteps apiSteps = new ContentApiSteps();

    @Test(description = "TBC-T8 | Site structure API publishes both locales and the Consumer Loan route")
    public void siteStructureShouldPublishLocalesAndRoutes() {
        Site site = apiSteps.loadSite();

        apiSteps.siteShouldSupportLocales(site);
        apiSteps.siteShouldPublish(site, PageSlugs.CONSUMER_LOAN);
        apiSteps.siteShouldPublish(site, PageSlugs.CURRENCY_EXCHANGE);
    }

    @Test(description = "TBC-T9 | Consumer Loan content API returns a complete, translated page in English and Georgian")
    public void consumerLoanContentShouldBeCompleteInBothLocales() {
        SitePage route = apiSteps.siteShouldPublish(apiSteps.loadSite(), PageSlugs.CONSUMER_LOAN);

        ContentPage english = apiSteps.loadPage(PageSlugs.CONSUMER_LOAN, SiteLocale.EN);
        ContentPage georgian = apiSteps.loadPage(PageSlugs.CONSUMER_LOAN, SiteLocale.KA);

        apiSteps
                .pageShouldDescribe(english, route.id(), PageSlugs.CONSUMER_LOAN)
                .pageShouldDescribe(georgian, route.id(), PageSlugs.CONSUMER_LOAN)
                .breadcrumbsShouldLeadTo(english, PageSlugs.CONSUMER_LOAN)
                .breadcrumbsShouldLeadTo(georgian, PageSlugs.CONSUMER_LOAN)
                .localizedPagesShouldDiffer(english, georgian);
        apiSteps.heroSectionShouldBeComplete(english, PageSlugs.CONSUMER_LOAN);
        apiSteps.heroSectionShouldBeComplete(georgian, PageSlugs.CONSUMER_LOAN);
    }

    @Test(description = "TBC-T10 | Content API answers an unknown page id with a structured 404 error")
    public void unknownPageShouldReturnNotFound() {
        Response response = apiSteps.requestPage(TestConstants.UNKNOWN_PAGE_ID, SiteLocale.EN);

        apiSteps.notFoundShouldBeReported(response, TestConstants.UNKNOWN_PAGE_ID);
    }
}
