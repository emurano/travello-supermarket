package ericmurano.checkout;

/**
 * Represents an item that can be scanned by Checkout
 * and that contributes to the total of the Checkout
 */
public interface Item {

    /**
     * The unique identifier of the product that the item is of
     * @return the SKU of the Item
     */
    String sku();
}
