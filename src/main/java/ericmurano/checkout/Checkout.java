package ericmurano.checkout;

/**
 * Builds a collection of items and produces a total
 * price of the items.
 *
 * Implementations of this interface may have pricing
 * logic that handles price bundling or discounting
 */
public interface Checkout {

    /**
     * Adds a purchasable item to the Checkout that
     * contributes to the Checkout's total
     *
     * @param item The item to add to the Checkout
     */
    void scan(Item item);

    /**
     * Calculates the total price of the checkout at that
     * point in time, applying all bundling and discounting
     * rules that have been defined for the Checkout.
     *
     * @return the total price of the checkout
     */
    Price total();
}
