package models.user;

import models.store.Feedback;
import models.store.Store;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Owner extends User {
    protected List<String> notifications;
    protected Set<Store> stores;

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

    public List<Feedback> getFeedbacksForZone(String zoneName) {
        List<Feedback> res = new ArrayList<>();
        for (Store store: stores){
            if (store.getZone().getZoneName().equals(zoneName)){
                for (Feedback feedback: store.getFeedbacks()){
                    res.add(feedback);
                }
            }
        }
        return res;
    }
}
