package ge.tbc.testautomation.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AmountParser {
    private AmountParser() {
    }

    public static double convert(double amount, double rate) {
        return BigDecimal.valueOf(amount)
                .multiply(BigDecimal.valueOf(rate))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static String plain(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
