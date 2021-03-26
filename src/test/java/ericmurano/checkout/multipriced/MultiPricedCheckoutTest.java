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
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(123.45), 1)
        ));
        checkout.scan(mockItem("BREAD"));

        checkout.total();
    }

    @Test
    public void scan_oneItemProvidedButWithOneRuleDefinedForSku_totalMethodReturnsSkuPrice() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(123.45), 1)
        ));
        checkout.scan(mockItem("BUTTER"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(123.45), price.amount());
    }

    @Test
    public void scan_twoItemSameSkuProvidedWithOneRuleDefinedForSku_totalMethodReturnsSkuPriceTimesTwo() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1)
        ));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BUTTER"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(150), price.amount());
    }

    @Test
    public void scan_twoItemDifferentSkuProvidedWithRulesDefinedForEach_totalMethodReturnsSingleSkuPricesSummed() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1)
        ));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BUTTER"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(150), price.amount());
    }

    @Test
    public void scan_twoItemsScanned_totalMethodReturnsSpecialPrice() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1),
            mockPricingRule("BREAD", BigDecimal.valueOf(23.45), 1)
        ));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BREAD"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(98.45), price.amount());
    }

    @Test
    public void scan_oneSkuScannedTwiceWithTwoItemSpecialPrice_totalMethodReturnsSpecialPrice() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1),
            mockPricingRule("BUTTER", BigDecimal.valueOf(80), 2)
        ));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BUTTER"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(80), price.amount());
    }

    @Test
    public void scan_oneSkuScannedThreeTimesWithTwoItemSpecialPrice_totalMethodReturnsSpecialPricePlusSinglePrice() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1),
            mockPricingRule("BUTTER", BigDecimal.valueOf(80), 2)
        ));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BUTTER"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(155), price.amount());
    }

    @Test(expected = NoPricingRuleForQuantityException.class)
    public void total_NoPricingRuleForItemCount_ThrowsException() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(80), 2)
        ));
        checkout.scan(mockItem("BUTTER"));

        checkout.total();
    }

    @Test
    public void total_pricingRuleQuantityNull_notUsedInTotal() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(75), null),
            mockPricingRule("BUTTER", BigDecimal.valueOf(80), 1)
        ));
        checkout.scan(mockItem("BUTTER"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(80), price.amount());
    }

    @Test
    public void total_pricingRulePriceNull_notUsedInTotal() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(75), 1),
            mockPricingRule("BUTTER", null, 2)
        ));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BUTTER"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(150), price.amount());
    }

    @Test
    public void total_enoughScansForTwoSpecialPrices_appliesSpecialTwice() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("BUTTER", BigDecimal.valueOf(100), 1),
            mockPricingRule("BUTTER", BigDecimal.valueOf(75), 2)
        ));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BUTTER"));
        checkout.scan(mockItem("BUTTER"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(150), price.amount());
    }

    @Test
    public void total_manyScansManyPrices_TotalReturnsExpected() {
        checkout = new MultiPricedCheckout(pricingRuleSet(
            mockPricingRule("A", new BigDecimal("15"), 1),
            mockPricingRule("A", new BigDecimal("25"), 2),
            mockPricingRule("B", new BigDecimal("100"), 1),
            mockPricingRule("B", new BigDecimal("250"), 3),
            mockPricingRule("B", new BigDecimal("800"), 10),
            mockPricingRule("C", new BigDecimal("23.45"), 1)
        ));
        checkout.scan(mockItem("A"));
        checkout.scan(mockItem("A"));
        checkout.scan(mockItem("B"));
        checkout.scan(mockItem("B"));
        checkout.scan(mockItem("C"));
        checkout.scan(mockItem("A"));
        checkout.scan(mockItem("B"));
        checkout.scan(mockItem("B"));
        checkout.scan(mockItem("C"));
        checkout.scan(mockItem("A"));
        checkout.scan(mockItem("C"));
        checkout.scan(mockItem("A"));

        Price price = checkout.total();

        assertEquals(BigDecimal.valueOf(485.35), price.amount());
    }

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