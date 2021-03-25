package ericmurano.checkout.multipriced;

import ericmurano.checkout.Item;
import ericmurano.checkout.Price;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

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
    public void scan_oneItemProvidedButNoRulesDefinedForItem_totalMethodReturnsValuePrice() {
        PricingRule rule = mock(PricingRule.class);
        when(rule.sku()).thenReturn("BUTTER");
        checkout = new MultiPricedCheckout(
            new HashMap<String, PricingRule>() {{ put("BUTTER", rule); }}
        );

        Item item = mock(Item.class);
        when(item.sku()).thenReturn("BREAD");
        checkout.scan(item);


        Price price = checkout.total();


        assertEquals(BigDecimal.ZERO, price.amount());
    }
}