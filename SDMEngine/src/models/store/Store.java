package models.store;

import models.inventoryitem.InventoryItem;
import models.order.*;
import models.user.Owner;
import models.user.Transaction;
import models.user.User;
import models.zone.Zone;
import resources.schema.jaxbgenerated.SDMDiscount;
import resources.schema.jaxbgenerated.SDMSell;
import resources.schema.jaxbgenerated.SDMStore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Store {
    private final String TAG = "Store";
    private Zone zone;
    private Owner owner;
    private int storeId;
    private String storeName;
    private int[] location;
    private int ppk;
    private double totalSalesIncome;
    private double totalDeliveryIncome;
    private Set<InventoryItem> storeItems;
    private Set<StoreDiscount> storeDiscounts;
    private List<Order> orders;
    private HashMap<InventoryItem, Integer> mapItemsToPrices;
    private HashMap<InventoryItem, Double> mapItemsToTotalSold;
    private List<Feedback> feedbacks;




    public Store(Zone zone, SDMStore store, Owner founder) {
        this.zone = zone;
        this.owner = founder;
        owner.getStores().add(this);
        this.storeId=store.getId();
        this.storeName=store.getName();
        this.ppk = store.getDeliveryPpk();
        this.totalSalesIncome = 0;
        this.totalDeliveryIncome = 0;
        this.storeItems = new HashSet<>();
        this.storeDiscounts = new HashSet<>();
        this.orders = new ArrayList<>();
        this.mapItemsToPrices = new HashMap<>();
        this.mapItemsToTotalSold = new HashMap<>();
        this.location = new int[2];

        this.location[0] = store.getLocation().getX();
        this.location[1] = store.getLocation().getY();
        this.feedbacks = new ArrayList<>();

        for (SDMSell sell: store.getSDMPrices().getSDMSell()){
            InventoryItem inventoryItem = zone.getInventoryItemById(sell.getItemId());
            storeItems.add(inventoryItem);
            mapItemsToPrices.put(inventoryItem, sell.getPrice());
            mapItemsToTotalSold.put(inventoryItem,0.0);
        }
        if (checkIfStoreHasDiscounts(store)){
            for (SDMDiscount sdmDiscount: store.getSDMDiscounts().getSDMDiscount()){
                storeDiscounts.add(new StoreDiscount(this,sdmDiscount));
            }
        }
    }

    //Store constructor used in "Add a new store" button
    public Store(String storeName,
                 Zone storeZone,
                 Owner owner,
                 int[] location,
                 int ppk,
                 String[] itemsToPrices) {

        System.out.println("Inside Store constructor - Add a new store button");
        this.zone = storeZone;
        this.owner = owner;
        this.storeName = storeName;
        storeDiscounts = new HashSet<>();
        storeItems = new HashSet<>();
        this.mapItemsToPrices = new HashMap<>();
        this.mapItemsToTotalSold = new HashMap<>();
        orders = new ArrayList<>();
        zone.incStoreIdRef();
        storeId = zone.getStoresIdRef();
        this.ppk = ppk;
        this.location = location;
        totalDeliveryIncome = 0;
        totalSalesIncome = 0;

        System.out.println(TAG + " About to parse items array for ids and prices");
        //TODO: How does this work? Would it be easier to use a Map?
        //add items to store (and to map items to stores with item)
        for (int i = 0; i < itemsToPrices.length; i = i + 2) {

            int itemId = Integer.parseInt(itemsToPrices[i]);
            int itemPrice = Integer.parseInt(itemsToPrices[i + 1]);
            System.out.println(TAG + " itemId=" + itemId + ", itemPrice= " + itemPrice);
            InventoryItem itemToAdd = zone.getInventoryItemById(itemId);
//            InventoryItem itemToAdd = itemsSoldInZone.stream()
//                    .filter(invItem -> invItem.getItemID() == itemId).findFirst().get();

            if (itemToAdd != null) {
                mapItemsToPrices.put(itemToAdd,itemPrice);
                mapItemsToTotalSold.put(itemToAdd,0.0);
                storeItems.add(itemToAdd);
                //StoreItem storeItem = new StoreItem(itemToAdd, itemPrice);
                //storeItems.add(storeItem);
                System.out.println("adding item to new store: " + storeName + " : " + itemId + " " + itemToAdd.getItemName() + " " + itemPrice);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    public Zone getZone() {
        return zone;
    }

    public User getOwner() {
        return owner;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public int[] getLocation() {
        return location;
    }

    public int getPpk() {
        return ppk;
    }

    public double getTotalSalesIncome() {
        return totalSalesIncome;
    }

    public double getTotalDeliveryIncome() {
        return totalDeliveryIncome;
    }

    public Set<InventoryItem> getStoreItems() {
        return storeItems;
    }

    public Set<StoreDiscount> getStoreDiscounts() {
        return storeDiscounts;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public HashMap<InventoryItem, Integer> getMapItemsToPrices() {
        return mapItemsToPrices;
    }

    public HashMap<InventoryItem, Double> getMapItemsToTotalSold() {
        return mapItemsToTotalSold;
    }

    public List<Feedback> getFeedbacks() { return feedbacks; }

    ///////////////////////////////////////////////////////////////////////////////////
    public static boolean checkIfStoreHasDiscounts(SDMStore store) {
        try{
            return store.getSDMDiscounts().getSDMDiscount().size()>0;
        } catch (NullPointerException npe){
            return false;
        }
    }

    public int getItemPrice(InventoryItem item){
        if (storeItems.contains(item))
            return mapItemsToPrices.get(item);
        return Integer.MAX_VALUE;
    }

    public double getDeliveryCost(int[] userLocation) {
        double val = getPpk()* getDistance(userLocation);
        BigDecimal bd = new BigDecimal(val).setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();

    }

    private double getDistance(int[] userLocation) {
        if (userLocation.length != 2){
            return -1;
        }
        int xDelta = userLocation[0] - location[0];
        int yDelta = userLocation[1] - location[1];
        return (double) Math.sqrt((xDelta*xDelta)+(yDelta*yDelta));
    }

    public InventoryItem getInventoryItemById(int id) {
        return storeItems.stream().filter(item->item.getItemID()==id).findFirst().orElse(null);
    }

    public HashMap<StoreDiscount, Integer> getEntitledDiscounts(Set<CartItem> cartItems) {
        HashMap<StoreDiscount, Integer> res = new HashMap<>();
        for (StoreDiscount storeDiscount: storeDiscounts){
            int timesConditionIsMet = getTimesConditionIsMet(storeDiscount, cartItems);
            if (timesConditionIsMet > 0){
                res.put(storeDiscount, timesConditionIsMet);
            }
        }
        return res;
    }


    private int getTimesConditionIsMet(StoreDiscount storeDiscount, Set<CartItem> cartItems) {
        double timesPurchasedAtRegularPrice = 0;
        int idToSearchFor = storeDiscount.getDiscountCondition().getIfYouBuyItem().getItemID();
        double conditionAmount = storeDiscount.getDiscountCondition().getQuantity();

        for (CartItem item: cartItems){
            if (item.getItemID()==idToSearchFor && item.getStore()==this){
                timesPurchasedAtRegularPrice = item.getAmount();
            }
        }

        if (timesPurchasedAtRegularPrice > 0){
            double quotient = timesPurchasedAtRegularPrice/conditionAmount;
            return (int) Math.floor(quotient);
        }
        return 0;
    }

    public StoreDiscount getDiscountByName(String discountName) {
        return storeDiscounts.stream().filter(storeDiscount -> storeDiscount.getDiscountName().equals(discountName)).findFirst().orElse(null);
    }

    public void addOrder(Order order) {
        //System.out.println(TAG + " 191");
        orders.add(order);
        //System.out.println(TAG + " 193");
        totalSalesIncome += order.getCartsSubtotal();
        //System.out.println(TAG + " 195");
        totalDeliveryIncome += order.getDeliveryFee();
        //System.out.println(TAG + " 197");
        updateStoreItemsTotalAmountSold(order.getMapStoresToCarts().get(this));
        //System.out.println(TAG + " 199");
        informOwner(order);
        //System.out.println(TAG + " 201");
    }

    private void informOwner(Order order) {
        String notification = "New Order created: " + order.getOrderId() + " for customer " + order.getCustomerName();
        owner.getNotifications().add(notification);
        double balanceBefore = owner.getBalance();
        owner.addToBalance(order.getTotalOrderCost());
        double balanceAfter = owner.getBalance();
        owner.getTransactions().add(new Transaction("deposit", order.getOrderDate(), order.getTotalOrderCost(), balanceBefore,balanceAfter));
    }

    private void updateStoreItemsTotalAmountSold(Cart cart) {
        System.out.println(TAG + " 210 - received following cart:");
        System.out.println(cart);
        for (CartItem item: cart.getCartItems()){
            System.out.println(TAG + " 212");
            InventoryItem inventoryItem = getInventoryItemById(item.getItemID());
            System.out.println(TAG + " 214");
            double oldVal = mapItemsToTotalSold.get(inventoryItem);
            System.out.println(TAG + " 216");
            double newVal = oldVal + item.getAmount();
            System.out.println(TAG + " 218");
            mapItemsToTotalSold.put(inventoryItem,newVal);
            System.out.println(TAG + " 220");
        }
        System.out.println(TAG + " 217");

        for (UsedDiscount usedDiscount: cart.getUsedDiscounts()){
            for (DiscountOffer discountOffer: usedDiscount.getOffersChosen()){
                System.out.println(TAG + " 221");
                double val = mapItemsToTotalSold.get(discountOffer.getOfferItem());
                val += (discountOffer.getQuantity() * usedDiscount.getTimesUsed());
                mapItemsToTotalSold.put(discountOffer.getOfferItem(), val);
            }
        }
        System.out.println(TAG + " 226");
    }


    public int getPriceByItemId(int itemId) {
        InventoryItem inventoryItem = storeItems.stream().filter(storeItem->storeItem.getItemID()==itemId).findFirst().orElse(null);
        if (inventoryItem != null)
            return mapItemsToPrices.get(inventoryItem);
        return Integer.MAX_VALUE;
    }

    public Order getOrderByParams(int id, int subId, eOrderType orderType) {
        for (Order order: orders){
            if (orderType==eOrderType.STATIC_ORDER){
                if (order.getOrderId().getId()==id)
                    return order;
            }
            if (orderType==eOrderType.DYNAMIC_ORDER){
                if (order.getOrderId().getId()==id && order.getOrderId().getSubId()==subId)
                    return order;
            }
        }
        return null;
    }

    public void addFeedbackAndNotifyOwner(Feedback feedback) {
        feedbacks.add(feedback);
        String notification = "Feedback left for store " + feedback.getStore() + " by customer " + feedback.getCustomer();
        owner.getNotifications().add(notification);
    }
}
