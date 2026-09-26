package ge.tbc.testautomation.tests;

import com.microsoft.playwright.Page;
import ge.tbc.testautomation.components.CookieBannerComponent;
import ge.tbc.testautomation.utils.PlaywrightManager;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

public abstract class BaseUiTest {

    @BeforeMethod(alwaysRun = true)
    public void startPage() {
        Page page = PlaywrightManager.startPage();
        if (rejectCookiesAutomatically()) {
            new CookieBannerComponent(page).rejectWheneverShown();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closePage(ITestResult result) {
        try {
            if (!result.isSuccess()) {
                byte[] screenshot = page().screenshot(new Page.ScreenshotOptions().setFullPage(false));
                Allure.addAttachment(result.getName(), "image/png", new ByteArrayInputStream(screenshot), "png");
            }
        } finally {
            PlaywrightManager.closePage();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void shutdownPlaywright() {
        PlaywrightManager.shutdown();
    }

    protected boolean rejectCookiesAutomatically() {
        return true;
    }

    protected Page page() {
        return PlaywrightManager.page();
    }
}
