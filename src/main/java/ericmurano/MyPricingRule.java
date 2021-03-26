package ericmurano;

import ericmurano.checkout.multipriced.PricingRule;

import java.math.BigDecimal;

public class MyPricingRule implements PricingRule {
    private final String sku;
    private final BigDecimal price;
    private final Integer quantity;

    public MyPricingRule(String sku, BigDecimal price, Integer quantity) {
        this.sku = sku;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String sku() {
        return sku;
    }

    @Override
    public BigDecimal price() {
        return price;
    }

    @Override
    public Integer quantity() {
        return quantity;
    }
}
