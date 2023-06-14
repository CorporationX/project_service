package faang.school.projectservice.service.util;

import faang.school.projectservice.dto.client.Currency;
import java.math.BigDecimal;

public class CurrencyConverter {

    public static BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == Currency.USD && to == Currency.EUR) {
            return amount.multiply(BigDecimal.valueOf(0.85));
        }
        if (from == Currency.EUR && to == Currency.USD) {
            return amount.multiply(BigDecimal.valueOf(1.18));
        }
        return amount;
    }
}
