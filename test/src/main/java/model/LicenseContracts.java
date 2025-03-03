package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class LicenseContracts {

    @JsonProperty("@id")
    private String licenseContractId;

    @JsonProperty("billingRules")
    private List<BillingRules> billingRules;

    @JsonProperty("id")
    private int id;

    @JsonProperty("licensor")
    private Licensor licensor;

    @JsonProperty("licensorId")
    private int licensorId;

    @JsonProperty("minimumLicenses")
    private List<String> minimumLicenses;

    @JsonProperty("specialBillingRules")
    private List<SpecialBillingRules> specialBillingRules;

    @JsonProperty("title")
    private String title;

    public String getLicenseContractId() {
        return licenseContractId;
    }

    public void setLicenseContractId(String licenseContractId) {
        this.licenseContractId = licenseContractId;
    }

    public void setBillingRules(List<BillingRules> billingRules) {
        this.billingRules = billingRules;
    }

    public List<BillingRules> getBillingRules() {
        return billingRules;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLicensor(Licensor licensor) {
        this.licensor = licensor;
    }

    public Licensor getLicensor() {
        return licensor;
    }

    public void setLicensorId(int licensorId) {
        this.licensorId = licensorId;
    }

    public int getLicensorId() {
        return licensorId;
    }

    public void setMinimumLicenses(List<String> minimumLicenses) {
        this.minimumLicenses = minimumLicenses;
    }

    public List<String> getMinimumLicenses() {
        return minimumLicenses;
    }

    public void setSpecialBillingRules(List<SpecialBillingRules> specialBillingRules) {
        this.specialBillingRules = specialBillingRules;
    }

    public List<SpecialBillingRules> getSpecialBillingRules() {
        return specialBillingRules;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}