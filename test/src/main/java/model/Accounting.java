package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

   
public class Accounting {

   @JsonProperty("@id")
   private String accId;

   @JsonProperty("appliedPrepayments")
   private List<String> appliedPrepayments;

   @JsonProperty("appliedTaxes")
   private List<AppliedTaxes> appliedTaxes;

   @JsonProperty("approved")
   private Date approved;

   @JsonProperty("billingPeriod")
   private String billingPeriod;

   @JsonProperty("contractAccumulations")
   private List<ContractAccumulations> contractAccumulations;

   @JsonProperty("created")
   private String created;

   @JsonProperty("creditId")
   private String creditId;

   @JsonProperty("dueDate")
   private String dueDate;

   @JsonProperty("hashCode")
   private String hashCode;

   @JsonProperty("id")
   private int id;

   @JsonProperty("licensor")
   private Licensor licensor;

   @JsonProperty("maturity")
   private Maturity maturity;

   @JsonProperty("paymentAmountInEuro")
   private PaymentAmountInEuro paymentAmountInEuro;

   @JsonProperty("prepaymentAmountInEuro")
   private PrepaymentAmountInEuro prepaymentAmountInEuro;

   @JsonProperty("prepaymentId")
   private String prepaymentId;

   @JsonProperty("prepaymentNote")
   private String prepaymentNote;

   @JsonProperty("royaltyAmountInEuro")
   private RoyaltyAmountInEuro royaltyAmountInEuro;

   @JsonProperty("sapFiBarcode")
   private String sapFiBarcode;

   @JsonProperty("season")
   private int season;

   @JsonProperty("serArcDocId")
   private String serArcDocId;

   @JsonProperty("specialPaymentAmountInEuro")
   private SpecialPaymentAmountInEuro specialPaymentAmountInEuro;

   @JsonProperty("specialPaymentComment")
   private String specialPaymentComment;

   @JsonProperty("specialPaymentCommodityGroup")
   private String specialPaymentCommodityGroup;

   @JsonProperty("status")
   private String status;

   @JsonProperty("type")
   private String type;

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public List<String> getAppliedPrepayments() {
        return appliedPrepayments;
    }

    public void setAppliedPrepayments(List<String> appliedPrepayments) {
        this.appliedPrepayments = appliedPrepayments;
    }

    public List<AppliedTaxes> getAppliedTaxes() {
        return appliedTaxes;
    }

    public void setAppliedTaxes(List<AppliedTaxes> appliedTaxes) {
        this.appliedTaxes = appliedTaxes;
    }

    public Date getApproved() {
        return approved;
    }

    public void setApproved(Date approved) {
        this.approved = approved;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public List<ContractAccumulations> getContractAccumulations() {
        return contractAccumulations;
    }

    public void setContractAccumulations(List<ContractAccumulations> contractAccumulations) {
        this.contractAccumulations = contractAccumulations;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Licensor getLicensor() {
        return licensor;
    }

    public void setLicensor(Licensor licensor) {
        this.licensor = licensor;
    }

    public Maturity getMaturity() {
        return maturity;
    }

    public void setMaturity(Maturity maturity) {
        this.maturity = maturity;
    }

    public PaymentAmountInEuro getPaymentAmountInEuro() {
        return paymentAmountInEuro;
    }

    public void setPaymentAmountInEuro(PaymentAmountInEuro paymentAmountInEuro) {
        this.paymentAmountInEuro = paymentAmountInEuro;
    }

    public PrepaymentAmountInEuro getPrepaymentAmountInEuro() {
        return prepaymentAmountInEuro;
    }

    public void setPrepaymentAmountInEuro(PrepaymentAmountInEuro prepaymentAmountInEuro) {
        this.prepaymentAmountInEuro = prepaymentAmountInEuro;
    }

    public String getPrepaymentId() {
        return prepaymentId;
    }

    public void setPrepaymentId(String prepaymentId) {
        this.prepaymentId = prepaymentId;
    }

    public String getPrepaymentNote() {
        return prepaymentNote;
    }

    public void setPrepaymentNote(String prepaymentNote) {
        this.prepaymentNote = prepaymentNote;
    }

    public RoyaltyAmountInEuro getRoyaltyAmountInEuro() {
        return royaltyAmountInEuro;
    }

    public void setRoyaltyAmountInEuro(RoyaltyAmountInEuro royaltyAmountInEuro) {
        this.royaltyAmountInEuro = royaltyAmountInEuro;
    }

    public String getSapFiBarcode() {
        return sapFiBarcode;
    }

    public void setSapFiBarcode(String sapFiBarcode) {
        this.sapFiBarcode = sapFiBarcode;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public String getSerArcDocId() {
        return serArcDocId;
    }

    public void setSerArcDocId(String serArcDocId) {
        this.serArcDocId = serArcDocId;
    }

    public SpecialPaymentAmountInEuro getSpecialPaymentAmountInEuro() {
        return specialPaymentAmountInEuro;
    }

    public void setSpecialPaymentAmountInEuro(SpecialPaymentAmountInEuro specialPaymentAmountInEuro) {
        this.specialPaymentAmountInEuro = specialPaymentAmountInEuro;
    }

    public String getSpecialPaymentComment() {
        return specialPaymentComment;
    }

    public void setSpecialPaymentComment(String specialPaymentComment) {
        this.specialPaymentComment = specialPaymentComment;
    }

    public String getSpecialPaymentCommodityGroup() {
        return specialPaymentCommodityGroup;
    }

    public void setSpecialPaymentCommodityGroup(String specialPaymentCommodityGroup) {
        this.specialPaymentCommodityGroup = specialPaymentCommodityGroup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}