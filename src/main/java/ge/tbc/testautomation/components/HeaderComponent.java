package ge.tbc.testautomation.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HeaderComponent extends BaseComponent {
    public static final String MEGA_MENU_HIDDEN_CLASS = "tbcx-pw-header__mega-menu--hidden";

    public final Locator logoLink;
    public final Locator navigationItems;
    public final Locator megaMenu;
    public final Locator visibleSubmenu;
    public final LanguageSwitcherComponent languageSwitcher;

    public HeaderComponent(Page page) {
        super(page, page.locator("tbcx-pw-header"));
        logoLink = root.locator(".tbcx-pw-header__logo a");
        navigationItems = root.locator("tbcx-pw-navigation .tbcx-pw-navigation-item__link");
        megaMenu = root.locator(".tbcx-pw-header__mega-menu");
        visibleSubmenu = megaMenu.locator(".tbcx-pw-mega-menu__submenu:not(.tbcx-pw-mega-menu__submenu--hidden)");
        languageSwitcher = new LanguageSwitcherComponent(page, root);
    }

    public Locator navigationItem(int index) {
        return navigationItems.nth(index);
    }

    public Locator megaMenuLink(String href) {
        return visibleSubmenu.locator(".show-tablet-up a[href='" + href + "']");
    }

    public void hoverNavigationItem(int index) {
        navigationItem(index).hover();
    }
}
