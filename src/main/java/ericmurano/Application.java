package ericmurano;

import ericmurano.checkout.Checkout;
import ericmurano.checkout.Item;
import ericmurano.checkout.Price;
import ericmurano.checkout.multipriced.MultiPricedCheckout;
import ericmurano.checkout.multipriced.PricingRule;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Application {
    public static void main(String[] args) {
        System.out.println("== Supermarket Application ==");

        Set<PricingRule> pricingRules = new HashSet<PricingRule>() {{
            add(rule("A", "15.00"));
            add(rule("A", "25.00", 2));
            add(rule("B", "100"));
            add(rule("B", "250", 3));
            add(rule("B", "800", 10));
            add(rule("C", "23.45"));
        }};
        Checkout checkout = new MultiPricedCheckout(pricingRules);
        checkout.scan(item("A"));
        checkout.scan(item("A"));
        checkout.scan(item("B"));
        checkout.scan(item("B"));
        checkout.scan(item("C"));
        checkout.scan(item("A"));
        checkout.scan(item("B"));
        checkout.scan(item("B"));
        checkout.scan(item("C"));
        checkout.scan(item("A"));
        checkout.scan(item("C"));
        checkout.scan(item("A"));
        Price price = checkout.total();

        System.out.printf("Total checkout: %s%n", price.amount());
    }

    private static PricingRule rule(String sku, String price) {
        return new MyPricingRule(sku, new BigDecimal(price), 1);
    }

    private static PricingRule rule(String sku, String price, int quantity) {
        return new MyPricingRule(sku, new BigDecimal(price), quantity);
    }

    private static Item item(String sku) {
        return () -> sku;
    }
}
