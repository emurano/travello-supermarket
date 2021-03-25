package ericmurano.checkout.multipriced;

/**
 * A rule that is used to determine how much something costs
 */
public interface PricingRule {

    /**
     * The unique identifier of the item that the pricing rule applies to
     * @return The SKU of the item the pricing rule is for
     */
    String sku();
}
