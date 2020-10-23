package models.store;

import models.inventoryitem.InventoryItem;
import resources.schema.jaxbgenerated.SDMDiscount;
import resources.schema.jaxbgenerated.SDMOffer;

import java.util.HashSet;
import java.util.Set;

public class StoreDiscount {

    protected String discountName;
    protected String discountOperator;
    protected DiscountCondition discountCondition;
    protected Set<DiscountOffer> discountOffers;
    protected Store store;

    public StoreDiscount(Store store, SDMDiscount sdmDiscount){
        this.store = store;
        this.discountName = sdmDiscount.getName();
        this.discountOperator = sdmDiscount.getThenYouGet().getOperator();
        InventoryItem ifYouBuyItem = store.getZone().getInventoryItemById(sdmDiscount.getIfYouBuy().getItemId());
        this.discountCondition = new DiscountCondition(ifYouBuyItem, sdmDiscount.getIfYouBuy().getQuantity());
        this.discountOffers = new HashSet<>();
        for (SDMOffer offer: sdmDiscount.getThenYouGet().getSDMOffer()){
            discountOffers.add(new DiscountOffer(store,offer));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public String getDiscountName() {
        return discountName;
    }

    public String getDiscountOperator() {
        return discountOperator;
    }

    public DiscountCondition getDiscountCondition() {
        return discountCondition;
    }

    public Set<DiscountOffer> getDiscountOffers() {
        return discountOffers;
    }

    public Store getStore() {
        return store;
    }

    public DiscountOffer getOfferDetailsByItemName(String offerChosen) {
        return discountOffers.stream().filter(offer->offer.getOfferItem().getItemName().equals(offerChosen)).findFirst().orElse(null);
    }
}
