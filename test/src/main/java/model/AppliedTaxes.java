package model;

import com.fasterxml.jackson.annotation.JsonProperty;

   
public class AppliedTaxes {

   @JsonProperty("@id")
   private String taxId;

   @JsonProperty("accounting")
   private Accounting accounting;

   @JsonProperty("amountOfTaxInEuro")
   private AmountOfTaxInEuro amountOfTaxInEuro;

   @JsonProperty("id")
   private int id;

   @JsonProperty("taxValue")
   private int taxValue;

   @JsonProperty("typeOfTax")
   private String typeOfTax;

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public void setAccounting(Accounting accounting) {
        this.accounting = accounting;
    }
    public Accounting getAccounting() {
        return accounting;
    }
    
    public void setAmountOfTaxInEuro(AmountOfTaxInEuro amountOfTaxInEuro) {
        this.amountOfTaxInEuro = amountOfTaxInEuro;
    }
    public AmountOfTaxInEuro getAmountOfTaxInEuro() {
        return amountOfTaxInEuro;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    
    public void setTaxValue(int taxValue) {
        this.taxValue = taxValue;
    }
    public int getTaxValue() {
        return taxValue;
    }
    
    public void setTypeOfTax(String typeOfTax) {
        this.typeOfTax = typeOfTax;
    }
    public String getTypeOfTax() {
        return typeOfTax;
    }
    
}