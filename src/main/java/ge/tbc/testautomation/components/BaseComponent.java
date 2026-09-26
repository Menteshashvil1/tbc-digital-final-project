package ge.tbc.testautomation.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public abstract class BaseComponent {
    protected final Page page;
    protected final Locator root;

    protected BaseComponent(Page page, Locator root) {
        this.page = page;
        this.root = root;
    }

    public Locator root() {
        return root;
    }
}
