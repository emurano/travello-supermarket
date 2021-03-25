package ericmurano.checkout.multipriced;

import ericmurano.checkout.Item;
import ericmurano.checkout.Price;
import org.junit.After;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MultiPricedCheckoutTest {

    private MultiPricedCheckout checkout;

    @After
    public void tearDown() {
        checkout = null;
    }

    @Test
    public void scan_nullPricesProvided_totalMethodReturnsNonNull() {
        checkout = new MultiPricedCheckout(null);

        Price price = checkout.total();

        assertNotNull(price);
    }

    @Test
    public void scan_nullPricesProvided_totalMethodReturnsZeroValuePrice() {
        checkout = new MultiPricedCheckout(null);

        Price price = checkout.total();

        assertEquals(BigDecimal.ZERO, price.amount());
    }

    @Test
    public void scan_emptyPricesProvided_totalMethodReturnsZeroValuePrice() {
        checkout = new MultiPricedCheckout(Collections.emptyMap());

        Price price = checkout.total();

        assertEquals(BigDecimal.ZERO, price.amount());
    }

    @Test
    public void scan_oneItemProvidedButNoRulesDefinedForItem_totalMethodReturnsZero() {
        PricingRule rule = mockPricingRule("BUTTER", BigDecimal.valueOf(123.45));
        Item item = mockItem("BREAD");
        checkout = new MultiPricedCheckout(pricingRuleMap(rule));
        checkout.scan(item);

        Price price = checkout.total();

        assertEquals(BigDecimal.ZERO, price.amount());
    }

    @Test
    public void scan_oneItemProvidedButWithOneRuleDefinedForSku_totalMethodReturnsSkuPrice() {
        PricingRule rule = mockPricingRule("BUTTER", BigDecimal.valueOf(123.45));
        Item item = mockItem("BUTTER");
        checkout = new MultiPricedCheckout(pricingRuleMap(rule));
        checkout.scan(item);

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(123.45), price.amount());
    }

    @Test
    public void scan_twoItemSameSkuProvidedWithOneRuleDefinedForSku_totalMethodReturnsSkuPriceTimesTwo() {
        PricingRule rule = mockPricingRule("BUTTER", BigDecimal.valueOf(75));
        Item item1 = mockItem("BUTTER");
        Item item2 = mockItem("BUTTER");
        checkout = new MultiPricedCheckout(pricingRuleMap(rule));
        checkout.scan(item1);
        checkout.scan(item2);

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(150), price.amount());
    }

    @Test
    public void scan_twoItemDifferentSkuProvidedWithRulesDefinedForEach_totalMethodReturnsSingleSkuPricesSummed() {
        PricingRule rule1 = mockPricingRule("BUTTER", BigDecimal.valueOf(75));
        PricingRule rule2 = mockPricingRule("BREAD", BigDecimal.valueOf(23.45));
        Item item1 = mockItem("BUTTER");
        Item item2 = mockItem("BREAD");
        checkout = new MultiPricedCheckout(pricingRuleMap(rule1, rule2));
        checkout.scan(item1);
        checkout.scan(item2);

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(98.45), price.amount());
    }

    private PricingRule mockPricingRule(String sku, BigDecimal price) {
        PricingRule rule = mock(PricingRule.class);
        when(rule.sku()).thenReturn(sku);
        when(rule.price()).thenReturn(price);
        return rule;
    }

    private Map<String, PricingRule> pricingRuleMap(PricingRule... rules) {
        Map<String, PricingRule> map = new HashMap<>();
        Arrays
            .stream(rules)
            .forEach(pricingRule -> map.put(pricingRule.sku(), pricingRule));
        return map;
    }

    private Item mockItem(String sku) {
        Item item = mock(Item.class);
        when(item.sku()).thenReturn(sku);
        return item;
    }
}