package ericmurano.checkout.multipriced;

import ericmurano.checkout.Checkout;
import ericmurano.checkout.Item;
import ericmurano.checkout.Price;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Checkout that discounts items based on the number of the same item that has
 * been scanned
 */
public class MultiPricedCheckout implements Checkout {

    private final Set<PricingRule> pricingRules;
    private final ArrayList<Item> scannedItems;

    public MultiPricedCheckout(Set<PricingRule> pricingRules) {
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

        final BigDecimal[] grandTotal = {BigDecimal.ZERO};

        scannedSkuTotals()
            .forEach((sku, numItems) -> {
                 BigDecimal price = pricingRules
                     .stream()
                     .filter(rule -> Objects.equals(rule.sku(), sku))
                     .filter(rule -> rule.quantity() >= numItems)
                     .sorted((o1, o2) -> o1.quantity().compareTo(o1.quantity()))
                     .findFirst()
                     .map(rule -> rule.price())
                     .orElse(BigDecimal.ZERO);

                grandTotal[0] = grandTotal[0].add(price);
            });

        return new ImmutablePrice(grandTotal[0]);

//        BigDecimal total = scannedItems
//            .stream()
//            .map(item -> pricingRules
//                .stream()
//                .filter(rule -> Objects.equals(rule.sku(), item.sku()))
//                .map(PricingRule::price)
//                .findFirst()
//                .orElse(BigDecimal.ZERO)
//            )
//            .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        return new ImmutablePrice(total);
    }

    private Map<String, Long> scannedSkuTotals() {
        return scannedItems
            .stream()
            .collect(Collectors.groupingBy(Item::sku, Collectors.counting()));
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