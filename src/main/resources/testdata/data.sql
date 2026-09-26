MERGE INTO currency_conversion (id, scenario_name, sell_currency, buy_currency, amount, sell_symbol, active) KEY (id)
VALUES (1, 'US dollar small amount', 'USD', 'GEL', 100.00, '$', TRUE);

MERGE INTO currency_conversion (id, scenario_name, sell_currency, buy_currency, amount, sell_symbol, active) KEY (id)
VALUES (2, 'Euro medium amount', 'EUR', 'GEL', 250.00, '€', TRUE);

MERGE INTO currency_conversion (id, scenario_name, sell_currency, buy_currency, amount, sell_symbol, active) KEY (id)
VALUES (3, 'Pound sterling large amount', 'GBP', 'GEL', 1500.00, '£', TRUE);

MERGE INTO currency_conversion (id, scenario_name, sell_currency, buy_currency, amount, sell_symbol, active) KEY (id)
VALUES (4, 'Swiss franc fractional amount', 'CHF', 'GEL', 75.50, 'CHF', TRUE);

MERGE INTO currency_conversion (id, scenario_name, sell_currency, buy_currency, amount, sell_symbol, active) KEY (id)
VALUES (5, 'Turkish lira low rate currency', 'TRY', 'GEL', 1000.00, '₺', TRUE);

MERGE INTO currency_conversion (id, scenario_name, sell_currency, buy_currency, amount, sell_symbol, active) KEY (id)
VALUES (6, 'Japanese yen weighted rate', 'JPY', 'GEL', 5000.00, '¥', FALSE);
