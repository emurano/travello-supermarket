package ericmurano.checkout.multipriced;

import ericmurano.checkout.Item;
import ericmurano.checkout.Price;
import org.junit.After;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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
        checkout = new MultiPricedCheckout(Collections.emptySet());

        Price price = checkout.total();

        assertEquals(BigDecimal.ZERO, price.amount());
    }

    @Test(expected = NoPricingRuleForQuantityException.class)
    public void scan_oneItemProvidedButNoRulesDefinedForItem_throwsException() {
        PricingRule rule = mockPricingRule("BUTTER", BigDecimal.valueOf(123.45), 1);
        Item item = mockItem("BREAD");
        checkout = new MultiPricedCheckout(pricingRuleSet(rule));
        checkout.scan(item);

        checkout.total();
    }

    @Test
    public void scan_oneItemProvidedButWithOneRuleDefinedForSku_totalMethodReturnsSkuPrice() {
        PricingRule rule = mockPricingRule("BUTTER", BigDecimal.valueOf(123.45), 1);
        Item item = mockItem("BUTTER");
        checkout = new MultiPricedCheckout(pricingRuleSet(rule));
        checkout.scan(item);

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(123.45), price.amount());
    }

    @Test
    public void scan_twoItemSameSkuProvidedWithOneRuleDefinedForSku_totalMethodReturnsSkuPriceTimesTwo() {
        PricingRule rule = mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1);
        Item item1 = mockItem("BUTTER");
        Item item2 = mockItem("BUTTER");
        checkout = new MultiPricedCheckout(pricingRuleSet(rule));
        checkout.scan(item1);
        checkout.scan(item2);

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(150), price.amount());
    }

    @Test
    public void scan_twoItemDifferentSkuProvidedWithRulesDefinedForEach_totalMethodReturnsSingleSkuPricesSummed() {
        PricingRule rule1 = mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1);
        Item item1 = mockItem("BUTTER");
        Item item2 = mockItem("BUTTER");
        checkout = new MultiPricedCheckout(pricingRuleSet(rule1));
        checkout.scan(item1);
        checkout.scan(item2);

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(150), price.amount());
    }

    @Test
    public void scan_twoItemsScanned_totalMethodReturnsSpecialPrice() {
        PricingRule rule1 = mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1);
        PricingRule rule2 = mockPricingRule("BREAD", BigDecimal.valueOf(23.45), 1);
        Item item1 = mockItem("BUTTER");
        Item item2 = mockItem("BREAD");
        checkout = new MultiPricedCheckout(pricingRuleSet(rule1, rule2));
        checkout.scan(item1);
        checkout.scan(item2);

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(98.45), price.amount());
    }

    @Test
    public void scan_oneSkuScannedTwiceWithTwoItemSpecialPrice_totalMethodReturnsSpecialPrice() {
        PricingRule rule1 = mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1);
        PricingRule rule2 = mockPricingRule("BUTTER", BigDecimal.valueOf(80), 2);
        Item item1 = mockItem("BUTTER");
        Item item2 = mockItem("BUTTER");
        checkout = new MultiPricedCheckout(pricingRuleSet(rule1, rule2));
        checkout.scan(item1);
        checkout.scan(item2);

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(80), price.amount());
    }

    @Test
    public void scan_oneSkuScannedThreeTimesWithTwoItemSpecialPrice_totalMethodReturnsSpecialPricePlusSinglePrice() {
        PricingRule rule1 = mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1);
        PricingRule rule2 = mockPricingRule("BUTTER", BigDecimal.valueOf(80), 2);
        Item item1 = mockItem("BUTTER");
        Item item2 = mockItem("BUTTER");
        Item item3 = mockItem("BUTTER");
        checkout = new MultiPricedCheckout(pricingRuleSet(rule1, rule2));
        checkout.scan(item1);
        checkout.scan(item2);
        checkout.scan(item3);

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(155), price.amount());
    }

    @Test(expected = NoPricingRuleForQuantityException.class)
    public void total_NoPricingRuleForItemCount_ThrowsException() {
        PricingRule rule = mockPricingRule("BUTTER", BigDecimal.valueOf(80), 2);
        Item item1 = mockItem("BUTTER");
        checkout = new MultiPricedCheckout(pricingRuleSet(rule));
        checkout.scan(item1);

        checkout.total();
    }

    // TODO - if pricing rule quantity is null
    // TODO - if pricing rule price is null

    private PricingRule mockPricingRule(
        String sku,
        BigDecimal price,
        Integer quantity
    ) {
        PricingRule rule = mock(PricingRule.class);
        when(rule.sku()).thenReturn(sku);
        when(rule.price()).thenReturn(price);
        when(rule.quantity()).thenReturn(quantity);
        when(rule.toString()).thenReturn(String.format("%s", price));
        return rule;
    }

    private Set<PricingRule> pricingRuleSet(PricingRule... rules) {
        return Arrays.stream(rules).collect(Collectors.toSet());
    }

    private Item mockItem(String sku) {
        Item item = mock(Item.class);
        when(item.sku()).thenReturn(sku);
        return item;
    }
}