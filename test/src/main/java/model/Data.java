package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;


public class Data {

    @JsonProperty("@id")
    private String dataId;

    @JsonProperty("bestWgFtKz")
    private int bestWgFtKz;

    @JsonProperty("bestWgKatartKz")
    private String bestWgKatartKz;

    @JsonProperty("bestWgOtNr")
    private int bestWgOtNr;

    @JsonProperty("bestWgUnterWgNr")
    private String bestWgUnterWgNr;

    @JsonProperty("brandId")
    private int brandId;

    @JsonProperty("brandLabel")
    private String brandLabel;

    @JsonProperty("descriptionDE")
    private String descriptionDE;

    @JsonProperty("descriptionEN")
    private String descriptionEN;

    @JsonProperty("inventoryCompanyId")
    private int inventoryCompanyId;

    @JsonProperty("isNew")
    private boolean isNew;

    @JsonProperty("itemKey")
    private ItemKey itemKey;

    @JsonProperty("itemNumber")
    private String itemNumber;

    @JsonProperty("lasKey")
    private String lasKey;

    @JsonProperty("licenseContracts")
    private List<LicenseContracts> licenseContracts;

    @JsonProperty("licenseCosts")
    private int licenseCosts;

    @JsonProperty("licenseFree")
    private boolean licenseFree;

    @JsonProperty("marketGroupKz")
    private int marketGroupKz;

    @JsonProperty("marketGroupLabel")
    private String marketGroupLabel;

    @JsonProperty("primalSeason")
    private int primalSeason;

    @JsonProperty("rowId")
    private String rowId;

    @JsonProperty("season")
    private int season;

    @JsonProperty("styleNumber")
    private String styleNumber;

    @JsonProperty("versionDate")
    private Date versionDate;


    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public void setBestWgFtKz(int bestWgFtKz) {
        this.bestWgFtKz = bestWgFtKz;
    }

    public int getBestWgFtKz() {
        return bestWgFtKz;
    }

    public void setBestWgKatartKz(String bestWgKatartKz) {
        this.bestWgKatartKz = bestWgKatartKz;
    }

    public String getBestWgKatartKz() {
        return bestWgKatartKz;
    }

    public void setBestWgOtNr(int bestWgOtNr) {
        this.bestWgOtNr = bestWgOtNr;
    }

    public int getBestWgOtNr() {
        return bestWgOtNr;
    }

    public void setBestWgUnterWgNr(String bestWgUnterWgNr) {
        this.bestWgUnterWgNr = bestWgUnterWgNr;
    }

    public String getBestWgUnterWgNr() {
        return bestWgUnterWgNr;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandLabel(String brandLabel) {
        this.brandLabel = brandLabel;
    }

    public String getBrandLabel() {
        return brandLabel;
    }

    public void setDescriptionDE(String descriptionDE) {
        this.descriptionDE = descriptionDE;
    }

    public String getDescriptionDE() {
        return descriptionDE;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setInventoryCompanyId(int inventoryCompanyId) {
        this.inventoryCompanyId = inventoryCompanyId;
    }

    public int getInventoryCompanyId() {
        return inventoryCompanyId;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setItemKey(ItemKey itemKey) {
        this.itemKey = itemKey;
    }

    public ItemKey getItemKey() {
        return itemKey;
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

    public void setLicenseContracts(List<LicenseContracts> licenseContracts) {
        this.licenseContracts = licenseContracts;
    }

    public List<LicenseContracts> getLicenseContracts() {
        return licenseContracts;
    }

    public void setLicenseCosts(int licenseCosts) {
        this.licenseCosts = licenseCosts;
    }

    public int getLicenseCosts() {
        return licenseCosts;
    }

    public void setLicenseFree(boolean licenseFree) {
        this.licenseFree = licenseFree;
    }

    public boolean getLicenseFree() {
        return licenseFree;
    }

    public void setMarketGroupKz(int marketGroupKz) {
        this.marketGroupKz = marketGroupKz;
    }

    public int getMarketGroupKz() {
        return marketGroupKz;
    }

    public void setMarketGroupLabel(String marketGroupLabel) {
        this.marketGroupLabel = marketGroupLabel;
    }

    public String getMarketGroupLabel() {
        return marketGroupLabel;
    }

    public void setPrimalSeason(int primalSeason) {
        this.primalSeason = primalSeason;
    }

    public int getPrimalSeason() {
        return primalSeason;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getRowId() {
        return rowId;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getSeason() {
        return season;
    }

    public void setStyleNumber(String styleNumber) {
        this.styleNumber = styleNumber;
    }

    public String getStyleNumber() {
        return styleNumber;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    public Date getVersionDate() {
        return versionDate;
    }

}