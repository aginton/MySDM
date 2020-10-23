package models.inventoryitem;

import resources.schema.jaxbgenerated.SDMItem;

import java.util.Objects;

public class InventoryItem implements Comparable<InventoryItem> {

    private int itemID;
    private String itemName;
    private ePurchaseCategory purchaseCategory;

    public InventoryItem(SDMItem sdmItem){
        this.itemID = sdmItem.getId();
        this.itemName = sdmItem.getName();
        if (sdmItem.getPurchaseCategory().equals("Weight")){
            this.purchaseCategory = ePurchaseCategory.WEIGHT;
        } else{
            this.purchaseCategory = ePurchaseCategory.QUANTITY;
        }
    }

    public InventoryItem(InventoryItem item){
        this.itemID=item.itemID;
        this.itemName=item.itemName;
        this.purchaseCategory=item.purchaseCategory;
    }

    public int getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public ePurchaseCategory getPurchaseCategory() {
        return purchaseCategory;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryItem)) return false;
        InventoryItem that = (InventoryItem) o;
        return itemID == that.itemID &&
                Objects.equals(itemName, that.itemName) &&
                purchaseCategory == that.purchaseCategory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID, itemName, purchaseCategory);
    }

    @Override
    public int compareTo(InventoryItem o) {
        return 0;
    }
}
