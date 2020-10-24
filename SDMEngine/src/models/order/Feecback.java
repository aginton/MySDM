package models.order;

import models.zone.Zone;

import java.util.Date;

public class Feecback {

    private Zone zoneOfOrder;
    private String customerName;
    private Date dateOfOrder;
    private eRate rate;
    private String comments;

    public Feecback(Zone zoneOfOrder, String customerName, Date dateOfOrder, eRate rate, String comments) {
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

    public eRate getRate() { return rate; }

    public void setRate(eRate rate) { this.rate = rate; }

    public String getComments() { return comments; }

    public void setComments(String comments) { this.comments = comments; }
}
