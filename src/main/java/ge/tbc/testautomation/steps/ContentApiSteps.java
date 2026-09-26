package ge.tbc.testautomation.steps;

import ge.tbc.testautomation.api.clients.SiteContentClient;
import ge.tbc.testautomation.api.models.ApiError;
import ge.tbc.testautomation.api.models.ContentPage;
import ge.tbc.testautomation.api.models.Link;
import ge.tbc.testautomation.api.models.SectionInputs;
import ge.tbc.testautomation.api.models.Site;
import ge.tbc.testautomation.api.models.SiteLocaleInfo;
import ge.tbc.testautomation.api.models.SitePage;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.constants.TestConstants;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;

import java.util.List;

public class ContentApiSteps {
    private final SiteContentClient client = new SiteContentClient();

    @Step("Load the site structure")
    public Site loadSite() {
        return client.getSite();
    }

    @Step("Load page {slug} in {locale}")
    public ContentPage loadPage(String slug, SiteLocale locale) {
        return client.getPageBySlug(slug, locale);
    }

    @Step("Request a page that does not exist")
    public Response requestPage(String pageId, SiteLocale locale) {
        return client.getPageResponse(pageId, locale);
    }

    @Step("Site should support every configured locale with ka-GE as default")
    public ContentApiSteps siteShouldSupportLocales(Site site) {
        for (SiteLocale locale : SiteLocale.values()) {
            SiteLocaleInfo info = site.localeByCode(locale.code())
                    .orElseThrow(() -> new AssertionError("Site does not publish locale " + locale.code()));
            Assert.assertEquals(info.shortCode(), locale.path(), "Locale short code should match the URL prefix");
            Assert.assertFalse(info.label().isBlank(), "Locale " + locale.code() + " needs a switcher label");
        }
        List<SiteLocaleInfo> defaults = site.locales().stream().filter(SiteLocaleInfo::isDefault).toList();
        Assert.assertEquals(defaults.size(), 1, "Exactly one locale should be the default");
        Assert.assertEquals(defaults.get(0).code(), SiteLocale.KA.code(), "Georgian should be the default locale");
        return this;
    }

    @Step("Site should publish {slug}")
    public SitePage siteShouldPublish(Site site, String slug) {
        Assert.assertTrue(site.pages().size() >= TestConstants.MIN_PUBLISHED_PAGES,
                "Site structure looks incomplete: " + site.pages().size() + " pages");
        SitePage sitePage = site.pageBySlug(slug)
                .orElseThrow(() -> new AssertionError("Site structure has no page " + slug));
        Assert.assertFalse(sitePage.id().isBlank(), "Published page needs an id");
        Assert.assertNotNull(site.cookieConsentId(), "Site should reference a cookie consent entry");
        return sitePage;
    }

    @Step("Page should describe {slug}")
    public ContentApiSteps pageShouldDescribe(ContentPage page, String pageId, String slug) {
        Assert.assertEquals(page.id(), pageId, "API returned another page");
        Assert.assertEquals(page.slug(), slug, "Unexpected slug");
        Assert.assertFalse(page.seo().title().isBlank(), "SEO title should be filled");
        Assert.assertFalse(page.seo().noIndex(), "Product page must stay indexable");
        return this;
    }

    @Step("Breadcrumbs should lead from home to {slug}")
    public ContentApiSteps breadcrumbsShouldLeadTo(ContentPage page, String slug) {
        List<Link> items = page.breadcrumbs().items();
        Assert.assertTrue(items.size() > 1 && items.size() <= TestConstants.MAX_BREADCRUMBS,
                "Unexpected breadcrumb depth " + items.size());
        Assert.assertEquals(items.get(0).url(), "/", "First breadcrumb should be home");
        Assert.assertEquals(items.get(items.size() - 1).url(), slug, "Last breadcrumb should be the page itself");
        items.forEach(item -> Assert.assertFalse(item.label().isBlank(), "Breadcrumb " + item.key() + " has no label"));
        return this;
    }

    @Step("Hero section should promote the product with benefits and a call to action")
    public SectionInputs heroSectionShouldBeComplete(ContentPage page, String slug) {
        SectionInputs hero = page.firstSectionOfType(TestConstants.CTA_SECTION_TYPE)
                .orElseThrow(() -> new AssertionError("Page has no CTA section"))
                .inputs();
        Assert.assertFalse(hero.headingText().isBlank(), "Hero heading should be filled");
        Assert.assertTrue(hero.showList(), "Benefit list should be visible");
        Assert.assertFalse(hero.list().isEmpty(), "Hero should list product benefits");
        hero.list().forEach(item -> Assert.assertFalse(item.label().isBlank(), "Benefit " + item.key() + " is empty"));
        Link link = hero.primaryButton().link();
        Assert.assertFalse(link.isExternal(), "Primary action should stay on tbcbank.ge");
        Assert.assertTrue(link.url().startsWith(slug), "Primary action should open a sub page of " + slug);
        return hero;
    }

    @Step("Localized pages should share structure but differ in text")
    public ContentApiSteps localizedPagesShouldDiffer(ContentPage english, ContentPage georgian) {
        Assert.assertEquals(english.id(), georgian.id(), "Both locales should come from the same page entry");
        Assert.assertEquals(english.slug(), georgian.slug(), "Slug should not depend on locale");
        Assert.assertEquals(english.breadcrumbs().items().size(), georgian.breadcrumbs().items().size(),
                "Breadcrumb depth should not depend on locale");
        Assert.assertNotEquals(english.seo().title(), georgian.seo().title(), "SEO title should be translated");
        return this;
    }

    @Step("API should report page {pageId} as not found")
    public ApiError notFoundShouldBeReported(Response response, String pageId) {
        Assert.assertEquals(response.statusCode(), HttpStatus.SC_NOT_FOUND, "Unknown page should return 404");
        Assert.assertTrue(response.contentType().contains(ContentType.JSON.toString())
                        || response.contentType().contains("problem+json"),
                "Error should be JSON, was " + response.contentType());
        ApiError error = response.as(ApiError.class);
        Assert.assertEquals(error.status(), HttpStatus.SC_NOT_FOUND, "Error body status");
        Assert.assertEquals(error.type(), TestConstants.NOT_FOUND_EXCEPTION, "Error type");
        Assert.assertEquals(error.code(), TestConstants.NOT_FOUND_EXCEPTION, "Error code");
        Assert.assertTrue(error.detail().contains(pageId), "Error detail should name the page: " + error.detail());
        Assert.assertFalse(error.traceId().isBlank(), "Error should carry a trace id");
        return error;
    }
}
