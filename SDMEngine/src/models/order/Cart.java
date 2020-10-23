package models.order;

import models.inventoryitem.InventoryItem;
import models.inventoryitem.ePurchaseCategory;
import models.store.DiscountOffer;
import models.store.Store;
import models.store.StoreDiscount;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cart {

    private final String TAG = "Cart";

    private Set<CartItem> cartItems;
    private Set<UsedDiscount> usedDiscounts;
    private double subtotal;
    private int totalNumberItems;
    private int totalNumberItemsByType;

    public Cart(){
        cartItems = new HashSet<>();
        usedDiscounts = new HashSet<>();
        subtotal = 0.0;
        totalNumberItems = 0;
        totalNumberItemsByType = 0;
    }

    public Set<CartItem> getCartItems() {
        return cartItems;
    }

    public Set<UsedDiscount> getUsedDiscounts() {
        return usedDiscounts;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public int getTotalNumberItems() {
        return totalNumberItems;
    }

    public int getTotalNumberItemsByType() {
        return totalNumberItemsByType;
    }

    //TODO: Use only version that uses itemId as parameter
    public void addCartItem(InventoryItem item, double quantity, int price, Store store){
        subtotal += quantity*price;
        if (item.getPurchaseCategory()==ePurchaseCategory.QUANTITY){
            this.totalNumberItems += quantity;
        } else{
            this.totalNumberItems++;
        }

        CartItem itemSearchedFor = searchCartItems(item);
        if (itemSearchedFor != null){
            itemSearchedFor.increaseAmount(quantity);
            return;
        }
        cartItems.add(new CartItem(item,quantity,price,store));
        totalNumberItemsByType++;
    }

    public void addCartItem(int id, double amount, Store store) {
        InventoryItem inventoryItem = store.getInventoryItemById(id);
        int price = store.getMapItemsToPrices().get(inventoryItem);
        subtotal += amount*price;

        if (inventoryItem.getPurchaseCategory()==ePurchaseCategory.QUANTITY){
            this.totalNumberItems += amount;
        } else{
            this.totalNumberItems++;
        }

//        CartItem itemSearchedFor = searchCartItems(inventoryItem);
        CartItem itemSearchedFor = cartItems.stream().filter(cartItem -> cartItem.getItemID()==id).findFirst().orElse(null);

        if (itemSearchedFor != null){
            itemSearchedFor.increaseAmount(amount);
            return;
        }
        cartItems.add(new CartItem(inventoryItem,amount,price,store));
        totalNumberItemsByType++;
    }

    //TODO: Use only one searchCartItems method (either for inventoryItem or cartItem)
    private CartItem searchCartItems(InventoryItem item) {
        for (CartItem cartItem: cartItems){
            if (cartItem.equals(item))
                return cartItem;
        }
        return null;
    }

    private CartItem searchForCartItem(CartItem itemToAdd) {
        return cartItems.stream().filter(item->item.getItemID()==itemToAdd.getItemID()).findFirst().orElse(null);
    }

    public void addItemToCart(CartItem itemToAdd){
        //System.out.println(TAG + " - LINE 74");

        double increaseInCartSubtotal = (itemToAdd.getAmount()*itemToAdd.getPrice());
        subtotal += increaseInCartSubtotal;

        //System.out.println(TAG + " - LINE 79");
        if (itemToAdd.getPurchaseCategory()== ePurchaseCategory.QUANTITY){
            this.totalNumberItems += itemToAdd.getAmount();
        } else{
            this.totalNumberItems++;
        }

        CartItem item = searchForCartItem(itemToAdd);
        //System.out.println(TAG + " - LINE 87");

        if (item != null){
            double oldAmount = item.getAmount();
            double newAmount = oldAmount + itemToAdd.getAmount();
            item.setAmount(newAmount);
            return;
        }

        cartItems.add(itemToAdd);
        this.totalNumberItemsByType++;
    }




    public void addDiscountUsedForParams(StoreDiscount discount, Set<DiscountOffer> offersUsed) {
        String discountName = discount.getDiscountName();
        String storeName = discount.getStore().getStoreName();
        System.out.println(TAG + " - addDiscountUsedForParams() called with args discountName=" + discountName + ", storeName= " + storeName);
        for (UsedDiscount discountUsed: usedDiscounts){
            //System.out.println(TAG + " LINE 122");
            boolean areSameOffersChosen = compareOffersLists(discountUsed.getOffersChosen(), offersUsed);
          //  System.out.println(TAG + " LINE 124");
            if (discountUsed.getDiscountName().equals(discountName) && discountUsed.getStore().getStoreName().equals(storeName) && areSameOffersChosen){
                System.out.println("Discount already in discountUsedList, incrementing Times used");
                discountUsed.incrementTimesUsed();
                return;
            }
        }

        //System.out.println(TAG + " LINE 131");
        usedDiscounts.add(new UsedDiscount(discount,offersUsed));
//        System.out.println(TAG + " 134");
//        System.out.println(TAG + " usedDiscounts is now: " + usedDiscounts);
    }

    //TODO: Change offersUsed to Set<>
    private boolean compareOffersLists(Set<DiscountOffer> offersUsed1, Set<DiscountOffer> offersUsed2) {
        if (offersUsed1.size() != offersUsed2.size())
            return false;
        for (DiscountOffer offer: offersUsed1){
            if (!offersUsed2.contains(offer))
                return false;
        }
        for (DiscountOffer offer: offersUsed2){
            if (!offersUsed1.contains(offer))
                return false;
        }

        return true;
    }

    public HashMap<String,Integer> getMapDiscountNamesToTimesUsed(){
        HashMap<String,Integer> res = new HashMap<>();

        for (UsedDiscount usedDiscount: usedDiscounts){
            String discountName = usedDiscount.getDiscountName();
            int timesUsed = usedDiscount.getTimesUsed();
            if (!res.containsKey(discountName)){
                res.put(discountName,timesUsed);
            }else{
                int val = res.get(discountName);
                val += timesUsed;
                res.put(discountName,val);
            }
        }
        return res;
    }


    @Override
    public String toString() {
        return "Cart{" +
                "cartItems=" + cartItems +
                ", usedDiscounts=" + usedDiscounts +
                '}';
    }
}
