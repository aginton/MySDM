package models.user;

import models.order.Cart;
import models.order.Order;
import models.order.UsedDiscount;
import models.store.DiscountOffer;
import models.store.Store;
import models.store.StoreDiscount;
import models.zone.Zone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Customer extends User{
    private final String TAG = "Customer";

    protected List<Order> orders;
    protected Cart currentCart;

    public Customer(String name, String role){
        super(name,role);
        this.orders = new ArrayList<>();
        this.currentCart = new Cart();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Cart getCurrentCart() {
        return currentCart;
    }

    public void addDiscountBasedOnParams(Zone zone, String discountName, String storeName, String discountOperator, String offerChosen) {
        System.out.println(TAG + " - addDiscountBasedOnParams started, received discountName= " + discountName + ", storeName= " + storeName + ", operator= " + discountOperator
                + ", offerChosen= " + offerChosen);

        Store store = zone.getStoreByName(storeName);
        StoreDiscount discount = store.getDiscountByName(discountName);
        Set<DiscountOffer> offersUsed = new HashSet<>();

        if (discountOperator.equals("ALL-OR-NOTHING")){
            for (DiscountOffer offer: discount.getDiscountOffers()){
                offersUsed.add(offer);
            }
        } else{
            DiscountOffer offer = discount.getOfferDetailsByItemName(offerChosen);
            offersUsed.add(offer);
        }
        currentCart.addDiscountUsedForParams(discount,offersUsed);
    }

    public void addOrderToCustomer(Order order) {
        orders.add(order);
        double balanceBefore = balance;
        balance = balanceBefore - order.getTotalOrderCost();
        transactions.add(new Transaction("withdrawal", order.getOrderDate(), order.getTotalOrderCost(), balanceBefore,balance));
    }

    @Override
    public String toString() {
        return "Customer{" +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", regularItems=" + currentCart.getCartItems().size() +
                ", discounts=" + currentCart.getUsedDiscounts().size() +
                "orders=" + orders +
                '}';
    }

    public void decrementDiscountUsed(int discountRefToDecrement) {
        System.out.println(TAG + " - entered decrementDiscountUsed");
        UsedDiscount d = null;

        for (UsedDiscount discountUsed : currentCart.getUsedDiscounts()) {
            if (discountUsed.getDiscountUsedRef() == discountRefToDecrement) {
                d = discountUsed;
                System.out.println(TAG + " LINE 204");
            }
        }
        if (d != null) {
            if (d.getTimesUsed() - 1 <= 0) {
                System.out.println(TAG + " LINE 208");
                currentCart.getUsedDiscounts().remove(d);
                System.out.println(TAG + " LINE 209");
            } else {
                System.out.println(TAG + " LINE 211");
                d.decrementTimesUsed();
                int oldval = currentCart.getMapDiscountNamesToTimesUsed().get(d.getDiscountName());
                System.out.println(TAG + " LINE 213");
            }
        }
    }

    public void emptyCartInProgress() {
        this.currentCart = new Cart();
    }
}
