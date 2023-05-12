package io.github.mystievous.mystigui.page;

public class TooManyItemsException extends Exception {
    public TooManyItemsException(int itemNum) {
        super(itemNum + " is too many elements to fit in an inventory.");
    }
}
