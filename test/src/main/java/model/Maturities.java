package model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Maturities {

    @JsonProperty("@id")
    String maturitiesId;

    @JsonProperty("accounting")
    Accounting accounting;

    @JsonProperty("billingPeriod")
    String billingPeriod;

    @JsonProperty("deductionCategory")
    String deductionCategory;

    @JsonProperty("deductionInterval")
    String deductionInterval;

    @JsonProperty("dueDate")
    String dueDate;

    @JsonProperty("id")
    int id;

    @JsonProperty("ignore")
    boolean ignore;

    @JsonProperty("licensor")
    Licensor licensor;

    @JsonProperty("maturityType")
    String maturityType;

    @JsonProperty("prepayment")
    String prepayment;

    @JsonProperty("salesMonth")
    int salesMonth;

    @JsonProperty("salesYear")
    int salesYear;

    public String getMaturitiesId() {
        return maturitiesId;
    }

    public void setMaturitiesId(String maturitiesId) {
        this.maturitiesId = maturitiesId;
    }

    public void setAccounting(Accounting accounting) {
        this.accounting = accounting;
    }

    public Accounting getAccounting() {
        return accounting;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setDeductionCategory(String deductionCategory) {
        this.deductionCategory = deductionCategory;
    }

    public String getDeductionCategory() {
        return deductionCategory;
    }

    public void setDeductionInterval(String deductionInterval) {
        this.deductionInterval = deductionInterval;
    }

    public String getDeductionInterval() {
        return deductionInterval;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean getIgnore() {
        return ignore;
    }

    public void setLicensor(Licensor licensor) {
        this.licensor = licensor;
    }

    public Licensor getLicensor() {
        return licensor;
    }

    public void setMaturityType(String maturityType) {
        this.maturityType = maturityType;
    }

    public String getMaturityType() {
        return maturityType;
    }

    public void setPrepayment(String prepayment) {
        this.prepayment = prepayment;
    }

    public String getPrepayment() {
        return prepayment;
    }

    public void setSalesMonth(int salesMonth) {
        this.salesMonth = salesMonth;
    }

    public int getSalesMonth() {
        return salesMonth;
    }

    public void setSalesYear(int salesYear) {
        this.salesYear = salesYear;
    }

    public int getSalesYear() {
        return salesYear;
    }

}