package models.user;

import models.store.Store;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Owner extends User {
    protected List<String> notifications;
    protected Set<Store> stores;
    //private List<Feedback> feedbacks;

    public Owner(String name,String role){
        super(name, role);
        this.notifications = new ArrayList<>();
        this.stores = new HashSet<>();
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public Set<Store> getStores() {
        return stores;
    }
}
