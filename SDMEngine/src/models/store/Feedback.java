package models.store;

import java.util.Date;

public class Feedback {
    private String customer;
    private String store;
    private int rating;
    private String comment;
    private Date date;

    public Feedback(String customer, String store, int rating, String comment){
        this.customer = customer;
        this.store = store;
        this.rating=rating;
        this.comment=comment;
        this.date = new Date();
    }

    public String getCustomer() {
        return customer;
    }

    public String getStore() {
        return store;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }
}
