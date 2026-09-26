package ge.tbc.testautomation.tests.ui;

import ge.tbc.testautomation.api.models.Button;
import ge.tbc.testautomation.api.models.ContentPage;
import ge.tbc.testautomation.api.models.SectionInputs;
import ge.tbc.testautomation.constants.PageSlugs;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.steps.ConsumerLoanSteps;
import ge.tbc.testautomation.steps.ContentApiSteps;
import ge.tbc.testautomation.steps.NavigationSteps;
import ge.tbc.testautomation.tests.BaseUiTest;
import ge.tbc.testautomation.utils.DataProviders;
import ge.tbc.testautomation.utils.LocalizationData;
import ge.tbc.testautomation.utils.UrlBuilder;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("API to UI consistency")
public class ConsumerLoanApiUiConsistencyTest extends BaseUiTest {

    @Test(description = "TBC-T7 | Consumer Loan page renders the heading, benefits, CTA and breadcrumbs served by the content API",
            dataProvider = DataProviders.LOCALES, dataProviderClass = DataProviders.class)
    public void consumerLoanPageShouldMatchContentApi(LocalizationData localization) {
        SiteLocale locale = localization.locale();
        ContentApiSteps apiSteps = new ContentApiSteps();

        ContentPage apiPage = apiSteps.loadPage(PageSlugs.CONSUMER_LOAN, locale);
        SectionInputs hero = apiSteps.heroSectionShouldBeComplete(apiPage, PageSlugs.CONSUMER_LOAN);
        Button primaryButton = hero.primaryButton();

        new NavigationSteps(page())
                .openPage(locale, PageSlugs.CONSUMER_LOAN)
                .browserTitleShouldBe(apiPage.seo().title())
                .breadcrumbsShouldBe(apiPage.breadcrumbLabels());

        new ConsumerLoanSteps(page())
                .headingShouldBe(hero.headingText())
                .descriptionShouldBe(hero.bodyText().trim())
                .benefitsShouldBe(hero.listLabels())
                .primaryButtonShouldBe(primaryButton.label().trim(),
                        UrlBuilder.localizedPath(locale, primaryButton.link().url()));
    }
}
