package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class Licensor {

    @JsonProperty("@id")
    String licensorId;

    @JsonProperty("addressLine1")
    String addressLine1;

    @JsonProperty("addressLine2")
    String addressLine2;

    @JsonProperty("addressLine3")
    String addressLine3;

    @JsonProperty("category")
    Category category;

    @JsonProperty("city")
    String city;

    @JsonProperty("company")
    String company;

    @JsonProperty("country")
    Country country;

    @JsonProperty("deductionCategory")
    String deductionCategory;

    @JsonProperty("deductionFS")
    String deductionFS;

    @JsonProperty("deductionHW")
    String deductionHW;

    @JsonProperty("deductionInterval")
    String deductionInterval;

    @JsonProperty("deductionMaturity")
    int deductionMaturity;

    @JsonProperty("email")
    String email;

    @JsonProperty("firstName")
    String firstName;

    @JsonProperty("foreignTradeReferenceNumber")
    String foreignTradeReferenceNumber;

    @JsonProperty("greeting")
    String greeting;

    @JsonProperty("id")
    int id;

    @JsonProperty("inactive")
    boolean inactive;

    @JsonProperty("language")
    String language;

    @JsonProperty("lastName")
    String lastName;

    @JsonProperty("licenseContracts")
    List<LicenseContracts> licenseContracts;

    @JsonProperty("lkz")
    int lkz;

    @JsonProperty("lkzCentralRegulator")
    boolean lkzCentralRegulator;

    @JsonProperty("maturities")
    List<Maturities> maturities;

    @JsonProperty("minimumLicenses")
    List<String> minimumLicenses;

    @JsonProperty("payment")
    Payment payment;

    @JsonProperty("paymentNote1")
    String paymentNote1;

    @JsonProperty("paymentNote2")
    String paymentNote2;

    @JsonProperty("postingCode")
    String postingCode;

    @JsonProperty("prepayments")
    List<String> prepayments;

    @JsonProperty("remainingTax")
    int remainingTax;

    @JsonProperty("salesTax")
    SalesTax salesTax;

    @JsonProperty("salutation")
    String salutation;

    @JsonProperty("sapPartnerId")
    String sapPartnerId;

    @JsonProperty("solidarityTax")
    SolidarityTax solidarityTax;

    @JsonProperty("subject")
    String subject;

    @JsonProperty("taxExempt")
    boolean taxExempt;

    @JsonProperty("taxNumber")
    String taxNumber;

    @JsonProperty("taxReferenceNumber")
    String taxReferenceNumber;

    @JsonProperty("withholdingTax")
    WithholdingTax withholdingTax;

    @JsonProperty("zipCode")
    String zipCode;

    @JsonProperty("zlkz")
    int zlkz;


    public String getLicensorId() {
        return licensorId;
    }

    public void setLicensorId(String licensorId) {
        this.licensorId = licensorId;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    public void setDeductionCategory(String deductionCategory) {
        this.deductionCategory = deductionCategory;
    }

    public String getDeductionCategory() {
        return deductionCategory;
    }

    public void setDeductionFS(String deductionFS) {
        this.deductionFS = deductionFS;
    }

    public String getDeductionFS() {
        return deductionFS;
    }

    public void setDeductionHW(String deductionHW) {
        this.deductionHW = deductionHW;
    }

    public String getDeductionHW() {
        return deductionHW;
    }

    public void setDeductionInterval(String deductionInterval) {
        this.deductionInterval = deductionInterval;
    }

    public String getDeductionInterval() {
        return deductionInterval;
    }

    public void setDeductionMaturity(int deductionMaturity) {
        this.deductionMaturity = deductionMaturity;
    }

    public int getDeductionMaturity() {
        return deductionMaturity;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setForeignTradeReferenceNumber(String foreignTradeReferenceNumber) {
        this.foreignTradeReferenceNumber = foreignTradeReferenceNumber;
    }

    public String getForeignTradeReferenceNumber() {
        return foreignTradeReferenceNumber;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public boolean getInactive() {
        return inactive;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLicenseContracts(List<LicenseContracts> licenseContracts) {
        this.licenseContracts = licenseContracts;
    }

    public List<LicenseContracts> getLicenseContracts() {
        return licenseContracts;
    }

    public void setLkz(int lkz) {
        this.lkz = lkz;
    }

    public int getLkz() {
        return lkz;
    }

    public void setLkzCentralRegulator(boolean lkzCentralRegulator) {
        this.lkzCentralRegulator = lkzCentralRegulator;
    }

    public boolean getLkzCentralRegulator() {
        return lkzCentralRegulator;
    }

    public void setMaturities(List<Maturities> maturities) {
        this.maturities = maturities;
    }

    public List<Maturities> getMaturities() {
        return maturities;
    }

    public void setMinimumLicenses(List<String> minimumLicenses) {
        this.minimumLicenses = minimumLicenses;
    }

    public List<String> getMinimumLicenses() {
        return minimumLicenses;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPaymentNote1(String paymentNote1) {
        this.paymentNote1 = paymentNote1;
    }

    public String getPaymentNote1() {
        return paymentNote1;
    }

    public void setPaymentNote2(String paymentNote2) {
        this.paymentNote2 = paymentNote2;
    }

    public String getPaymentNote2() {
        return paymentNote2;
    }

    public void setPostingCode(String postingCode) {
        this.postingCode = postingCode;
    }

    public String getPostingCode() {
        return postingCode;
    }

    public void setPrepayments(List<String> prepayments) {
        this.prepayments = prepayments;
    }

    public List<String> getPrepayments() {
        return prepayments;
    }

    public void setRemainingTax(int remainingTax) {
        this.remainingTax = remainingTax;
    }

    public int getRemainingTax() {
        return remainingTax;
    }

    public void setSalesTax(SalesTax salesTax) {
        this.salesTax = salesTax;
    }

    public SalesTax getSalesTax() {
        return salesTax;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSapPartnerId(String sapPartnerId) {
        this.sapPartnerId = sapPartnerId;
    }

    public String getSapPartnerId() {
        return sapPartnerId;
    }

    public void setSolidarityTax(SolidarityTax solidarityTax) {
        this.solidarityTax = solidarityTax;
    }

    public SolidarityTax getSolidarityTax() {
        return solidarityTax;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setTaxExempt(boolean taxExempt) {
        this.taxExempt = taxExempt;
    }

    public boolean getTaxExempt() {
        return taxExempt;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxReferenceNumber(String taxReferenceNumber) {
        this.taxReferenceNumber = taxReferenceNumber;
    }

    public String getTaxReferenceNumber() {
        return taxReferenceNumber;
    }

    public void setWithholdingTax(WithholdingTax withholdingTax) {
        this.withholdingTax = withholdingTax;
    }

    public WithholdingTax getWithholdingTax() {
        return withholdingTax;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZlkz(int zlkz) {
        this.zlkz = zlkz;
    }

    public int getZlkz() {
        return zlkz;
    }

}