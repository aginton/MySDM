package models.user;

import models.order.Cart;
import models.order.Order;
import models.store.DiscountOffer;
import models.store.Store;
import models.store.StoreDiscount;
import models.zone.Zone;

import java.util.ArrayList;
import java.util.List;

public class User {

    private final String TAG = "User";
    protected String name;
    protected String role;
    protected double balance;

    protected List<Transaction> transactions;

    public User(String name, String role){
        this.name = name;
        this.role = role;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        System.out.println();
    }


    //////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }


    public void addToBalance(double totalOrderCost) {
        this.balance += totalOrderCost;
    }
}
