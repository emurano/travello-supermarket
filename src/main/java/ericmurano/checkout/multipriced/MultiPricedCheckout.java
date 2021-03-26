package ericmurano.checkout.multipriced;

import ericmurano.checkout.Checkout;
import ericmurano.checkout.Item;
import ericmurano.checkout.Price;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
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

    /**
     * Calculates the total price of all scanned items
     * @return The summed price of all scanned items
     * @throws NoPricingRuleForQuantityException If there are no pricing rules
     * for a SKU and the number of times the SKU was scanned
     */
    @Override
    public Price total() {
        if (pricingRules == null) return new ImmutablePrice(BigDecimal.ZERO);
        if (pricingRules.isEmpty()) return new ImmutablePrice(BigDecimal.ZERO);

        return new ImmutablePrice(
            scannedSkuCounts()
                .stream()
                .map(this::calculateSkuSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    private BigDecimal calculateSkuSubTotal(SkuCount skuCount) {
        return pricingRules
            .stream()
            .filter(rule -> Objects.nonNull(rule.price()))
            .filter(rule -> Objects.nonNull(rule.quantity()))
            .filter(rule -> Objects.equals(rule.sku(), skuCount.getSku()))
            .filter(rule -> rule.quantity() <= skuCount.getCount())
            .min((rule1, rule2) -> rule2.quantity().compareTo(rule1.quantity()))
            .map(rule -> {
                long remainingCount = skuCount.getCount() - rule.quantity();
                if (remainingCount > 0) {
                    return rule.price().add(
                        calculateSkuSubTotal(
                            new SkuCount(skuCount.getSku(), remainingCount)
                        )
                    );
                } else {
                    return rule.price();
                }
            })
            .orElseThrow(() -> new NoPricingRuleForQuantityException(
                skuCount.getSku(),
                skuCount.getCount()
            ));
    }

    private Set<SkuCount> scannedSkuCounts() {
        HashSet<SkuCount> counts = new HashSet<>();
        scannedItems
            .stream()
            .collect(Collectors.groupingBy(Item::sku, Collectors.counting()))
            .forEach((sku, count) -> counts.add(new SkuCount(sku, count)));
        return counts;
    }
}

class SkuCount {
    private final Long count;
    private final String sku;

    public SkuCount(String sku, Long count) {
        this.count = count;
        this.sku = sku;
    }

    public Long getCount() {
        return count;
    }

    public String getSku() {
        return sku;
    }
}