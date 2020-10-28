package models.order;

import models.zone.Zone;

import java.util.Date;

public class Feedback {

    private Zone zoneOfOrder;
    private String customerName;
    private Date dateOfOrder;
    private int rate;
    private String comments;

    public Feedback(Zone zoneOfOrder, String customerName, Date dateOfOrder, int rate, String comments) {
        this.zoneOfOrder = zoneOfOrder;
        this.customerName = customerName;
        this.dateOfOrder = dateOfOrder;
        this.rate = rate;
        this.comments = comments;
    }

    public Zone getZoneOfOrder() { return zoneOfOrder; }

    public void setZoneOfOrder(Zone zoneOfOrder) { this.zoneOfOrder = zoneOfOrder; }

    public String getCustomerName() { return customerName; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Date getDateOfOrder() { return dateOfOrder; }

    public void setDateOfOrder(Date dateOfOrder) { this.dateOfOrder = dateOfOrder; }

    public int getRate() { return rate; }

    public void setRate(int rate) { this.rate = rate; }

    public String getComments() { return comments; }

    public void setComments(String comments) { this.comments = comments; }
}
