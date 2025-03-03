package model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class AdditionalPaymentAmountInEuro {

   @JsonProperty("amount")
   private int amount;

   @JsonProperty("currencyCode")
   private String currencyCode;

    public void setAmount(int amount) {
        this.amount = amount;
    }
    public int getAmount() {
        return amount;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    
}