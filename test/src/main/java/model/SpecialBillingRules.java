package model;

import com.fasterxml.jackson.annotation.JsonProperty;

   
public class SpecialBillingRules {

   @JsonProperty("@id")
   String specialBillingRulesId;

   @JsonProperty("brandId")
   String brandId;

   @JsonProperty("channel")
   String channel;

   @JsonProperty("country")
   String country;

   @JsonProperty("endTime")
   String endTime;

   @JsonProperty("id")
   int id;

   @JsonProperty("inventoryCompanyId")
   String inventoryCompanyId;

   @JsonProperty("itemNumber")
   String itemNumber;

   @JsonProperty("lasKey")
   String lasKey;

   @JsonProperty("licenseContract")
   LicenseContract licenseContract;

   @JsonProperty("licenseType")
   String licenseType;

   @JsonProperty("licenseValue")
   int licenseValue;

   @JsonProperty("marketGroupLabel")
   String marketGroupLabel;

   @JsonProperty("quantityCounter")
   QuantityCounter quantityCounter;

   @JsonProperty("quantityFrom")
   String quantityFrom;

   @JsonProperty("quantityTo")
   String quantityTo;

   @JsonProperty("rEkAktBez")
   String rEkAktBez;

   @JsonProperty("salesSaisonFirst")
   boolean salesSaisonFirst;

   @JsonProperty("salesSaisonNext")
   boolean salesSaisonNext;

   @JsonProperty("salesTypeRegular")
   boolean salesTypeRegular;

   @JsonProperty("salesTypeRest")
   boolean salesTypeRest;

   @JsonProperty("salesTypeUtilization")
   boolean salesTypeUtilization;

   @JsonProperty("seasonFrom")
   String seasonFrom;

   @JsonProperty("seasonTo")
   String seasonTo;

   @JsonProperty("startTime")
   String startTime;

   @JsonProperty("styleNumber")
   String styleNumber;

   @JsonProperty("tmpQuantityCounter")
   TmpQuantityCounter tmpQuantityCounter;


    public String getSpecialBillingRulesId() {
        return specialBillingRulesId;
    }

    public void setSpecialBillingRulesId(String specialBillingRulesId) {
        this.specialBillingRulesId = specialBillingRulesId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }
    public String getBrandId() {
        return brandId;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    public String getChannel() {
        return channel;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountry() {
        return country;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getEndTime() {
        return endTime;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    
    public void setInventoryCompanyId(String inventoryCompanyId) {
        this.inventoryCompanyId = inventoryCompanyId;
    }
    public String getInventoryCompanyId() {
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
    
    public void setLicenseContract(LicenseContract licenseContract) {
        this.licenseContract = licenseContract;
    }
    public LicenseContract getLicenseContract() {
        return licenseContract;
    }
    
    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }
    public String getLicenseType() {
        return licenseType;
    }
    
    public void setLicenseValue(int licenseValue) {
        this.licenseValue = licenseValue;
    }
    public int getLicenseValue() {
        return licenseValue;
    }
    
    public void setMarketGroupLabel(String marketGroupLabel) {
        this.marketGroupLabel = marketGroupLabel;
    }
    public String getMarketGroupLabel() {
        return marketGroupLabel;
    }
    
    public void setQuantityCounter(QuantityCounter quantityCounter) {
        this.quantityCounter = quantityCounter;
    }
    public QuantityCounter getQuantityCounter() {
        return quantityCounter;
    }
    
    public void setQuantityFrom(String quantityFrom) {
        this.quantityFrom = quantityFrom;
    }
    public String getQuantityFrom() {
        return quantityFrom;
    }
    
    public void setQuantityTo(String quantityTo) {
        this.quantityTo = quantityTo;
    }
    public String getQuantityTo() {
        return quantityTo;
    }
    
    public void setREkAktBez(String rEkAktBez) {
        this.rEkAktBez = rEkAktBez;
    }
    public String getREkAktBez() {
        return rEkAktBez;
    }
    
    public void setSalesSaisonFirst(boolean salesSaisonFirst) {
        this.salesSaisonFirst = salesSaisonFirst;
    }
    public boolean getSalesSaisonFirst() {
        return salesSaisonFirst;
    }
    
    public void setSalesSaisonNext(boolean salesSaisonNext) {
        this.salesSaisonNext = salesSaisonNext;
    }
    public boolean getSalesSaisonNext() {
        return salesSaisonNext;
    }
    
    public void setSalesTypeRegular(boolean salesTypeRegular) {
        this.salesTypeRegular = salesTypeRegular;
    }
    public boolean getSalesTypeRegular() {
        return salesTypeRegular;
    }
    
    public void setSalesTypeRest(boolean salesTypeRest) {
        this.salesTypeRest = salesTypeRest;
    }
    public boolean getSalesTypeRest() {
        return salesTypeRest;
    }
    
    public void setSalesTypeUtilization(boolean salesTypeUtilization) {
        this.salesTypeUtilization = salesTypeUtilization;
    }
    public boolean getSalesTypeUtilization() {
        return salesTypeUtilization;
    }
    
    public void setSeasonFrom(String seasonFrom) {
        this.seasonFrom = seasonFrom;
    }
    public String getSeasonFrom() {
        return seasonFrom;
    }
    
    public void setSeasonTo(String seasonTo) {
        this.seasonTo = seasonTo;
    }
    public String getSeasonTo() {
        return seasonTo;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getStartTime() {
        return startTime;
    }
    
    public void setStyleNumber(String styleNumber) {
        this.styleNumber = styleNumber;
    }
    public String getStyleNumber() {
        return styleNumber;
    }
    
    public void setTmpQuantityCounter(TmpQuantityCounter tmpQuantityCounter) {
        this.tmpQuantityCounter = tmpQuantityCounter;
    }
    public TmpQuantityCounter getTmpQuantityCounter() {
        return tmpQuantityCounter;
    }
    
}