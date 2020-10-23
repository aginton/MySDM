package models.store;

import models.inventoryitem.InventoryItem;
import resources.schema.jaxbgenerated.SDMOffer;

import java.util.Objects;

public class DiscountOffer {
    protected InventoryItem offerItem;
    protected double quantity;
    protected int forAdditional;

    public DiscountOffer(InventoryItem item, double quantity, int forAdditional){
        this.offerItem = item;
        this.quantity = quantity;
        this.forAdditional = forAdditional;
    }

    public DiscountOffer(Store store, SDMOffer offer) {
        this.offerItem = store.getZone().getInventoryItemById(offer.getItemId());
        this.quantity = offer.getQuantity();
        this.forAdditional = offer.getForAdditional();
    }

    public InventoryItem getOfferItem() {
        return offerItem;
    }

    public double getQuantity() {
        return quantity;
    }

    public int getForAdditional() {
        return forAdditional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscountOffer)) return false;
        DiscountOffer that = (DiscountOffer) o;
        return Double.compare(that.quantity, quantity) == 0 &&
                forAdditional == that.forAdditional &&
                Objects.equals(offerItem, that.offerItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offerItem, quantity, forAdditional);
    }


}
