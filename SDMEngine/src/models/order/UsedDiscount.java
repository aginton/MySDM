package models.order;

import models.store.DiscountCondition;
import models.store.DiscountOffer;
import models.store.Store;
import models.store.StoreDiscount;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UsedDiscount {
    private final String TAG = "UsedDiscount";

    private static int discountRef = 0;


    private Store store;
    private String discountName;
    private String discountOperator;
    private DiscountCondition condition;
    private Set<DiscountOffer> offersChosen;
    private int timesUsed;
    private int discountUsedRef;

    public UsedDiscount(StoreDiscount storeDiscount, Set<DiscountOffer> offersToAdd){
        System.out.println(TAG + " LINE 21");
        store = storeDiscount.getStore();
        discountName = storeDiscount.getDiscountName();
        discountOperator = storeDiscount.getDiscountOperator();
        condition = storeDiscount.getDiscountCondition();
        offersChosen = new HashSet<>();
        for (DiscountOffer offer: offersToAdd){
            offersChosen.add(offer);
        }
        timesUsed = 1;
        System.out.println(TAG + " LINE 29");

        this.discountUsedRef = ++discountRef;
    }

    public Store getStore() {
        return store;
    }

    public String getDiscountName() {
        return discountName;
    }

    public String getDiscountOperator() {
        return discountOperator;
    }

    public DiscountCondition getCondition() {
        return condition;
    }

    public Set<DiscountOffer> getOffersChosen() {
        return offersChosen;
    }

    public int getDiscountUsedRef() {
        return discountUsedRef;
    }

    public int getTimesUsed() {
        return timesUsed;
    }



    public void incrementTimesUsed(){
        timesUsed++;
    }
    public void decrementTimesUsed(){
        timesUsed--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsedDiscount)) return false;
        UsedDiscount that = (UsedDiscount) o;
        return Objects.equals(store, that.store) &&
                Objects.equals(discountName, that.discountName) &&
                Objects.equals(condition, that.condition) &&
                Objects.equals(offersChosen, that.offersChosen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(store, discountName, condition, offersChosen);
    }

    @Override
    public String toString() {
        return "UsedDiscount{" +
                "store=" + store +
                ", discountName='" + discountName + '\'' +
                ", discountOperator='" + discountOperator + '\'' +
                ", timesUsed=" + timesUsed +
                '}';
    }

    //    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder("UsedDiscount{store=" + store.getStoreName() + ", discountName= " + discountName + ", " + condition + ", offersChosen={");
//        for (DiscountOffer offer: offersChosen){
//            sb.append("{" + offer.getQuantity() + " of " + offer.getOfferItem().getItemName() + " for additional " + offer.getForAdditional() + "},");
//        }
//        sb.append(", timesUsed = " + timesUsed + "}");
//        return sb.toString();
//    }
}
