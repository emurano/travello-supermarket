package ericmurano.checkout;

import java.math.BigDecimal;

/**
 * A monetary type used by Checkout to represent the
 * price of something or the total of a collection
 * of prices
 */
public interface Price {
    BigDecimal amount();
}
