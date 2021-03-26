package ericmurano.checkout.multipriced;

/**
 * Thrown when the checkout doesn't have a pricing rule for the
 * given SKU with the given number of items of that SKU scanned
 */
public class NoPricingRuleForQuantityException extends RuntimeException{
    public NoPricingRuleForQuantityException(
        String sku,
        Long scannedQuantity
    ) {
        super(String.format(
            "No pricing rule that covers %d %s with SKU %s",
            scannedQuantity,
            (scannedQuantity == 1 ? "item" : "items"),
            sku
        ));
    }
}
