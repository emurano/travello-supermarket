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
     * The price that is applied when the number of items reaches the
     * qualificationQuantity
     * @return The price to apply
     */
    BigDecimal price();

    /**
     * The number of items that need to be scanned before this price can be
     * applied
     * @return The quantity to test for
     */
    Integer quantity();
}
