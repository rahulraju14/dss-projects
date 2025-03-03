package model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentAmountInEuro {

   @JsonProperty("amount")
   double amount;

   @JsonProperty("currencyCode")
   String currencyCode;


    public void setAmount(double amount) {
        this.amount = amount;
    }
    public double getAmount() {
        return amount;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    
}