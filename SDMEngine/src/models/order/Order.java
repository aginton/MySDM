package models.order;

import models.store.Store;

import java.util.*;

public class Order {

    private final String TAG = "Order";
    private static int numOfOrders = 0;
    public static void setNumOfOrders(int numOfOrders) {
        Order.numOfOrders = numOfOrders;
    }

    private OrderId orderId;
    private eOrderType orderType;
    private HashMap<Store,Cart> mapStoresToCarts;
    private String customerName;
    private String zoneName;
    private Date orderDate;
    private int[] userLocation;
    private double deliveryFee;
    private double cartsSubtotal;
    private int totalNumberItems;
    private int totalNumberItemsByType;

    public Order(String zoneName, String customerName, int[] userLocation, Date orderDate, eOrderType orderType, HashMap<Store,Cart> mapStoresToCarts){
        System.out.println(TAG + " LINE 31");
        this.customerName = customerName;
        this.zoneName = zoneName;
        this.userLocation = userLocation;
        this.orderDate = orderDate;
        this.orderType = orderType;
        //this.mapStoreToCart = new HashMap<>();
        this.mapStoresToCarts = mapStoresToCarts;
        this.deliveryFee = 0.0;
        this.cartsSubtotal = 0.0;
        this.totalNumberItems = 0;
        this.totalNumberItemsByType = 0;

        mapStoresToCarts.forEach((store,cart)->{
            deliveryFee += store.getDeliveryCost(userLocation);
            cartsSubtotal += cart.getSubtotal();
            totalNumberItems += cart.getTotalNumberItems();
            totalNumberItemsByType = cart.getTotalNumberItemsByType();
        });
        if (orderType == eOrderType.STATIC_ORDER || orderType == eOrderType.DYNAMIC_ORDER) {
            numOfOrders++;
            this.orderId = new OrderId(numOfOrders, -1);
        }

        Set<Store> storesBoughtFrom = mapStoresToCarts.keySet();
        if (orderType == eOrderType.SPLITTED_DYNAMIC_ORDER) {
            //id in format: <dynamic-order-id.store-id>
            Iterator<Store> iterator = storesBoughtFrom.iterator();
            int storeId = iterator.next().getStoreId();
            this.orderId = new OrderId(numOfOrders, storeId);
        }
    }

    public static int getNumOfOrders() {
        return numOfOrders;
    }

    public List<String> getStoreNames(){
        List<String> res = new ArrayList<>();
        for (Store store: mapStoresToCarts.keySet()){
            res.add(store.getStoreName());
        }
        return res;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public eOrderType getOrderType() {
        return orderType;
    }

    public HashMap<Store, Cart> getMapStoresToCarts() {
        return mapStoresToCarts;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public int[] getCustomerLocation() {
        return userLocation;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public double getCartsSubtotal() {
        return cartsSubtotal;
    }

    public int getTotalNumberItems() {
        return totalNumberItems;
    }

    public int getTotalNumberItemsByType() {
        return totalNumberItemsByType;
    }

    public double getTotalOrderCost() {
        return cartsSubtotal + deliveryFee;
    }
}
