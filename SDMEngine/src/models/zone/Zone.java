package models.zone;

import models.inventoryitem.InventoryItem;
import models.order.*;
import models.store.DiscountOffer;
import models.store.Store;
import models.user.Customer;
import models.user.Owner;
import models.user.User;
import resources.schema.jaxbgenerated.SuperDuperMarketDescriptor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Zone {

    private final String TAG = "Zone";
    private int storesIdRef;

    private Owner founder;
    private String zoneName;
    private Set<Store> stores;
    private Set<InventoryItem> itemsSoldInZone;
    private List<Order> orders;
    private HashMap<InventoryItem,Double> mapItemsToAmountSold;
    private HashMap<InventoryItem,Double> mapItemsToAveragePrice;
    private HashMap<InventoryItem, Set<Store>> mapItemsToCarryingStores;
    private double averageOrderCost;
    private int zoneVersion;

    public Zone(SuperDuperMarketDescriptor sdm, Owner user){
        storesIdRef = 0;
        zoneVersion = 1;
        zoneName = sdm.getSDMZone().getName();
        founder = user;
        stores = new HashSet<>();
        itemsSoldInZone = new HashSet<>();
        orders = new ArrayList<>();
        mapItemsToAmountSold = new HashMap<>();
        mapItemsToAveragePrice = new HashMap<>();
        mapItemsToCarryingStores = new HashMap<>();
        averageOrderCost = 0;

        sdm.getSDMItems().getSDMItem().forEach(item->itemsSoldInZone.add(new InventoryItem(item)));
        itemsSoldInZone.forEach(item -> {
            mapItemsToAmountSold.put(item,0.0);
            mapItemsToCarryingStores.put(item,new HashSet<>());
        });
        sdm.getSDMStores().getSDMStore().forEach(store->{
            stores.add(new Store(this,store,founder));
            storesIdRef++;
        });

        for (Store store: stores){
            updateCarryingStores(store);
        }
        updateAveragePrices();
    }

    private void updateAveragePrices() {
        for (InventoryItem item: mapItemsToCarryingStores.keySet()){
            double sum = 0.0;
            for (Store carryingStore: mapItemsToCarryingStores.get(item)){
                sum += carryingStore.getItemPrice(item);
            }
            double ave = sum / mapItemsToCarryingStores.get(item).size();
            mapItemsToAveragePrice.put(item,ave);
        }
    }

    private void updateCarryingStores(Store store) {
        for (InventoryItem storeItem: store.getStoreItems()){
            Set<Store> carryingStores = mapItemsToCarryingStores.get(storeItem);
            carryingStores.add(store);
            mapItemsToCarryingStores.put(storeItem,carryingStores);
        }
    }


    public void incStoreIdRef(){
        storesIdRef++;
    }

    public int getStoresIdRef() {
        return storesIdRef;
    }

    public User getFounder() {
        return founder;
    }

    public String getZoneName() {
        return zoneName;
    }

    public Set<Store> getStores() {
        return stores;
    }

    public Set<InventoryItem> getItemsSoldInZone() {
        return itemsSoldInZone;
    }

    public int getZoneVersion() {
        return zoneVersion;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public HashMap<InventoryItem, Double> getMapItemsToAmountSold() {
        return mapItemsToAmountSold;
    }

    public HashMap<InventoryItem, Double> getMapItemsToAveragePrice() {
        return mapItemsToAveragePrice;
    }

    public HashMap<InventoryItem, Set<Store>> getMapItemsToCarryingStores() {
        return mapItemsToCarryingStores;
    }

    public double getAverageOrderCost() {
        return averageOrderCost;
    }

    public InventoryItem getInventoryItemById(int itemId) {
        for (InventoryItem item: itemsSoldInZone){
            if (item.getItemID()==itemId)
                return item;
        }
        return null;
    }

    public Store getStoreByName(String storeName) {
        return stores.stream().filter(store -> store.getStoreName().equals(storeName)).findFirst().orElse(null);
    }

    public Store findCheapestStoreForItem(int id) {

        InventoryItem item = getInventoryItemById(id);
        Set<Store> storesWithItem = mapItemsToCarryingStores.get(item);
        int currentMin =Integer.MAX_VALUE;
        Store cheapestStore = null;
        for (Store store: storesWithItem){
            int storePrice = store.getMapItemsToPrices().get(item);
            if (storePrice < currentMin){
                currentMin = storePrice;
                cheapestStore = store;
            }
        }
        return cheapestStore;
    }

    public void confirmOrderForUser(Customer user, String orderTypeString, int[] loc) {
        System.out.println("\n\n\n" + TAG + " going to try and create new order for user with current cart:!");
        System.out.println(user.getCurrentCart());

        eOrderType orderType = null;
        if (orderTypeString.equals("static"))
            orderType = eOrderType.STATIC_ORDER;
        else if (orderTypeString.equals("dynamic"))
            orderType = eOrderType.DYNAMIC_ORDER;

        Date date = new Date(); // This object contains the current date value
        HashMap<Store, Cart> mapStoresToCarts = new HashMap<>();
        Cart cart = user.getCurrentCart();

        for (CartItem item: cart.getCartItems()){
            if (!mapStoresToCarts.containsKey(item.getStore())){
                mapStoresToCarts.put(item.getStore(), new Cart());
            }
            mapStoresToCarts.get(item.getStore()).addItemToCart(item);
        }

        //TODO: Check if can use stream instead of lines 157-160
        for (UsedDiscount discountUsed: cart.getUsedDiscounts()){
//            Store store = mapStoresToCarts.keySet().stream().filter(keyStore -> keyStore.getStoreName().equals(discountUsed.getStoreName())).findFirst().orElse(null);
            Store store = null;
            for (Store keyStores: mapStoresToCarts.keySet()){
                if (keyStores.getStoreName().equals(discountUsed.getStore().getStoreName())){
                    store = keyStores;
                }
            }
            if (store != null){
                mapStoresToCarts.get(store).getUsedDiscounts().add(discountUsed);
            }
        }

        System.out.println("\n" + TAG + " - confirmOrderForUser(), LINE224, following stores and carts created:");
        mapStoresToCarts.forEach((s,c)->{
            System.out.println("Store=" + s.getStoreName() + " has cart= " + c + "\n");
        });

        Order order = new Order(zoneName, user.getName(), loc,
                date,
                orderType,
                mapStoresToCarts);

        System.out.println(TAG + " - LINE 181");
        if (orderType == eOrderType.STATIC_ORDER){
            System.out.println(TAG + " - LINE 183");
            Store store = mapStoresToCarts.keySet().stream().findFirst().orElse(null);
            System.out.println(TAG + " - LINE 185");
            addNewStaticOrder(store,order, user);
            System.out.println(TAG + " - LINE 187");
        } else if (orderType == eOrderType.DYNAMIC_ORDER){
            System.out.println(TAG + " - LINE 188");
            addNewDynamicOrder(order,user);
            System.out.println(TAG + " - LINE 190");
        }

        updateAverageOrderCost(order);
        System.out.println(TAG + " - just created following order: \n\t" + order + "\n---------------------------------------------");
        incrementZoneVerion();
    }

    public synchronized void incrementZoneVerion(){
        zoneVersion++;
    }

    private void updateAverageOrderCost(Order order) {
        if (orders.size()==0){
            averageOrderCost = order.getTotalOrderCost();
            return;
        }

        double totalOrdersCost = 0f;
        for (Order pastOrder: orders){
            totalOrdersCost+=pastOrder.getTotalOrderCost();
        }
        double val = totalOrdersCost / (orders.size());
        BigDecimal bd = new BigDecimal(val).setScale(2, RoundingMode.HALF_UP);
        averageOrderCost = bd.doubleValue();
    }

    private void addNewStaticOrder(Store store, Order order, Customer user) {
        System.out.println(TAG + " - LINE 242 received store=" + store.getStoreName() + ", user=" + user.getName() + ", and an order:");
        System.out.println(order);
        store.addOrder(order);
        System.out.println(TAG + " - LINE 244");
        orders.add(order);

        System.out.println(TAG + " - LINE 247");
        updateSalesMapForOrder(order.getMapStoresToCarts());
        System.out.println(TAG + " - LINE 249");
        user.addOrderToCustomer(order);
        System.out.println(TAG + " - LINE 251");
    }

    private void addNewDynamicOrder(Order order, Customer user) {
        addSplittedOrdersToStores(order);
        orders.add(order);
        updateSalesMapForOrder(order.getMapStoresToCarts());
        user.addOrderToCustomer(order);
    }

    private void addSplittedOrdersToStores(Order order) {
        HashMap<Store,Cart> mapStoresToCarts = order.getMapStoresToCarts();
        mapStoresToCarts.forEach((store,cart)->{

            HashMap<Store,Cart> mapStoreToCart = new HashMap<>();
            mapStoreToCart.put(store,cart);

            Order orderForStore = new Order(zoneName,order.getCustomerName(), order.getCustomerLocation(),
                    order.getOrderDate(),
                    eOrderType.SPLITTED_DYNAMIC_ORDER,
                    mapStoreToCart
            );
            store.addOrder(orderForStore);
        });
    }



    private void updateSalesMapForOrder(HashMap<Store, Cart> mapStoreToCart) {
        mapStoreToCart.values().forEach(cart -> {
            for (CartItem cartItem: cart.getCartItems()){
                InventoryItem inventoryItem = getInventoryItemById(cartItem.getItemID());
                double oldVal = mapItemsToAmountSold.get(inventoryItem);
                double newVal = oldVal + cartItem.getAmount();
                mapItemsToAmountSold.put(inventoryItem,newVal);
            }

            for (UsedDiscount usedDiscount: cart.getUsedDiscounts()){
                for (DiscountOffer discountOffer: usedDiscount.getOffersChosen()){
                    double val = mapItemsToAmountSold.get(discountOffer.getOfferItem());
                    val += (discountOffer.getQuantity() * usedDiscount.getTimesUsed());
                    mapItemsToAmountSold.put(discountOffer.getOfferItem(), val);
                }
            }
        });
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

    public void addStoreAndNotifyFounder(Store storeToAdd) {
        System.out.println(TAG + " - entered addStoreAndNotifyFounder, going to add store to set");
        stores.add(storeToAdd);
        System.out.println(TAG + " - addStoreAndNotifyFounder, going to update Carrying Stores");
        updateCarryingStores(storeToAdd);
        System.out.println(TAG + " - addStoreAndNotifyFounder, going to update Average Prices");
        updateAveragePrices();
        System.out.println(TAG + " - addStoreAndNotifyFounder, do we need to inform founder?");
        boolean isSameOwner = storeToAdd.getOwner().getName().equals(founder.getName());
        System.out.println(isSameOwner);
        if (!isSameOwner){
            founder.getNotifications().add(String.format("New store \"%s\" was created in zone %s",storeToAdd.getStoreName(),storeToAdd.getZone().getZoneName()));
        }
        System.out.println(TAG + " - DONE!");
    }
}
