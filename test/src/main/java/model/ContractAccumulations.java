package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class ContractAccumulations {

    @JsonProperty("@id")
    private String contractAccId;

    @JsonProperty("accounting")
    private Accounting accounting;

    @JsonProperty("additionalPaymentAmountInEuro")
    private AdditionalPaymentAmountInEuro additionalPaymentAmountInEuro;

    @JsonProperty("appliedMinimumLicense")
    private String appliedMinimumLicense;

    @JsonProperty("id")
    private int id;

    @JsonProperty("licenseContract")
    private LicenseContract licenseContract;

    @JsonProperty("originalAmountInEuro")
    private String originalAmountInEuro;

    @JsonProperty("prepaymentAmountInEuro")
    private PrepaymentAmountInEuro prepaymentAmountInEuro;

    @JsonProperty("royaltyAmountInEuro")
    private RoyaltyAmountInEuro royaltyAmountInEuro;

    @JsonProperty("ruleAccumulations")
    private List<RuleAccumulations> ruleAccumulations;

    public String getContractAccId() {
        return contractAccId;
    }

    public void setContractAccId(String contractAccId) {
        this.contractAccId = contractAccId;
    }

    public void setAccounting(Accounting accounting) {
        this.accounting = accounting;
    }

    public Accounting getAccounting() {
        return accounting;
    }

    public void setAdditionalPaymentAmountInEuro(AdditionalPaymentAmountInEuro additionalPaymentAmountInEuro) {
        this.additionalPaymentAmountInEuro = additionalPaymentAmountInEuro;
    }

    public AdditionalPaymentAmountInEuro getAdditionalPaymentAmountInEuro() {
        return additionalPaymentAmountInEuro;
    }

    public void setAppliedMinimumLicense(String appliedMinimumLicense) {
        this.appliedMinimumLicense = appliedMinimumLicense;
    }

    public String getAppliedMinimumLicense() {
        return appliedMinimumLicense;
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

    public void setOriginalAmountInEuro(String originalAmountInEuro) {
        this.originalAmountInEuro = originalAmountInEuro;
    }

    public String getOriginalAmountInEuro() {
        return originalAmountInEuro;
    }

    public void setPrepaymentAmountInEuro(PrepaymentAmountInEuro prepaymentAmountInEuro) {
        this.prepaymentAmountInEuro = prepaymentAmountInEuro;
    }

    public PrepaymentAmountInEuro getPrepaymentAmountInEuro() {
        return prepaymentAmountInEuro;
    }

    public void setRoyaltyAmountInEuro(RoyaltyAmountInEuro royaltyAmountInEuro) {
        this.royaltyAmountInEuro = royaltyAmountInEuro;
    }

    public RoyaltyAmountInEuro getRoyaltyAmountInEuro() {
        return royaltyAmountInEuro;
    }

    public void setRuleAccumulations(List<RuleAccumulations> ruleAccumulations) {
        this.ruleAccumulations = ruleAccumulations;
    }

    public List<RuleAccumulations> getRuleAccumulations() {
        return ruleAccumulations;
    }

}