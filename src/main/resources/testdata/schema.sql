CREATE TABLE IF NOT EXISTS currency_conversion (
    id            INT           PRIMARY KEY,
    scenario_name VARCHAR(100)  NOT NULL,
    sell_currency CHAR(3)       NOT NULL,
    buy_currency  CHAR(3)       NOT NULL,
    amount        DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    sell_symbol   NVARCHAR(3)   NOT NULL,
    active        BOOLEAN       NOT NULL DEFAULT TRUE
);
