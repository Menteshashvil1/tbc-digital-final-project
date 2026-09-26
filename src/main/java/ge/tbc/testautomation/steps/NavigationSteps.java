package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.TimeoutError;
import ge.tbc.testautomation.constants.Endpoints;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.pages.HomePage;
import ge.tbc.testautomation.utils.Patterns;
import ge.tbc.testautomation.utils.UrlBuilder;
import io.qameta.allure.Step;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class NavigationSteps {
    private static final String CONTENT_PAGE_PATH = "/api/v1/sites/pages/";

    private final Page page;
    private final HomePage homePage;

    public NavigationSteps(Page page) {
        this.page = page;
        this.homePage = new HomePage(page);
    }

    @Step("Open {slug} in {locale}")
    public NavigationSteps openPage(SiteLocale locale, String slug) {
        String url = UrlBuilder.pageUrl(locale, slug);
        try {
            page.waitForResponse(response -> isContentPageResponse(response, locale), () -> page.navigate(url));
        } catch (TimeoutError error) {
            if (homePage.firewallMessage.isVisible()) {
                throw new IllegalStateException("tbcbank.ge firewall blocked " + url + ": "
                        + homePage.firewallMessage.innerText(), error);
            }
            throw error;
        }
        assertThat(homePage.header.root()).isVisible();
        return this;
    }

    @Step("Open the Personal mega menu")
    public NavigationSteps openPersonalMegaMenu() {
        homePage.header.hoverNavigationItem(0);
        assertThat(homePage.header.visibleSubmenu).isVisible();
        return this;
    }

    @Step("Choose {slug} from the mega menu")
    public NavigationSteps chooseMegaMenuLink(SiteLocale locale, String slug, String expectedLabel) {
        Locator link = homePage.header.megaMenuLink(UrlBuilder.localizedPath(locale, slug));
        assertThat(link).isVisible();
        assertThat(link).hasText(expectedLabel);
        page.waitForResponse(response -> isContentPageResponse(response, locale), link::click);
        return this;
    }

    @Step("Switch the site language to {target}")
    public NavigationSteps switchLanguageTo(SiteLocale target) {
        page.waitForResponse(response -> isContentPageResponse(response, target),
                homePage.header.languageSwitcher::toggleLanguage);
        assertThat(homePage.html).hasAttribute("lang", target.path());
        return this;
    }

    @Step("URL should point to {slug} in {locale}")
    public NavigationSteps urlShouldBe(SiteLocale locale, String slug) {
        assertThat(page).hasURL(Patterns.pageUrl(UrlBuilder.pageUrl(locale, slug)));
        return this;
    }

    @Step("Document language should be {locale}")
    public NavigationSteps documentLanguageShouldBe(SiteLocale locale) {
        assertThat(homePage.html).hasAttribute("lang", locale.path());
        return this;
    }

    @Step("Language switcher should offer {label}")
    public NavigationSteps languageSwitcherShouldOffer(String label) {
        assertThat(homePage.header.languageSwitcher.offeredLanguage).hasText(label);
        return this;
    }

    @Step("Breadcrumbs should be {labels}")
    public NavigationSteps breadcrumbsShouldBe(List<String> labels) {
        assertThat(homePage.breadcrumbs.links).hasText(labels.toArray(String[]::new));
        return this;
    }

    @Step("Breadcrumb links should stay inside {locale}")
    public NavigationSteps breadcrumbLinksShouldUseLocale(SiteLocale locale) {
        int count = homePage.breadcrumbs.links.count();
        for (int index = 0; index < count; index++) {
            assertThat(homePage.breadcrumbs.link(index))
                    .hasAttribute("href", Pattern.compile("^/" + locale.path() + "(/.*)?$"));
        }
        return this;
    }

    @Step("Go back through breadcrumb {label}")
    public NavigationSteps openBreadcrumb(String label) {
        Locator crumb = homePage.breadcrumbs.links.filter(new Locator.FilterOptions()
                .setHasText(Patterns.exactText(label)));
        assertThat(crumb).hasCount(1);
        crumb.click();
        return this;
    }

    @Step("Logo should lead to the {locale} home page")
    public NavigationSteps logoShouldLeadHome(SiteLocale locale) {
        assertThat(homePage.header.logoLink).hasAttribute("href", UrlBuilder.localizedPath(locale, ""));
        return this;
    }

    @Step("Browser title should be {title}")
    public NavigationSteps browserTitleShouldBe(String title) {
        assertThat(page).hasTitle(title);
        return this;
    }

    public static boolean isContentPageResponse(Response response, SiteLocale locale) {
        String url = response.url();
        return url.contains(CONTENT_PAGE_PATH)
                && url.contains(Endpoints.LOCALE_PARAM + "=" + locale.code())
                && response.ok();
    }
}
