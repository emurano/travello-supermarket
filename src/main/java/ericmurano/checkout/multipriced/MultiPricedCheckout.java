package ericmurano.checkout.multipriced;

import ericmurano.checkout.Checkout;
import ericmurano.checkout.Item;
import ericmurano.checkout.Price;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

/**
 * A Checkout that discounts items based on the number of the same item that has
 * been scanned
 */
public class MultiPricedCheckout implements Checkout {

    private final Map<String, PricingRule> pricingRules;
    private final ArrayList<Item> scannedItems;

    public MultiPricedCheckout(Map<String, PricingRule> pricingRules) {
        this.pricingRules = pricingRules;
        this.scannedItems = new ArrayList<>();
    }

    @Override
    public void scan(Item item) {
        scannedItems.add(item);
    }

    @Override
    public Price total() {
        if (pricingRules == null) return new ImmutablePrice(BigDecimal.ZERO);
        if (pricingRules.isEmpty()) return new ImmutablePrice(BigDecimal.ZERO);
        BigDecimal total = scannedItems
            .stream()
            .filter(item -> pricingRules.containsKey(item.sku()))
            .map(item -> pricingRules.get(item.sku()).price())
            .reduce(BigDecimal.ZERO, (subtotal, element) -> subtotal.add(element));

        return new ImmutablePrice(total);
    }
}

class ImmutablePrice implements Price {

    private final BigDecimal amount;

    public ImmutablePrice(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public BigDecimal amount() {
        return amount;
    }
}