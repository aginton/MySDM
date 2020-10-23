package models.user;

import java.util.Date;

public class Transaction {
    private String type;
    private Date date;
    private double amount;
    private double balanceBefore;
    private double balanceAfter;

    public Transaction(String type, Date date, double amount, double balanceBefore, double balanceAfter){
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceBefore() {
        return balanceBefore;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }
}
