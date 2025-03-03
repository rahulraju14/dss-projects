package model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class QuantityCounter {

   @JsonProperty("amount")
   int amount;

   @JsonProperty("currencyCode")
   String currencyCode;


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