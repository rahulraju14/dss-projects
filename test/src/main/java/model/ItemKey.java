package model;

import com.fasterxml.jackson.annotation.JsonProperty;

   
public class ItemKey {

   @JsonProperty("inventoryCompanyId")
   private int inventoryCompanyId;

   @JsonProperty("itemNumber")
   private String itemNumber;

   @JsonProperty("lasKey")
   private String lasKey;

   @JsonProperty("season")
   private int season;
    public void setInventoryCompanyId(int inventoryCompanyId) {
        this.inventoryCompanyId = inventoryCompanyId;
    }
    public int getInventoryCompanyId() {
        return inventoryCompanyId;
    }
    
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }
    public String getItemNumber() {
        return itemNumber;
    }
    
    public void setLasKey(String lasKey) {
        this.lasKey = lasKey;
    }
    public String getLasKey() {
        return lasKey;
    }
    
    public void setSeason(int season) {
        this.season = season;
    }
    public int getSeason() {
        return season;
    }
    
}