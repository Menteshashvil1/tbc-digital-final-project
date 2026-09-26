package ge.tbc.testautomation.database.models;

import java.math.BigDecimal;

public class CurrencyConversion {
    private int id;
    private String scenarioName;
    private String sellCurrency;
    private String buyCurrency;
    private BigDecimal amount;
    private String sellSymbol;
    private boolean active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getSellCurrency() {
        return sellCurrency;
    }

    public void setSellCurrency(String sellCurrency) {
        this.sellCurrency = sellCurrency;
    }

    public String getBuyCurrency() {
        return buyCurrency;
    }

    public void setBuyCurrency(String buyCurrency) {
        this.buyCurrency = buyCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSellSymbol() {
        return sellSymbol;
    }

    public void setSellSymbol(String sellSymbol) {
        this.sellSymbol = sellSymbol;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String amountAsText() {
        return amount.stripTrailingZeros().toPlainString();
    }

    @Override
    public String toString() {
        return scenarioName + " [" + amountAsText() + " " + sellCurrency + " -> " + buyCurrency + "]";
    }
}
