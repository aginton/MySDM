package models.order;

import models.inventoryitem.InventoryItem;
import models.store.Store;

import java.util.Objects;

public class CartItem extends InventoryItem {

    int price;
    double amount;
    Store store;

    public CartItem(InventoryItem item, double amount, int price, Store store){
        super(item);
        this.amount = amount;
        this.price = price;
        this.store = store;
    }

    public int getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }

    public Store getStore() {
        return store;
    }

    public void increaseAmount(double i) {
        double oldAmount = this.amount;
        this.amount = oldAmount+i;
    }

    public void decreaseAmount(double i) {
        double oldAmount = this.amount;
        if (oldAmount-i <0){
            this.amount=0;
            return;
        }
        this.amount = oldAmount-i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        if (!super.equals(o)) return false;
        CartItem cartItem = (CartItem) o;
        return price == cartItem.price &&
                Objects.equals(store, cartItem.store);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), price, store);
    }

    public void setAmount(double newAmount) {
        this.amount=newAmount;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "price=" + price +
                ", amount=" + amount +
                ", store=" + store +
                '}';
    }
}
