package ericmurano.checkout.multipriced;

import ericmurano.checkout.Checkout;
import ericmurano.checkout.Item;
import ericmurano.checkout.Price;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return pricingRules()
            .stream()
            .filter(rule -> Objects.equals(rule.sku(), skuCount.sku()))
            .filter(rule -> rule.quantity() <= skuCount.count())
            .min((rule1, rule2) -> rule2.quantity().compareTo(rule1.quantity()))
            .map(rule -> {
                long remainingCount = skuCount.count() - rule.quantity();
                if (remainingCount > 0) {
                    return rule.price().add(
                        calculateSkuSubTotal(
                            new SkuCount(skuCount.sku(), remainingCount)
                        )
                    );
                } else {
                    return rule.price();
                }
            })
            .orElseThrow(() -> new NoPricingRuleForQuantityException(
                skuCount.sku(),
                skuCount.count()
            ));
    }

    private Collection<PricingRule> pricingRules() {
        return pricingRules
            .stream()
            .filter(rule -> Objects.nonNull(rule.price()))
            .filter(rule -> Objects.nonNull(rule.quantity()))
            .collect(Collectors.toMap(
                pricingRule -> new PriceKey(pricingRule),
                Function.identity(),
                (existing, candidate) -> candidate.price().compareTo(existing.price()) <= 0
                    ? candidate
                    : existing)
            )
            .values();
    }

    private Set<SkuCount> scannedSkuCounts() {
        HashSet<SkuCount> counts = new HashSet<>();
        scannedItems
            .stream()
            .collect(Collectors.groupingBy(Item::sku, Collectors.counting()))
            .forEach((sku, count) -> counts.add(new SkuCount(sku, count)));
        return counts;
    }

    private class SkuCount {
        private final Long count;
        private final String sku;

        public SkuCount(String sku, Long count) {
            this.count = count;
            this.sku = sku;
        }

        public Long count() {
            return count;
        }

        public String sku() {
            return sku;
        }
    }

    private class PriceKey {
        private final String sku;
        private final Integer quantity;

        public PriceKey(PricingRule pricingRule) {
            this.sku = pricingRule.sku();
            this.quantity = pricingRule.quantity();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PriceKey)) return false;

            PriceKey priceKey = (PriceKey) o;

            if (!sku.equals(priceKey.sku)) return false;
            return quantity.equals(priceKey.quantity);
        }

        @Override
        public int hashCode() {
            int result = sku.hashCode();
            result = 31 * result + quantity.hashCode();
            return result;
        }
    }
}