package ge.tbc.testautomation.api.clients;

import ge.tbc.testautomation.api.models.ContentPage;
import ge.tbc.testautomation.api.models.Site;
import ge.tbc.testautomation.api.models.SitePage;
import ge.tbc.testautomation.constants.Endpoints;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.utils.ConfigReader;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

public class SiteContentClient extends BaseApiClient {

    @Step("GET site structure")
    public Response getSiteResponse() {
        return request()
                .pathParam("siteId", ConfigReader.siteId())
                .get(Endpoints.SITE);
    }

    public Site getSite() {
        return getSiteResponse()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(Site.class);
    }

    @Step("GET content page {pageId} for locale {locale}")
    public Response getPageResponse(String pageId, SiteLocale locale) {
        return request()
                .pathParam("pageId", pageId)
                .queryParam(Endpoints.LOCALE_PARAM, locale.code())
                .get(Endpoints.SITE_PAGE);
    }

    public ContentPage getPage(String pageId, SiteLocale locale) {
        return getPageResponse(pageId, locale)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(ContentPage.class);
    }

    public ContentPage getPageBySlug(String slug, SiteLocale locale) {
        SitePage sitePage = getSite().pageBySlug(slug)
                .orElseThrow(() -> new IllegalStateException("Site structure has no page with slug " + slug));
        return getPage(sitePage.id(), locale);
    }
}
