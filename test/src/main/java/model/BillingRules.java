package model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class BillingRules {

    @JsonProperty("@id")
    private String billingId;

    @JsonProperty("id")
    private int id;

    @JsonProperty("licenseContract")
    private LicenseContract licenseContract;

    @JsonProperty("licenseType")
    private String licenseType;

    @JsonProperty("licenseValue")
    private double licenseValue;

    @JsonProperty("salesSaisonFirst")
    private boolean salesSaisonFirst;

    @JsonProperty("salesSaisonNext")
    private boolean salesSaisonNext;

    @JsonProperty("salesTypeRegular")
    private boolean salesTypeRegular;

    @JsonProperty("salesTypeRest")
    private boolean salesTypeRest;

    @JsonProperty("salesTypeUtilization")
    private boolean salesTypeUtilization;

    public String getBillingId() {
        return billingId;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public void setLicenseValue(double licenseValue) {
        this.licenseValue = licenseValue;
    }

    public double getLicenseValue() {
        return licenseValue;
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

}