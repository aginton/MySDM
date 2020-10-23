package models.store;

import models.inventoryitem.InventoryItem;

public class DiscountCondition {
    protected double quantity;
    protected InventoryItem ifYouBuyItem;

    public DiscountCondition(InventoryItem ifYouBuyItem, double quantity){
        this.ifYouBuyItem = ifYouBuyItem;
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public InventoryItem getIfYouBuyItem() {
        return ifYouBuyItem;
    }

    @Override
    public String toString() {
        return "DiscountCondition{" +
                "ifYouBuy quantity=" + quantity +
                " of " + ifYouBuyItem.getItemName() +
                '}';
    }
}
