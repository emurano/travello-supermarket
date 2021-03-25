package ericmurano.checkout.multipriced;

import ericmurano.checkout.Checkout;
import ericmurano.checkout.Item;
import ericmurano.checkout.Price;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * A Checkout that discounts items based on the number of the same item that has
 * been scanned
 */
public class MultiPricedCheckout implements Checkout {

    private final Collection<PricingRule> pricingRules;

    public MultiPricedCheckout(Collection<PricingRule> pricingRules) {
        this.pricingRules = pricingRules;
    }

    @Override
    public void scan(Item item) {
        throw new RuntimeException("scan not yet implemented");
    }

    @Override
    public Price total() {
        return new Price() {
            @Override
            public BigDecimal amount() {
                return BigDecimal.ZERO;
            }
        };
    }
}
