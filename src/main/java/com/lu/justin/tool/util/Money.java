package com.lu.justin.tool.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {

    long value;
    Currency currency;

    public Money(long value) {
        this.value = value;
        this.currency = Currency.CNY;
    }

    public static Money fromCNY(BigDecimal m) {
        return new Money(m.multiply(BigDecimal.valueOf(Currency.CNY.times())).longValue());
    }

    public BigDecimal toCNY() {
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(Currency.CNY.times()), 4, RoundingMode.HALF_EVEN);
    }

    static enum Currency {
        CNY {
            @Override
            long times() {
                return 10000;
            }
        },
        USD {
            @Override
            long times() {
                return 10000;
            }
        };

        abstract long times();
    }
}
