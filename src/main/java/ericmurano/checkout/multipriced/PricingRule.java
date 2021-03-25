package ericmurano.checkout.multipriced;

import java.math.BigDecimal;

/**
 * A rule that is used to determine how much something costs
 */
public interface PricingRule {

    /**
     * The unique identifier of the item that the pricing rule applies to
     * @return The SKU of the item the pricing rule is for
     */
    String sku();

    /**
     * The price of a single item of this sku
     * @return The price
     */
    BigDecimal price();
}
