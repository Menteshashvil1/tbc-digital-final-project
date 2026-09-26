package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Page;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.pages.ConsumerLoanPage;
import ge.tbc.testautomation.pages.ConsumerLoanTermsPage;
import io.qameta.allure.Step;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ConsumerLoanSteps {
    private final Page page;
    private final ConsumerLoanPage consumerLoanPage;
    private final ConsumerLoanTermsPage termsPage;

    public ConsumerLoanSteps(Page page) {
        this.page = page;
        this.consumerLoanPage = new ConsumerLoanPage(page);
        this.termsPage = new ConsumerLoanTermsPage(page);
    }

    @Step("Consumer loan heading should be {title}")
    public ConsumerLoanSteps headingShouldBe(String title) {
        assertThat(consumerLoanPage.heroSection.title).hasText(title);
        return this;
    }

    @Step("Consumer loan description should be {text}")
    public ConsumerLoanSteps descriptionShouldBe(String text) {
        assertThat(consumerLoanPage.heroSection.bodyText).hasText(text);
        return this;
    }

    @Step("Consumer loan benefits should be {benefits}")
    public ConsumerLoanSteps benefitsShouldBe(List<String> benefits) {
        assertThat(consumerLoanPage.heroSection.listItems).hasText(benefits.toArray(String[]::new));
        return this;
    }

    @Step("Consumer loan should list {count} benefits")
    public ConsumerLoanSteps benefitsShouldHaveCount(int count) {
        assertThat(consumerLoanPage.heroSection.listItems).hasCount(count);
        return this;
    }

    @Step("Primary call to action should be {label} linking to {href}")
    public ConsumerLoanSteps primaryButtonShouldBe(String label, String href) {
        assertThat(consumerLoanPage.heroSection.button(0)).hasText(label);
        assertThat(consumerLoanPage.heroSection.button(0)).hasAttribute("href", href);
        return this;
    }

    @Step("Open consumer loan terms in {locale}")
    public ConsumerLoanSteps openTerms(SiteLocale locale) {
        page.waitForResponse(response -> NavigationSteps.isContentPageResponse(response, locale),
                () -> consumerLoanPage.heroSection.clickButton(0));
        return this;
    }

    @Step("Terms page should show heading {title} with terms tabs")
    public ConsumerLoanSteps termsPageShouldBeShown(String title) {
        assertThat(termsPage.heroSection.title).hasText(title);
        assertThat(termsPage.termsTabs.first()).isVisible();
        return this;
    }
}
