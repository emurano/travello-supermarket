package ericmurano.checkout.multipriced;

import ericmurano.checkout.Price;
import java.math.BigDecimal;

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
