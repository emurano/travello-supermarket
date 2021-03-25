package ericmurano.checkout.multipriced;

import ericmurano.checkout.Price;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MultiPricedCheckoutTest {

    private MultiPricedCheckout checkout;

    @After
    public void tearDown() throws Exception {
        checkout = null;
    }

    @Test
    public void scan_nullPricesProvided_totalMethodReturnsNotNull() {
        checkout = new MultiPricedCheckout(null);

        Price price = checkout.total();

        assertNotNull(price);
    }
}